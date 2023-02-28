package org.rudi.tools.nodestub.controller;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.microservice.kalim.core.bean.Report;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.controller.api.ResourcesApi;
import org.rudi.tools.nodestub.service.ResourcesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ResourcesApiController implements ResourcesApi {

	public static final int DEFAULT_LIMIT = 50;
	public static final int DEFAULT_OFFSET = 0;

	private final NodeStubConfiguration nodeStubConfiguration;
	private final ObjectMapper mapper;
	private final ResourcesService resourcesService;

	@Override
	public ResponseEntity<Metadata> getRessource(UUID uuid) {
		final File file = getResourceFile(uuid);
		if (file.isFile()) {
			try {
				final Metadata metadata = mapper.readValue(file, Metadata.class);
				return ResponseEntity.ok(metadata);
			} catch (Exception e) {
				log.error("Invalid ressources:{}", uuid);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Override
	public ResponseEntity<MetadataList> getRessources(@Valid Integer incomingLimit, @Valid Integer incomingOffset,
			@Valid OffsetDateTime updatedAfter, @Valid OffsetDateTime updateBefore) {

		final int limit;
		if (incomingLimit == null || incomingLimit < 0) {
			limit = DEFAULT_LIMIT;
		} else {
			limit = incomingLimit;
		}

		final int offset;
		if (incomingOffset == null || incomingOffset < 0) {
			offset = DEFAULT_OFFSET;
		} else {
			offset = incomingOffset;
		}

		final List<Metadata> metadatas = resourcesService.getMetadataList(limit, offset, updatedAfter);

		return ResponseEntity.ok(
				new MetadataList()
						.total((long) metadatas.size())
						.offset((long) offset)
						.items(metadatas)
		);
	}

	@Override
	public ResponseEntity<Void> sendResourceReport(UUID uuid, @Valid Report report) throws IOException {
		final File reportFile = new File(nodeStubConfiguration.getReportsDirectory(), nodeStubConfiguration.getReportsNameFormat().format(new Object[]{ uuid }));
		if (checkParentDirectoryExistsOrCreate(reportFile)) {
			mapper.writeValue(reportFile, report);
			if (nodeStubConfiguration.getErrors429().contains(uuid.toString())) {
				return ResponseEntity.status(429).build();
			} else {
				return ResponseEntity.ok().build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private boolean checkParentDirectoryExistsOrCreate(File file) {
		final File parentDirectory = file.getParentFile();
		if (parentDirectory.exists()) {
			if (!parentDirectory.isDirectory()) {
				log.error("{} exists but should be a directory", parentDirectory);
				return false;
			}
		} else {
			if (!parentDirectory.mkdirs()) {
				log.error("Cannot create directory {}", parentDirectory);
				return false;
			}
		}

		return true;
	}

	@Override
	public ResponseEntity<Void> uploadResource(Metadata metadata) throws IOException {
		final UUID uuid = metadata.getGlobalId();
		final File resourceFile = getResourceFile(uuid);
		if (checkParentDirectoryExistsOrCreate(resourceFile)) {
			mapper.writeValue(resourceFile, metadata);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Nonnull
	private File getResourceFile(UUID uuid) {
		return new File(nodeStubConfiguration.getResourcesDirectory(), nodeStubConfiguration.getResourcesNameFormat().format(new Object[]{ uuid }));
	}
}
