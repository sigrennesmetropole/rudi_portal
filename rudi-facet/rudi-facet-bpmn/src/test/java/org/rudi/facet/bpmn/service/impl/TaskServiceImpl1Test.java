/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.bean.AssetDescription1Test;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao1Test;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity1Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescriptionWorkflowHelper1Test;
import org.rudi.facet.bpmn.helper.workflow.AssigmentHelper1Test;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class TaskServiceImpl1Test extends
		AbstractTaskServiceImpl<AssetDescriptionEntity1Test, AssetDescription1Test, AssetDescriptionDao1Test, AssetDescriptionWorkflowHelper1Test, AssigmentHelper1Test> {

	private static final String FIELD_A = "A";

	public TaskServiceImpl1Test(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			AssetDescriptionDao1Test assetDescriptionDao, AssetDescriptionWorkflowHelper1Test assetDescriptionHelper,
			AssigmentHelper1Test assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			AssetDescriptionEntity1Test assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

	@Override
	protected AbstractTaskServiceImpl<AssetDescriptionEntity1Test, AssetDescription1Test, AssetDescriptionDao1Test, AssetDescriptionWorkflowHelper1Test, AssigmentHelper1Test> lookupMe() {
		return ApplicationContext.getBean(TaskServiceImpl1Test.class);
	}

}
