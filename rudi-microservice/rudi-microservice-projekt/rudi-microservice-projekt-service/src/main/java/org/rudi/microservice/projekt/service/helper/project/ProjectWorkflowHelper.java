/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.project;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ProjectWorkflowHelper extends AbstactAssetDescriptionHelper<ProjectEntity, Project, ProjectMapper> {

	public ProjectWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper, BpmnHelper bpmnHelper,
			ProjectMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected ProjectEntity createAsset() {
		return new ProjectEntity();
	}

}
