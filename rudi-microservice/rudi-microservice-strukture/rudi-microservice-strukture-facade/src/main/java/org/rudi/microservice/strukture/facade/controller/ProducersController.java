package org.rudi.microservice.strukture.facade.controller;

import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.microservice.strukture.facade.controller.api.ProducersApi;
import org.rudi.microservice.strukture.service.producer.ProducerService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProducersController implements ProducersApi {

	private final ProducerService producerService;
	private final ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<Resource> downloadProducerMediaByType(UUID producerUuid, KindOfData kindOfData)
			throws Exception {
		final DocumentContent documentContent = producerService.downloadMedia(producerUuid, kindOfData);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}

	@Override
	public ResponseEntity<Void> uploadProducerMediaByType(UUID producerUuid, KindOfData kindOfData, MultipartFile file) throws Exception {
		DocumentContent documentContent = controllerHelper.documentContentFrom(file);

		producerService.uploadMedia(producerUuid, kindOfData, documentContent);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Void> deleteProducerMediaByType(UUID producerUuid, KindOfData kindOfData) throws Exception {
		producerService.deleteMedia(producerUuid, kindOfData);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
