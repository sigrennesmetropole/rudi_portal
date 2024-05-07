package org.rudi.microservice.projekt.service.helper.project.processor;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections.CollectionUtils;
import org.rudi.common.service.util.CollectionMerger;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.Support;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityDao;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.rudi.microservice.projekt.storage.dao.targetaudience.TargetAudienceDao;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeDao;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectTaskUpdateFieldsProcessor implements ProjectTaskUpdateProcessor {

	private final ProjectTypeDao projectTypeDao;
	private final TargetAudienceDao targetAudienceDao;
	private final TerritorialScaleDao territorialScaleDao;
	private final SupportDao supportDao;
	private final ConfidentialityDao confidentialityDao;
	private final ReutilisationStatusDao reutilisationStatusDao;
	private final CollectionMerger collectionMerger;

	/**
	 * @param project le projet DTO à sauvegarder
	 * @param projectEntity l'entité Project à mettre à jour par rapport au DTP
	 */
	@Override
	public void process(Project project, ProjectEntity projectEntity) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

		if(project.getType() != null){
			// Resultat non null, sinon exception lancée par le DAOs
			projectEntity.setType(projectTypeDao.findByUUID(project.getType().getUuid()));
		}

		if(CollectionUtils.isNotEmpty(project.getTargetAudiences())){
			collectionMerger.mergeCollection(projectEntity.getTargetAudiences(), project.getTargetAudiences(), TargetAudience.class, targetAudienceDao);
		}

		if(project.getTerritorialScale() != null){
			// Resultat non null, sinon exception lancée par le DAO
			projectEntity.setTerritorialScale(territorialScaleDao.findByUUID(project.getTerritorialScale().getUuid()));
		}

		if(CollectionUtils.isNotEmpty(project.getDesiredSupports())){
			collectionMerger.mergeCollection(projectEntity.getDesiredSupports(),project.getDesiredSupports(), Support.class, supportDao);
		}

		if(project.getConfidentiality() != null){
			// Resultat non null, sinon exception lancée par le DAO
			projectEntity.setConfidentiality(confidentialityDao.findByUUID(project.getConfidentiality().getUuid()));
		}

		// Est un champ requis, donc ne peut pas être null
		projectEntity.setReutilisationStatus(reutilisationStatusDao.findByUUID(project.getReutilisationStatus().getUuid()));

	}
}
