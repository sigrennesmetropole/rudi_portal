package org.rudi.common.facade.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.rudi.common.core.DocumentContent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.val;

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

	@Nonnull
	public DocumentContent documentContentFrom(MultipartFile file) throws IOException {
		return new DocumentContent(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
	}

	@Nonnull
	public ResponseEntity<UUID> uploadResponseEntity(UUID uuid) {
		val location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{uuid}")
				.buildAndExpand(uuid)
				.toUri();
		return ResponseEntity.created(location).body(uuid);
	}

}
