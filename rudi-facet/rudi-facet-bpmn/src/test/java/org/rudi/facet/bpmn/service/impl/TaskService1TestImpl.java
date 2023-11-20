/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.bean.AssetDescription1TestData;
import org.rudi.facet.bpmn.dao.workflow.AssetDescription1TestDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescription1TestWorkflowHelper;
import org.rudi.facet.bpmn.helper.workflow.Assigment1TestHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class TaskService1TestImpl extends
		AbstractTaskServiceImpl<AssetDescription1TestEntity, AssetDescription1TestData, AssetDescription1TestDao, AssetDescription1TestWorkflowHelper, Assigment1TestHelper> {

	private static final String FIELD_A = "A";

	public TaskService1TestImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			AssetDescription1TestDao assetDescriptionDao, AssetDescription1TestWorkflowHelper assetDescriptionHelper,
			Assigment1TestHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			AssetDescription1TestEntity assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

	@Override
	protected AbstractTaskServiceImpl<AssetDescription1TestEntity, AssetDescription1TestData, AssetDescription1TestDao, AssetDescription1TestWorkflowHelper, Assigment1TestHelper> lookupMe() {
		return ApplicationContext.getBean(TaskService1TestImpl.class);
	}

}
