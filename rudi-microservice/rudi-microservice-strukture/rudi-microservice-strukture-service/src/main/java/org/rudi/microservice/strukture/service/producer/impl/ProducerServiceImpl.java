package org.rudi.microservice.strukture.service.producer.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.util.ContentTypeUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.microservice.strukture.service.helper.StruktureAuthorisationHelper;
import org.rudi.microservice.strukture.service.producer.ProducerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * @author GTR18509
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

	private final MediaService mediaService;
	private final StruktureAuthorisationHelper struktureAuthorisationHelper;

	@Value("${rudi.producer.attachement.allowed.types:image/jpeg,image/png}")
	List<String> allowedLogoType;

	@Override
	public DocumentContent downloadMedia(@NotNull UUID producerUuid, @NotNull KindOfData kindOfData) throws AppServiceException {
		try {
			return mediaService.getMediaFor(MediaOrigin.PRODUCER, producerUuid, KindOfData.LOGO);
		} catch (DataverseAPIException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors du téléchargement du %s du producteur avec producerUuid = %s",
							kindOfData.getValue(),
							producerUuid),
					e);
		}
	}


	/**
	 * Upload un media sous la forme d'un document content pour un producer ciblé par son UUID.
	 * Le media cible chez le producteur est détermié par le kindOfData
	 *
	 * @param producerUuid uuid du producteur de donnée à qui on souhaite changer le "kindOfData"
	 * @param kindOfData type de data ciblée par la requête (EXEMPLE : LOGO)
	 * @param documentContent document qui doit être uploadé
	 * @throws AppServiceException en cas d'erreur sur l'upload
	 */
	@Override
	public void uploadMedia(UUID producerUuid, KindOfData kindOfData, DocumentContent documentContent) throws AppServiceException {
		Map<String, Boolean> accessRightsByRole = StruktureAuthorisationHelper.getADMINISTRATOR_ACCESS();
		// Vérification des droits d'accès
		// les droits autorisés dans accessRights doivent être cohérents avec ceux définis en PreAuth coté Controller
		if(!(struktureAuthorisationHelper.isAccessGrantedByRole(accessRightsByRole)) && !(struktureAuthorisationHelper.isAccessGrantedForUserOnOrganization(producerUuid))) {
			throw new AppServiceUnauthorizedException("Accès non autorisé à la fonctionnalité pour l'utilisateur");
		}

		try {
			if(kindOfData.equals(KindOfData.LOGO)) {
				// Vérifie que le type de contenu est bien autorisé pour un logo
				ContentTypeUtils.checkMediaType(documentContent.getContentType(), allowedLogoType);
			}

			File tempFile = File.createTempFile(UUID.randomUUID().toString(),"." + FilenameUtils.getExtension(documentContent.getFileName()));
			FileUtils.copyInputStreamToFile(documentContent.getFileStream(), tempFile);
			mediaService.setMediaFor(MediaOrigin.PRODUCER, producerUuid, kindOfData, tempFile);
		} catch (final DataverseAPIException | IOException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors de l'upload du %s du producteur d'id %s",
							kindOfData.getValue(),
							producerUuid)
					, e);
		}
	}

	@Override
	public void deleteMedia(UUID producerUuid, KindOfData kindOfData) throws AppServiceException {
		try {
			mediaService.deleteMediaFor(MediaOrigin.PRODUCER, producerUuid, kindOfData);
		} catch (final DataverseAPIException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors de la suppression du %s du producteur d'id %s",
							kindOfData.getValue(),
							producerUuid)
					, e);
		}
	}

}
