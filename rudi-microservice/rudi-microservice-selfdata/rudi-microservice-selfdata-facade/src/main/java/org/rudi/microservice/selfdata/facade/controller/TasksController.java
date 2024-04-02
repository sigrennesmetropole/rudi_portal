package org.rudi.microservice.selfdata.facade.controller;

import java.util.List;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.service.TaskQueryService;
import org.rudi.microservice.selfdata.facade.controller.api.TasksApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import bean.workflow.SelfdataTaskSearchCriteria;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TasksController implements TasksApi {

	private final TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService;

	@Override
	public ResponseEntity<List<Task>> searchTasks(String title, String description, List<String> processDefinitionKeys,
			List<Status> status, List<String> fonctionalStatus, Boolean asAdmin) throws Exception {
		SelfdataTaskSearchCriteria searchCriteria = SelfdataTaskSearchCriteria.builder().title(title)
				.description(description).processDefinitionKeys(processDefinitionKeys)
				.functionalStatus(fonctionalStatus).asAdmin(Boolean.TRUE.equals(asAdmin)).build();
		Page<Task> tasks = taskQueryService.searchTasks(searchCriteria, Pageable.unpaged());
		return ResponseEntity.ok(tasks.getContent());
	}

	@Override
	public ResponseEntity<Task> getTask(String taskId) throws Exception {
		return ResponseEntity.ok(taskQueryService.getTask(taskId));
	}
}
