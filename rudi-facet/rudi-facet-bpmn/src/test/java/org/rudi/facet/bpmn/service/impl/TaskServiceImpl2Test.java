/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.bean.AssetDescription2Test;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao2Test;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity2Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescriptionWorkflowHelper2Test;
import org.rudi.facet.bpmn.helper.workflow.AssigmentHelper2Test;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TaskServiceImpl2Test extends
		AbstractTaskServiceImpl<AssetDescriptionEntity2Test, AssetDescription2Test, AssetDescriptionDao2Test, AssetDescriptionWorkflowHelper2Test, AssigmentHelper2Test> {

	private static final String FIELD_A = "A";

	public TaskServiceImpl2Test(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			AssetDescriptionDao2Test assetDescriptionDao, AssetDescriptionWorkflowHelper2Test assetDescriptionHelper,
			AssigmentHelper2Test assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			AssetDescriptionEntity2Test assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

	@Override
	protected AbstractTaskServiceImpl<AssetDescriptionEntity2Test, AssetDescription2Test, AssetDescriptionDao2Test, AssetDescriptionWorkflowHelper2Test, AssigmentHelper2Test> lookupMe() {
		return ApplicationContext.getBean(TaskServiceImpl2Test.class);
	}

}
