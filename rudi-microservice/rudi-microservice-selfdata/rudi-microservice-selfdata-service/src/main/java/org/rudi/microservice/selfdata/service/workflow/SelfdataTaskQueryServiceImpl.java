package org.rudi.microservice.selfdata.service.workflow;

import bean.workflow.SelfdataTaskSearchCriteria;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.impl.AbstractTaskQueryServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SelfdataTaskQueryServiceImpl extends AbstractTaskQueryServiceImpl<SelfdataTaskSearchCriteria> {

	public SelfdataTaskQueryServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, ApplicationContext applicationContext) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, applicationContext);
	}

	@Override
	protected void applyExtentedCriteria(TaskQuery taskQuery, SelfdataTaskSearchCriteria taskSearchCriteria) {
		if (StringUtils.isNotEmpty(taskSearchCriteria.getTitle())) {
			taskQuery.processVariableValueLikeIgnoreCase(
					SelfdataWorkflowConstants.TITLE, taskSearchCriteria.getTitle());
		}
	}
}
