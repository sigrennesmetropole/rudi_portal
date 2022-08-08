/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.Test2AssetDescription;
import org.rudi.facet.bpmn.dao.workflow.Test2AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.Test2AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.helper.workflow.Test2AssetDescriptionWorkflowHelper;
import org.rudi.facet.bpmn.helper.workflow.Test2AssigmentHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class Test2TaskServiceImpl extends
		AbstractTaskServiceImpl<Test2AssetDescriptionEntity, Test2AssetDescription, Test2AssetDescriptionDao, Test2AssetDescriptionWorkflowHelper, Test2AssigmentHelper> {

	private static final String FIELD_A = "A";

	public Test2TaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			Test2AssetDescriptionDao assetDescriptionDao, Test2AssetDescriptionWorkflowHelper assetDescriptionHelper,
			Test2AssigmentHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			Test2AssetDescriptionEntity assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

}
