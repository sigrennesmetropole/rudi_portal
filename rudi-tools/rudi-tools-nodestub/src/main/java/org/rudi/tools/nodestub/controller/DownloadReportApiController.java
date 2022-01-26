/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.controller;

import org.apache.commons.io.FileUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.controller.api.DownloadReportApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.UUID;

/**
 * @author FNI18300
 *
 */
@RestController
public class DownloadReportApiController implements DownloadReportApi {

	private static final String APPLICATION_JSON_MIME_TYPE = "application/json";

	private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";

	private static final String ERROR_CONTENT = "Pas de rapport.";

	private static final String ERROR_CONTENT_MIME_TYPE = "text/plain";

	private static final String TEMP_FILE_EXTENSION = ".file";

	private static final String TEMP_FILE_PREFIX = "upload";

	private final NodeStubConfiguration nodeStubConfiguration;

	public DownloadReportApiController(NodeStubConfiguration nodeStubConfiguration) {
		this.nodeStubConfiguration = nodeStubConfiguration;
	}

	@Override
	public ResponseEntity<Resource> downloadReport(UUID uuid) throws Exception {
		String fileName = uuid + ".rpt";
		File reportFile = new File(nodeStubConfiguration.getReportsDirectory(), fileName);
		DocumentContent documentContent = null;
		if (reportFile.exists()) {
			documentContent = new DocumentContent(fileName, APPLICATION_JSON_MIME_TYPE, reportFile);
		} else {
			ByteArrayInputStream bais = new ByteArrayInputStream(ERROR_CONTENT.getBytes());
			documentContent = new DocumentContent(fileName, ERROR_CONTENT_MIME_TYPE, ERROR_CONTENT.length(), bais);
		}
		return downloadDocumentContent(documentContent);
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

	protected DocumentContent convertResource(Resource body, boolean asFile) throws IOException {
		DocumentContent documentContent = null;
		String fileName = body.getFilename();
		String mimeType = DEFAULT_MIME_TYPE;
		if (fileName != null) {
			mimeType = URLConnection.guessContentTypeFromName(body.getFilename());
		} else {
			fileName = "unknown";
		}
		if (body.isFile()) {
			documentContent = new DocumentContent(fileName, mimeType, body.getFile());
		} else if (!asFile) {
			documentContent = new DocumentContent(fileName, mimeType, body.contentLength(), body.getInputStream());
		} else {
			File tmpFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_EXTENSION);
			FileUtils.copyInputStreamToFile(body.getInputStream(), tmpFile);
			documentContent = new DocumentContent(fileName, mimeType, tmpFile);
		}
		return documentContent;
	}
}
