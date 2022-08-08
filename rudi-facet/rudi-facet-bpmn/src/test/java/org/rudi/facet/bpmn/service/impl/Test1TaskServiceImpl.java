/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.Test1AssetDescription;
import org.rudi.facet.bpmn.dao.workflow.Test1AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.Test1AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.helper.workflow.Test1AssetDescriptionWorkflowHelper;
import org.rudi.facet.bpmn.helper.workflow.Test1AssigmentHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class Test1TaskServiceImpl extends
		AbstractTaskServiceImpl<Test1AssetDescriptionEntity, Test1AssetDescription, Test1AssetDescriptionDao, Test1AssetDescriptionWorkflowHelper, Test1AssigmentHelper> {

	private static final String FIELD_A = "A";

	public Test1TaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			Test1AssetDescriptionDao assetDescriptionDao, Test1AssetDescriptionWorkflowHelper assetDescriptionHelper,
			Test1AssigmentHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables,
			Test1AssetDescriptionEntity assetDescriptionEntity) {
		variables.put(FIELD_A, assetDescriptionEntity.getA());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "test";
	}

}
