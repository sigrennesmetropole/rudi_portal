package org.rudi.facet.bpmn.service;

import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

/**
 * Listener pour la suppression des process defintion
 * 
 * @author FNI18300
 *
 */
public interface ProcessDefinitionActionListener {

	boolean acceptDeletion(ProcessDefinition processDefinition, ProcessDefinition latestProcessDefinition,
			List<Task> tasks);
}
