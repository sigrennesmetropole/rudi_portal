/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.facade.controller;

import java.util.List;

import javax.validation.Valid;

import org.rudi.bpmn.core.bean.ProcessDefinition;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.microservice.projekt.facade.controller.api.ProcessDefinitionsApi;
import org.springframework.core.io.Resource;
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
public class ProcessDefinitionController implements ProcessDefinitionsApi {

	private final InitializationService initializationService;

	private final ResourceHelper resourceHelper;

	@Override
	public ResponseEntity<Void> deleteProcessDefinition(String processDefinitionKey, @Valid Integer version)
			throws Exception {
		initializationService.deleteProcessDefinition(processDefinitionKey, version);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Override
	public ResponseEntity<Boolean> updateProcessDefinition(String deploymentName, @Valid Resource body)
			throws Exception {
		initializationService.updateProcessDefinition(deploymentName, resourceHelper.convertToDocumentContent(body));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
	}

	@Override
	public ResponseEntity<List<ProcessDefinition>> searchProcessDefinitions() throws Exception {
		return ResponseEntity.ok(initializationService.searchProcessDefinitions());
	}
}
