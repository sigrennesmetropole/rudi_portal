/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.bean.AssetDescription2TestData;
import org.rudi.facet.bpmn.dao.workflow.AssetDescription2TestDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription2TestEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescription2TestWorkflowHelper;
import org.rudi.facet.bpmn.helper.workflow.Assigment2TestHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TaskService2TestImpl extends
		AbstractTaskServiceImpl<AssetDescription2TestEntity, AssetDescription2TestData, AssetDescription2TestDao, AssetDescription2TestWorkflowHelper, Assigment2TestHelper> {

	private static final String FIELD_A = "A";

	public TaskService2TestImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			AssetDescription2TestDao assetDescriptionDao, AssetDescription2TestWorkflowHelper assetDescriptionHelper,
			Assigment2TestHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			AssetDescription2TestEntity assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

	@Override
	protected AbstractTaskServiceImpl<AssetDescription2TestEntity, AssetDescription2TestData, AssetDescription2TestDao, AssetDescription2TestWorkflowHelper, Assigment2TestHelper> lookupMe() {
		return ApplicationContext.getBean(TaskService2TestImpl.class);
	}
}
