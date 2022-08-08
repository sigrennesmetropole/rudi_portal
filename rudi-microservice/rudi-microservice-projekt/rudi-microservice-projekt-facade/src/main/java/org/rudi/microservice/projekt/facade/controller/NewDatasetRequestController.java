/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.facade.controller;

import javax.validation.Valid;

import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.facade.controller.api.NewDatasetRequestApi;
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
public class NewDatasetRequestController implements NewDatasetRequestApi {

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;

	@Override
	public ResponseEntity<Task> claimNewDatasetRequestTask(String taskId) throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> createNewDatasetRequestDraft(@Valid NewDatasetRequest project) throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.createDraft(project));
	}

	@Override
	public ResponseEntity<Void> doItNewDatasetRequest(String taskId, String actionName) throws Exception {
		newDatasetRequestTaskService.doIt(taskId, actionName);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Form> lookupNewDatasetRequestDraftForm() throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.lookupDraftForm());
	}

	@Override
	public ResponseEntity<Task> startNewDatasetRequestTask(Task task) throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.startTask(task));
	}

	@Override
	public ResponseEntity<Task> unclaimNewDatasetRequestTask(String taskId) throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> updateNewDatasetRequestTask(Task task) throws Exception {
		return ResponseEntity.ok(newDatasetRequestTaskService.updateTask(task));
	}

}
