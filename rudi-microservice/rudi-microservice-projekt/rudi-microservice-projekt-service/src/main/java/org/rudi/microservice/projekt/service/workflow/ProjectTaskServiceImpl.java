/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.workflow;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.helper.ProjektAuthorisationHelper;
import org.rudi.microservice.projekt.service.helper.project.ProjectAssigmentHelper;
import org.rudi.microservice.projekt.service.helper.project.ProjectWorkflowHelper;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class ProjectTaskServiceImpl extends
		AbstractTaskServiceImpl<ProjectEntity, Project, ProjectDao, ProjectWorkflowHelper, ProjectAssigmentHelper> {

	@Value("${rudi.project.task.allowed.status.administrator}")
	private List<String> administratorAllowedModificationStatus;

	@Value("${rudi.project.task.allowed.status.owner}")
	private List<String> projectOwnerAllowedModificationStatus;

	@Autowired
	private ProjektAuthorisationHelper projektAuthorisationHelper;

	public ProjectTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			ProjectDao assetDescriptionDao, ProjectWorkflowHelper assetDescriptionHelper,
			ProjectAssigmentHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables, ProjectEntity assetDescriptionEntity) {
		variables.put(ProjektWorkflowConstants.TITLE, assetDescriptionEntity.getTitle());
		if (assetDescriptionEntity.getProjectStatus() != null) {
			variables.put(ProjektWorkflowConstants.PROJECT_STATUS, assetDescriptionEntity.getStatus().name());
		}
	}

	@Override
	public String getProcessDefinitionKey() {
		return "project-process";
	}

	@Override
	@PostConstruct
	public void loadBpmn() throws IOException {
		super.loadBpmn();
	}

	@Override
	protected AbstractTaskServiceImpl<ProjectEntity, Project, ProjectDao, ProjectWorkflowHelper, ProjectAssigmentHelper> lookupMe() {
		return ApplicationContext.getBean(ProjectTaskServiceImpl.class);
	}

	/**
	 * @param assetDescriptionEntity
	 */
	@Override
	protected boolean checkUpdate(ProjectEntity assetDescriptionEntity) {
		boolean result = super.checkUpdate(assetDescriptionEntity);
		List<String> allowedStatus = isCurrentAdmin() ? administratorAllowedModificationStatus
				: projectOwnerAllowedModificationStatus;

		return allowedStatus.contains(assetDescriptionEntity.getProjectStatus().name()) && result;
	}

	@Override
	protected void checkRightsOnInitEntity(ProjectEntity assetDescriptionEntity) throws IllegalArgumentException {
		try {
			projektAuthorisationHelper.checkRightsInitProject(assetDescriptionEntity);
		} catch (GetOrganizationMembersException | MissingParameterException | AppServiceUnauthorizedException e) {
			throw new IllegalArgumentException(
					"Erreur lors de la vérification des droits pour le traitement de la tache de projet", e);
		}
	}
}
