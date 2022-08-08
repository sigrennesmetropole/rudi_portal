/**
 * 
 */
package org.rudi.facet.bpmn.service;

import java.util.List;

import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;

/**
 * Service de gestion des tâches
 * 
 * @param <D> le dto
 */
public interface TaskQueryService<S extends TaskSearchCriteria> {

	/**
	 * Recherche des tâches affectées à l'utilisateur courant
	 * 
	 * @param taskSearchCriteria
	 * @return
	 */
	List<Task> searchTasks(S taskSearchCriteria) throws InvalidDataException, FormDefinitionException;

	/**
	 * Retourne une tâche par son id
	 * 
	 * @param taskId
	 * @return
	 */
	Task getTask(String taskId) throws InvalidDataException, FormDefinitionException;

}
