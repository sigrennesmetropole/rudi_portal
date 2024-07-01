/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.project;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.helper.project.processor.ProjectTaskUpdateProcessor;
import org.rudi.microservice.projekt.service.helper.project.validator.ProjectValidator;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ProjectWorkflowHelper extends AbstactAssetDescriptionHelper<ProjectEntity, Project, ProjectMapper> {

	private final List<ProjectValidator> projectValidators;
	private final List<ProjectTaskUpdateProcessor> projectTaskUpdateProcessors;

	public ProjectWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper, BpmnHelper bpmnHelper,
			ProjectMapper assetDescriptionMapper, List<ProjectValidator> projectValidators, List<ProjectTaskUpdateProcessor> projectTaskUpdateProcessors) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
		this.projectValidators = projectValidators;
		this.projectTaskUpdateProcessors = projectTaskUpdateProcessors;
	}

	@Override
	protected ProjectEntity createAsset() {
		return new ProjectEntity();
	}

	/**
	 * @param assetDescription
	 * @param assetDescriptionEntity
	 */
	@Override
	public void updateAssetEntity(Project assetDescription, ProjectEntity assetDescriptionEntity)  throws InvalidDataException {
		projectValidators.forEach(validator -> validator.validate(assetDescription));

		super.updateAssetEntity(assetDescription, assetDescriptionEntity);

		for (ProjectTaskUpdateProcessor projectTaskUpdateProcessor : projectTaskUpdateProcessors) {
			try {
				projectTaskUpdateProcessor.process(assetDescription, assetDescriptionEntity);
			} catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException e){
				throw new InvalidDataException("Données invalides lors de la mise à jour d'un projet.");
			}
		}
	}
}
