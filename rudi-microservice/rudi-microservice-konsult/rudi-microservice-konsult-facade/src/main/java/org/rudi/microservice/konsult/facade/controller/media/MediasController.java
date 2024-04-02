package org.rudi.microservice.konsult.facade.controller.media;

import java.util.UUID;

import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.microservice.konsult.facade.controller.api.MediasApi;
import org.rudi.microservice.konsult.service.helper.APIManagerHelper;
import org.rudi.microservice.konsult.service.metadata.MetadataService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.ANONYMOUS;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class MediasController implements MediasApi {

	private final MetadataService metadataService;
	private final ControllerHelper controllerHelper;
	private final APIManagerHelper apiManagerHelper;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + USER + ", " + ANONYMOUS + ")")
	public ResponseEntity<Resource> downloadMedia(UUID mediaId, String interfaceContract, String version) throws Exception {
		final UUID metadataGlobalId = getMetadataGlobalIdFromMediaId(mediaId);
		final var documentContent = metadataService.downloadMetadataMedia(metadataGlobalId, mediaId);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}

	private UUID getMetadataGlobalIdFromMediaId(UUID mediaId) throws APIManagerException {
		return apiManagerHelper.getGlobalIdFromMediaId(mediaId);
	}
}
