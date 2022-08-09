/**
 * 
 */
package org.rudi.facet.bpmn.service;

import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	 * @param pageable
	 * @return
	 * @throws InvalidDataException
	 * @throws FormDefinitionException
	 */
	Page<Task> searchTasks(S taskSearchCriteria, Pageable pageable)
			throws InvalidDataException, FormDefinitionException;

	/**
	 * Retourne une tâche par son id
	 * 
	 * @param taskId
	 * @return
	 */
	Task getTask(String taskId) throws InvalidDataException, FormDefinitionException;

}
