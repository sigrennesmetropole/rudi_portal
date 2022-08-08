/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.facade.controller;

import javax.validation.Valid;

import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.facade.controller.api.LinkedDatasetApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@RestController
@RequiredArgsConstructor
public class LinkedDatasetController implements LinkedDatasetApi {

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	@Override
	public ResponseEntity<Task> claimLinkedDatasetTask(String taskId) throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> createLinkedDatasetDraft(@Valid LinkedDataset project) throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.createDraft(project));
	}

	@Override
	public ResponseEntity<Void> doItLinkedDataset(String taskId, String actionName) throws Exception {
		linkedDatasetTaskService.doIt(taskId, actionName);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Form> lookupLinkedDatasetDraftForm() throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.lookupDraftForm());
	}

	@Override
	public ResponseEntity<Task> startLinkedDatasetTask(Task task) throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.startTask(task));
	}

	@Override
	public ResponseEntity<Task> unclaimLinkedDatasetTask(String taskId) throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> updateLinkedDatasetTask(Task task) throws Exception {
		return ResponseEntity.ok(linkedDatasetTaskService.updateTask(task));
	}

}
