/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.rudi.facet.bpmn.service.ProcessDefinitionActionListener;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Test1ProcessDescriptionActionListener implements ProcessDefinitionActionListener {

	@Override
	public boolean acceptDeletion(ProcessDefinition processDefinition, ProcessDefinition latestProcessDefinition,
			List<Task> tasks) {
		return true;
	}

}
