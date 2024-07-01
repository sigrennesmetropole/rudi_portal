package org.rudi.microservice.strukture.service.producer;

import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kmedia.bean.KindOfData;

/**
 * @author GTR18509
 */
public interface ProducerService {

	/**
	 * Télécharge le média du producteur d'un jeu de données, dans le dataverse Rudi Media
	 *
	 * @param producerUuid l'uuid du producteur
	 * @param kindOfData   le type de média
	 * @return le média téléchargé
	 * @throws AppServiceException En cas d'erreur avec le service de téléchargement
	 */
	DocumentContent downloadMedia(UUID producerUuid, KindOfData kindOfData) throws AppServiceException;

	/**
	 * Uploade le média du producteur d'un jeu de données, dans le dataverse Rudi Media
	 *
	 * @param producerUuid l'uuid du producteur
	 * @param kindOfData   le type de média
	 * @param documentContent le média à remplacer dans Dataverse
	 * @throws AppServiceException En cas d'erreur avec le service d'upload
	 */
	void uploadMedia(UUID producerUuid, KindOfData kindOfData, DocumentContent documentContent) throws AppServiceException;

	/**
	 * Supprime le média associé à ce producteur
	 *
	 * @param producerUuid l'uuid du producteur
	 * @param kindOfData   type du média à supprimer
	 */
	void deleteMedia(UUID producerUuid, KindOfData kindOfData) throws AppServiceException;
}
