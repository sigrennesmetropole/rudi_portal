package org.rudi.microservice.selfdata.facade.controller;

import java.util.List;


import javax.validation.Valid;

import bean.workflow.SelfdataTaskSearchCriteria;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.service.TaskQueryService;
import org.rudi.microservice.selfdata.facade.controller.api.TasksApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TasksController implements TasksApi {

	private final TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService;

	@Override
	public ResponseEntity<List<Task>> searchTasks(@Valid String title, @Valid String description, @Valid List<String> processDefinitionKeys,
			@Valid List<Status> status, @Valid List<String> fonctionalStatus, @Valid Boolean asAdmin) throws Exception {
		SelfdataTaskSearchCriteria searchCriteria = SelfdataTaskSearchCriteria.builder()
				.title(title)
				.description(description)
				.processDefinitionKeys(processDefinitionKeys)
				.functionalStatus(fonctionalStatus)
				.asAdmin(Boolean.TRUE.equals(asAdmin))
				.build();
		Page<Task> tasks = taskQueryService.searchTasks(searchCriteria, Pageable.unpaged());
		return ResponseEntity.ok(tasks.getContent());
	}

	@Override
	public ResponseEntity<Task> getTask(String taskId) throws Exception {
		return ResponseEntity.ok(taskQueryService.getTask(taskId));
	}
}
