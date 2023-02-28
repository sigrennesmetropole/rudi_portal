package org.rudi.microservice.selfdata.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.doks.core.bean.DocumentMetadata;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.doks.exceptions.DocumentNotFoundException;
import org.rudi.facet.doks.helper.DocumentContentHelper;
import org.rudi.facet.doks.helper.DocumentMetadataHelper;
import org.rudi.microservice.selfdata.facade.controller.api.AttachmentsApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODERATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@Controller
@RequiredArgsConstructor
public class AttachmentsController implements AttachmentsApi {
	private final ControllerHelper controllerHelper;
	private final DocumentContentHelper documentContentHelper;
	private final AttachmentAuthorizationPolicy authorizationPolicy;
	private final ACLHelper aclHelper;
	private final DocumentMetadataHelper documentMetadataHelper;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODERATOR + "," + USER + ")")
	public ResponseEntity<UUID> uploadAttachment(MultipartFile file) throws Exception {
		final var authenticatedUserUuid = aclHelper.getAuthenticatedUserUuid();
		val documentContent = controllerHelper.documentContentFrom(file);
		val uuid = documentContentHelper.createDocumentContent(documentContent, true, authenticatedUserUuid);
		return controllerHelper.uploadResponseEntity(uuid);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODERATOR + "," + USER + ")")
	public ResponseEntity<Resource> downloadAttachment(UUID attachmentUuid) throws Exception {
		final var documentContent = documentContentHelper.getDocumentContent(attachmentUuid, authorizationPolicy);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODERATOR + "," + USER + ")")
	public ResponseEntity<Void> deleteAttachment(UUID attachmentUuid) throws Exception {
		documentContentHelper.deleteAttachment(attachmentUuid, authorizationPolicy);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODERATOR + "," + USER + ")")
	public ResponseEntity<DocumentMetadata> getAttachmentMetadata(UUID uuid) throws DocumentNotFoundException {
		return ResponseEntity.ok(documentMetadataHelper.getDocumentMetadata(uuid));
	}
}
