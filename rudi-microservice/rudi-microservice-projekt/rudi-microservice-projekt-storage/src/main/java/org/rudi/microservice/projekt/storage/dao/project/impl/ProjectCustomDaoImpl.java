package org.rudi.microservice.projekt.storage.dao.project.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.common.storage.dao.PredicateListBuilder;
import org.rudi.microservice.projekt.core.bean.ComputeIndicatorsSearchCriteria;
import org.rudi.microservice.projekt.core.bean.Indicators;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;

@Repository
public class ProjectCustomDaoImpl extends AbstractCustomDaoImpl<ProjectEntity, ProjectSearchCriteria>
		implements ProjectCustomDao {

	private static final String FIELD_PROJECT_STATUS = "projectStatus";
	private static final String FIELD_OWNER_UUID = ProjectEntity.FIELD_OWNER_UUID;
	private static final String FIELD_KEYWORDS = "keywords";
	private static final String FIELD_TARGET_AUDIENCES = "targetAudiences";
	private static final String FIELD_LINKED_DATASETS = "linkedDatasets";
	private static final String FIELD_DATASET_REQUESTS = "datasetRequests";
	private static final String FIELD_DATASET_UUID = "datasetUuid";
	private static final String FIELD_UUID = "uuid";
	private static final String FIELD_PRODUCER_UUID = "datasetOrganisationUuid";
	private static final String FIELD_DATASET_CONFIDENTIALITY = "datasetConfidentiality";

	public ProjectCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ProjectEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<ProjectEntity> searchProjects(ProjectSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(PredicateListBuilder<ProjectEntity, ProjectSearchCriteria> builder) {
		val searchCriteria = builder.getSearchCriteria();
		builder.add(searchCriteria.getKeywords(), (project, keywords) -> project.join(FIELD_KEYWORDS).in(keywords))
				.add(searchCriteria.getTargetAudiences(),
						(project, targetAudiences) -> project.join(FIELD_TARGET_AUDIENCES).in(targetAudiences))
				.add(searchCriteria.getThemes(), (project, themes) -> project.join("themes").in(themes))
				.add(searchCriteria.getDatasetUuids(),
						(project, linkedDatasetsUuids) -> project.join(FIELD_LINKED_DATASETS).get(FIELD_DATASET_UUID)
								.in(linkedDatasetsUuids))
				.add(searchCriteria.getLinkedDatasetUuids(),
						(project, linkedDatasetEntityUuids) -> project.join(FIELD_LINKED_DATASETS).get(FIELD_UUID)
								.in(linkedDatasetEntityUuids))
				.add(searchCriteria.getOwnerUuids(),
						(project, ownerUuids) -> project.get(FIELD_OWNER_UUID).in(ownerUuids))
				.add(searchCriteria.getStatus(), ProjectStatus::valueOf,
						(project, status) -> project.get(FIELD_PROJECT_STATUS).in(status));
	}

	@Override
	public ProjectEntity findProjectByNewDatasetRequestUuid(UUID newDatasetRequestUuid) {
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);

		Join<ProjectEntity, NewDatasetRequestEntity> joinNewDatasetRequest = searchRoot.join(FIELD_DATASET_REQUESTS);

		searchQuery.where(builder.equal(joinNewDatasetRequest.get(FIELD_UUID), newDatasetRequestUuid));

		TypedQuery<ProjectEntity> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getSingleResult();
	}

	@Override
	public ProjectEntity findProjectByLinkedDatasetUuid(UUID linkedDatasetUuid) {
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);

		Join<ProjectEntity, LinkedDatasetEntity> joinNewDatasetRequest = searchRoot.join(FIELD_LINKED_DATASETS);

		searchQuery.where(builder.equal(joinNewDatasetRequest.get(FIELD_UUID), linkedDatasetUuid));

		TypedQuery<ProjectEntity> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getSingleResult();
	}

	@Override
	public Indicators computeProjectInfos(ComputeIndicatorsSearchCriteria searchCriteria) {
		val builder = entityManager.getCriteriaBuilder();
		val countQuery = builder.createQuery(Long.class);
		val countRoot = countQuery.from(entitiesClass);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(countRoot.get(FIELD_UUID), searchCriteria.getProjectUuid()));
		final Join<ProjectEntity, LinkedDatasetEntity> join = countRoot.join(FIELD_LINKED_DATASETS);
		//Seulement les jdds restreints demandés sont à récuperer, les jdds ouverts n'étant pas des demandes au sen fonctionnel
		predicates.add(builder.equal(join.get(FIELD_DATASET_CONFIDENTIALITY), DatasetConfidentiality.RESTRICTED));
		if(searchCriteria.getExcludedProducerUuid() != null) {
			predicates.add(builder.notEqual(join.get(FIELD_PRODUCER_UUID), searchCriteria.getExcludedProducerUuid()));
		}
		countQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		countQuery.select(builder.countDistinct(join));
		Long numberOfRequest = entityManager.createQuery(countQuery).getSingleResult();

		countQuery.select(builder.countDistinct(join.get(FIELD_PRODUCER_UUID)));
		Long numberOfProducer = entityManager.createQuery(countQuery).getSingleResult();

		Indicators results = new Indicators();
		results.setNumberOfRequest(numberOfRequest.intValue());
		results.setNumberOfProducer(numberOfProducer.intValue());
		return results;
	}

	@Override
	public Integer getNumberOfLinkedDatasets(UUID projectUuid){ //etendre en 2 méthodes
		return queryLauncher(projectUuid, FIELD_LINKED_DATASETS).intValue();
	}

	@Override
	public Integer getNumberOfNewRequests(UUID projectUuid){ //etendre en 2 méthodes
		return queryLauncher(projectUuid, FIELD_DATASET_REQUESTS).intValue();
	}

	private<T> Long queryLauncher(UUID projectUuid, String fieldToJoin) {
		val builder = entityManager.getCriteriaBuilder();
		val countQuery = builder.createQuery(Long.class);
		val countRoot = countQuery.from(entitiesClass);
		//Parametrer avec la classe
		final Join<ProjectEntity, T> join = countRoot.join(fieldToJoin);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(countRoot.get(FIELD_UUID), projectUuid));
		countQuery.where(builder.and(predicates.toArray(Predicate[]::new)));

		countQuery.select(builder.countDistinct(join));
		return entityManager.createQuery(countQuery).getSingleResult();
	}
}
