package org.rudi.microservice.providers.service.producer.impl;

import lombok.RequiredArgsConstructor;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.microservice.providers.service.producer.ProducerService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author GTR18509
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

	private final MediaService mediaService;
	private final ResourceHelper resourceHelper;

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

	@Override
	public void uploadMedia(UUID producerUuid, KindOfData kindOfData, Resource media) throws AppServiceException {
		try {
			final File tempFile = resourceHelper.copyResourceToTempFile(media);
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
