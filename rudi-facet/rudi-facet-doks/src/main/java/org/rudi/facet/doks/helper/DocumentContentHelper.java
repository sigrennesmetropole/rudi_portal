package org.rudi.facet.doks.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.crypto.SecuredFileHelper;
import org.rudi.facet.doks.crypto.DocumentCipherOperator;
import org.rudi.facet.doks.dao.DocumentDao;
import org.rudi.facet.doks.entity.DocumentEntity;
import org.rudi.facet.doks.exceptions.EmptyDocumentException;
import org.rudi.facet.doks.mapper.DocumentMapper;
import org.rudi.facet.doks.policy.AuthorizationPolicy;
import org.rudi.facet.doks.properties.DoksProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentContentHelper {
	private final DocumentMapper documentMapper;
	private final DocumentDao documentDao;
	private final DocumentCipherOperator documentCipherOperator;
	private final DoksProperties doxProperties;
	private final SecuredFileHelper securedFileHelper;
	private final ACLHelper aclHelper;

	/**
	 * @param encrypt on souhaite chiffrer le document ?
	 * @return l'UUID du document créé
	 */
	@Transactional  // readOnly = false
	public UUID createDocumentContent(DocumentContent documentContent, boolean encrypt, UUID uploaderUuid) throws GeneralSecurityException, IOException, SQLException {
		validate(documentContent);
		final DocumentEntity documentEntity = dtoToEntity(documentContent, encrypt);
		documentEntity.setUuid(UUID.randomUUID());
		documentEntity.setEncrypted(encrypt);
		documentEntity.setUploaderUuid(uploaderUuid);
		documentDao.save(documentEntity);
		return documentEntity.getUuid();
	}

	private void validate(DocumentContent documentContent) {
		checkFileSize(documentContent);
	}

	private void checkFileSize(DocumentContent documentContent) {
		final var fileSize = documentContent.getFileSize();

		if (fileSize <= 0) {
			throw new EmptyDocumentException(documentContent.getFileName());
		}

		// La taille max est déjà gérée par Spring (cf. FileSizeLimitExceededException et MaxUploadSizeExceededException) via les propriétés spring.servlet.multipart.max-file-size et spring.servlet.multipart.max-request-size
	}

	private DocumentEntity dtoToEntity(DocumentContent documentContent, boolean encrypt) throws IOException, GeneralSecurityException, SQLException {
		final var originalEntity = documentMapper.dtoToEntity(documentContent);
		return encrypt ? encrypt(originalEntity) : originalEntity;
	}

	private DocumentEntity encrypt(DocumentEntity decryptedDocument) throws IOException, GeneralSecurityException, SQLException {
		final var decryptedStream = decryptedDocument.getFileContents().getBinaryStream();
		final var encryptedFilePath = documentCipherOperator.encrypt(decryptedStream);
		return decryptedDocument.toBuilder()
				.fileContents(documentMapper.toFileContents(encryptedFilePath))
				.build();
	}

	public DocumentContent getDocumentContent(UUID uuid, AuthorizationPolicy authorizationPolicy) throws GeneralSecurityException, IOException, AppServiceNotFoundException, SQLException, AppServiceForbiddenException, AppServiceUnauthorizedException {
		final var documentEntity = getDocumentEntity(uuid);

		checkIfAuthenticatedUserCanDownloadDocument(documentEntity, authorizationPolicy);

		final Path tmpFile = createTempFile();
		try (
				final var originalInputStream = documentEntity.getFileContents().getBinaryStream();
				final var outputStream = Files.newOutputStream(tmpFile)
		) {

			if (documentEntity.isEncrypted()) {
				documentCipherOperator.decrypt(originalInputStream, outputStream);
			} else {
				IOUtils.copy(originalInputStream, outputStream); // si on renvoie directement le binaryStream de fileContents, sans passer par un fichier temporaire, on reçoit l'erreur : org.postgresql.util.PSQLException: ERREUR: descripteur invalide de « Large Object » : 1
			}

			final var inputStream = Files.newInputStream(tmpFile);
			return documentMapper.entityToDto(documentEntity).toBuilder()
					.fileStream(inputStream)
					.build();
		}
	}

	/**
	 * @throws AppServiceForbiddenException si l'utilisateur connecté n'a pas le droit de télécharger le document
	 */
	private void checkIfAuthenticatedUserCanDownloadDocument(DocumentEntity document, AuthorizationPolicy authorizationPolicy) throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		checkIfAuthenticatedUser(authorizationPolicy::isAllowedToDownloadDocument, document, "download document");
	}

	/**
	 * @throws AppServiceForbiddenException si l'utilisateur connecté n'a pas le droit de télécharger le document
	 */
	private void checkIfAuthenticatedUser(BiPredicate<User, UUID> predicate, DocumentEntity document, String authorizationTitle) throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		final var authenticatedUser = aclHelper.getAuthenticatedUser();
		final var uploaderUuid = document.getUploaderUuid();
		if (!predicate.test(authenticatedUser, uploaderUuid)) {
			throw new AppServiceForbiddenException("Authenticated user is not allowed to " + authorizationTitle);
		}
	}

	private Path createTempFile() throws IOException {
		final var prefix = "decrypted";
		final var suffix = ".bin";
		return doxProperties.isUnsecuredTempDirectoryAllowed()
				? Files.createTempFile(prefix, suffix)
				: securedFileHelper.createSecuredTempFile(prefix, suffix);
	}

	@Nonnull
	private DocumentEntity getDocumentEntity(UUID uuid) throws AppServiceNotFoundException {
		final var documentEntity = documentDao.findByUuid(uuid);
		if (documentEntity == null) {
			throw new AppServiceNotFoundException(DocumentEntity.class, uuid);
		}
		return documentEntity;
	}

	@Transactional  // readOnly = false
	public void deleteAttachment(UUID uuid, AuthorizationPolicy authorizationPolicy) throws AppServiceNotFoundException, AppServiceForbiddenException, AppServiceUnauthorizedException {
		final var documentEntity = getDocumentEntity(uuid);
		checkIfAuthenticatedUser(authorizationPolicy::isAllowedToDeleteDocument, documentEntity, "delete document");
		documentDao.delete(documentEntity);
	}
}
