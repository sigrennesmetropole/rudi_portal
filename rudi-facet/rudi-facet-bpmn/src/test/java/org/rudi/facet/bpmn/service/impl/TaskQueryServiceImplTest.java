/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria1Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class TaskQueryServiceImplTest extends AbstractTaskQueryServiceImpl<TaskSearchCriteria1Test> {

	private static final String FIELD_A = "A";

	public TaskQueryServiceImplTest(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, ApplicationContext applicationContext) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, applicationContext);
	}

	@Override
	protected void applyExtentedCriteria(TaskQuery taskQuery, TaskSearchCriteria1Test taskSearchCriteria) {
		if (StringUtils.isNotEmpty(taskSearchCriteria.getA())) {
			taskQuery.processVariableValueEquals(FIELD_A, taskSearchCriteria.getA());
		}
	}

}
