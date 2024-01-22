package org.rudi.microservice.strukture.service.organization.bean.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.MetadataFacet;
import org.rudi.facet.kaccess.bean.MetadataFacetValues;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.strukture.core.bean.OrganizationBean;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.mapper.OrganizationBeanMapper;
import org.rudi.microservice.strukture.service.organization.bean.OrganizationBeanService;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationCustomDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrganizationBeanServiceImpl implements OrganizationBeanService {

	private static final String ORGANIZATION_UUID_FACET = "producer_organization_id";

	private final OrganizationCustomDao organizationCustomDao;
	private final OrganizationBeanMapper organizationBeanMapper;
	private final DatasetService datasetService;
	private final ProjektHelper projektHelper;

	@Override
	public Page<OrganizationBean> searchOrganizationBeans(OrganizationSearchCriteria criteria, Pageable pageable) {
		Page<OrganizationBean> beans = organizationBeanMapper.entitiesToDto(organizationCustomDao.searchOrganizations(criteria, pageable), pageable);

		if(Boolean.TRUE.equals(criteria.getLoadAllInformations())){
			// Récupération de la liste des UUID d'organisation pour récupérer
			//  leur nombre de jdd et leur nombre de projets
			List<UUID> organizationsUuids= beans.map(OrganizationBean::getUuid).toList();

			// Récupération du nombre de projets ayant pour owner une des organisations récupérées précédemment.
			List<ProjectByOwner> projectByOwners = projektHelper.getNumberOfProjectsPerOwners(organizationsUuids);

			// Récupération du nombre de JDD par producteur (organisations)
			//  indépendamment de la liste des organisations précédemment récupérées
			MetadataListFacets metadataListFacets = null;
			try {
				DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
						.producerUuids(organizationsUuids)
						.limit(null);

				// Liste des facets dataverse sur lesquels on souhaite s'appuyer
				//  ici, celle des organisations productrice de jdd.
				List<String> facets = new ArrayList<>();
				facets.add(OrganizationBeanServiceImpl.ORGANIZATION_UUID_FACET);

				// Récupération d'une liste d'organisations ayant produit des JDD
				metadataListFacets = datasetService.searchDatasets(datasetSearchCriteria, facets);
			} catch (DataverseAPIException e) {
				log.error("Cannot get datasets from Organizations", e);
			}

			for(OrganizationBean bean : beans){
				assignProjectCount(bean, projectByOwners);

				assignDatasetCount(bean, metadataListFacets);
			}
		}
		return beans;
	}

	private void assignDatasetCount(OrganizationBean bean, MetadataListFacets metadataListFacets){
		// Stockage du nombre de JDD, par défaut 0
		int datasetCount = 0;

		// Vérifie si le retour contient bien des données
		if(metadataListFacets != null
				&& metadataListFacets.getFacets() != null
				&& CollectionUtils.isNotEmpty(metadataListFacets.getFacets().getItems())) {

			// Récupère les données de la facet dataset sur les organisations ayant produit des données
			// C'est à dire une liste mettant en corélation des organizationUuid
			//  et le nombre de jdd produit par ces organisations.
			Optional<MetadataFacet> targettedFacet = metadataListFacets
					.getFacets()
					.getItems()
					.stream()
					.filter(f -> f.getPropertyName().equals(OrganizationBeanServiceImpl.ORGANIZATION_UUID_FACET)).findFirst();

			if(targettedFacet.isPresent()){

				// Si l'oganisation est présente dans la liste c'est qu'elle a produit au moins un jdd
				//  alors, on récupère le nombre de jdd produit par cette organisation.
				Optional<MetadataFacetValues> metadataFacetValue = targettedFacet
						.get()
						.getValues()
						.stream()
						.filter(v -> v.getValue().equals(bean.getUuid().toString())).findFirst();

				if (metadataFacetValue.isPresent()){
					datasetCount = metadataFacetValue.get().getCount();

				}
			}
		}
		bean.setDatasetCount(datasetCount);
	}

	private void assignProjectCount(OrganizationBean bean, List<ProjectByOwner> projectByOwners){
		//Stockage du nombre de projets
		Optional<ProjectByOwner> projectByOwner = projectByOwners
				.stream()
				.filter(p -> p.getOwnerUUID().equals(bean.getUuid())).findFirst();

		bean.setDatasetCount(projectByOwner.map(byOwner -> byOwner.getProjectCount().intValue()).orElse(0));
	}


}
