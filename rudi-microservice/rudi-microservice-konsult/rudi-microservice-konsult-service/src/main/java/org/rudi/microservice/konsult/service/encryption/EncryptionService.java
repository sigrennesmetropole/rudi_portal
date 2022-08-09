package org.rudi.microservice.konsult.service.encryption;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;

public interface EncryptionService {

	/**
	 * Récupère la clé publique des clés de chiffrement
	 * @return le contenu de la clé publique
	 * @throws AppServiceException levée si problème de lecture de clé
	 */
	DocumentContent getPublicEncryptionKey() throws AppServiceException;
}
