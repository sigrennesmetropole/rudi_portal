package org.rudi.common.facade.helper;

import org.rudi.common.core.DocumentContent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;

@Component
public class ControllerHelper {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";

	public ResponseEntity<Resource> downloadableResponseEntity(@Nullable DocumentContent documentContent) throws FileNotFoundException {
		final HttpHeaders responseHeaders = new HttpHeaders();

		if (documentContent != null) {

			responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentContent.getFileName());
			responseHeaders.add(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());
			responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
			responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE);
			InputStreamResource inputStreamResource = new InputStreamResource(documentContent.getFileStream());

			return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
		}
	}
}
