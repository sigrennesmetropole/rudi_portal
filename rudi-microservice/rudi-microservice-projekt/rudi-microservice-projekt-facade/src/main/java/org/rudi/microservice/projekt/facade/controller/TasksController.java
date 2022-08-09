/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.facade.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.service.TaskQueryService;
import org.rudi.microservice.projekt.core.bean.ProjectStatus;
import org.rudi.microservice.projekt.core.bean.workflow.ProjektTaskSearchCriteria;
import org.rudi.microservice.projekt.facade.controller.api.TasksApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@RestController
@RequiredArgsConstructor
public class TasksController implements TasksApi {

	private final TaskQueryService<ProjektTaskSearchCriteria> taskQueryService;

	@Override
	public ResponseEntity<List<Task>> searchTasks(@Valid String title, @Valid String description,
			@Valid List<String> processDefinitionKeys, @Valid List<Status> status, @Valid List<String> fonctionalStatus,
			@Valid ProjectStatus projectStatus, @Valid Boolean asAdmin, @Valid UUID datasetProducerUuid,
			@Valid UUID projectUuid) throws Exception {
		ProjektTaskSearchCriteria searchCriteria = ProjektTaskSearchCriteria.builder().title(title)
				.description(description).processDefinitionKeys(processDefinitionKeys)
				.functionalStatus(fonctionalStatus).projectStatus(projectStatus).status(status)
				.asAdmin(Boolean.TRUE.equals(asAdmin)).datasetProducerUuid(datasetProducerUuid).projectUuid(projectUuid)
				.build();
		Page<Task> tasks = taskQueryService.searchTasks(searchCriteria, Pageable.unpaged());
		return ResponseEntity.ok(tasks.getContent());
	}

	@Override
	public ResponseEntity<Task> getTask(String taskId) throws Exception {
		return ResponseEntity.ok(taskQueryService.getTask(taskId));
	}

}
