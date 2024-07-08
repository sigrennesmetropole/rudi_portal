/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.controller;

import java.io.IOException;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.tools.nodestub.controller.api.EndpointsApi;
import org.rudi.tools.nodestub.service.EndpointService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
public class EndpointsApiController implements EndpointsApi {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";

	private final EndpointService endpointService;

	@Override
	public ResponseEntity<Resource> callEndpoint(UUID mediaUuid) throws Exception {
		DocumentContent result = endpointService.callEndpoint(mediaUuid);
		return downloadDocumentContent(result);
	}

	protected ResponseEntity<Resource> downloadDocumentContent(DocumentContent documentContent) {
		ResponseEntity<Resource> result = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentContent.getFileName());
		responseHeaders.add(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());
		responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE);
		try {
			if (documentContent.isFile()) {
				FileSystemResource fileSystemResource = new FileSystemResource(documentContent.getFile());
				result = new ResponseEntity<>(fileSystemResource, responseHeaders, HttpStatus.OK);
			} else if (documentContent.isStream()) {
				InputStreamResource inputStreamResource = new InputStreamResource(documentContent.getFileStream());
				result = new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
			} else if (documentContent.isURL()) {
				throw new UnsupportedOperationException("URL not supported");
			}
		} catch (IOException e) {
			ByteArrayResource byteArrayResource = new ByteArrayResource("Invalid document".getBytes());
			result = new ResponseEntity<>(byteArrayResource, responseHeaders, HttpStatus.OK);
		}
		return result;
	}
}
