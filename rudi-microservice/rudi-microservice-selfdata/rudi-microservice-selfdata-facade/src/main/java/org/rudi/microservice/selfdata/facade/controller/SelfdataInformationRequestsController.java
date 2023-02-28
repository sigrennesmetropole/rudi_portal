package org.rudi.microservice.selfdata.facade.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.selfdata.core.bean.MatchingData;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataInformationRequestList;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequestSearchCriteria;
import org.rudi.microservice.selfdata.facade.controller.api.InformationRequestsApi;
import org.rudi.microservice.selfdata.service.selfdata.SelfdataService;
import org.rudi.microservice.selfdata.service.selfdata.workflow.SelfdataInformationRequestTaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class SelfdataInformationRequestsController implements InformationRequestsApi {

	private final SelfdataInformationRequestTaskService selfdataInformationRequestTaskService;
	private final SelfdataService selfdataService;
	private final UtilPageable utilPageable;

	@Override
	public ResponseEntity<Form> lookupSelfdataInformationRequestDraftForm(UUID datasetUuid, String language)
			throws Exception {
		return ResponseEntity.ok(
				selfdataInformationRequestTaskService.lookupDraftFormWithSelfdata(datasetUuid, Optional.of(language)));
	}

	@Override
	public ResponseEntity<Task> createSelfdataInformationRequestDraft(
			@Valid SelfdataInformationRequest informationRequest) throws Exception {
		return ResponseEntity.ok(selfdataInformationRequestTaskService.createDraft(informationRequest));
	}

	@Override
	public ResponseEntity<Task> startSelfdataInformationRequestTask(Task task) throws Exception {
		return ResponseEntity.ok(selfdataInformationRequestTaskService.startTask(task));
	}

	@Override
	public ResponseEntity<Task> claimSelfdataInformationRequestTask(String taskId) throws Exception {
		return ResponseEntity.ok(selfdataInformationRequestTaskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> unclaimSelfdataInformationRequestTask(String taskId) throws Exception {
		selfdataInformationRequestTaskService.unclaimTask(taskId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Void> doItSelfdataInformationRequest(String taskId, String actionName) throws Exception {
		selfdataInformationRequestTaskService.doIt(taskId, actionName);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Void> deleteSelfdataInformationRequest(String uuid) throws Exception {
		this.selfdataService.deleteSelfdataInformationRequest(UUID.fromString(uuid));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Form> lookupFilledMatchingDataForm(String taskId) throws Exception {
		return ResponseEntity.ok(selfdataInformationRequestTaskService.lookupFilledMatchingDataForm(taskId));
	}

	@Override
	public ResponseEntity<Task> updateSelfdataInformationRequestTask(Task task) throws Exception {
		return ResponseEntity.ok(selfdataInformationRequestTaskService.updateTask(task));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + USER + ")")
	public ResponseEntity<PagedSelfdataInformationRequestList> searchMySelfdataInformationRequests(
			SelfdataInformationRequestSearchCriteria criteria) throws Exception {
		Pageable pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());
		return ResponseEntity.ok(selfdataService.searchMySelfdataInformationRequests(criteria, pageable));
	}

	@Override
	public ResponseEntity<List<MatchingData>> getMySelfdataInformationRequestMatchingData(UUID datasetUuid) throws Exception {
		return ResponseEntity.ok(selfdataService.getMySelfdataInformationRequestMatchingData(datasetUuid));

	}
}
