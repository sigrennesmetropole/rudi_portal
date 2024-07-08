package org.rudi.microservice.apigateway.service.encryption;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;

public interface EncryptionService {

	/**
	 * Récupère la clé publique de la clé de chiffrement d'un media dans un DocumentContent
	 * 
	 * @param mediaId l'identifiant du media, optionnel
	 * @return le contenu de la clé publique correspondant au media. Si l'identifiant du media n'est pas renseigné, la clé publique par défaut est
	 *         retournée. Si le media n'est pas connu, une nouvelle clé est créée.
	 * @throws AppServiceException levée si problème de lecture de clé
	 */
	DocumentContent getPublicEncryptionKeyAsDocumentContent(UUID mediaId) throws AppServiceException;

	/**
	 * Récupère la clé publique de la clé de chiffrement d'un media
	 * 
	 * @param mediaId l'identifiant du media, optionnel
	 * @return le contenu de la clé publique correspondant au media. Si l'identifiant du media n'est pas renseigné, la clé publique par défaut est
	 *         retournée. Si le media n'est pas connu, une nouvelle clé est créée.
	 * @throws AppServiceException levée si problème de lecture de clé
	 */
	PublicKey getPublicEncryptionKey(UUID mediaId) throws AppServiceException;

	/**
	 * Récupère la clé privée de la clé de chiffrement d'un media
	 * 
	 * @param mediaId          l'identifiant du media, obligatoire
	 * @param mediaUpdatedDate date de mise à jour du média
	 * @return le contenu de la clé privée correspondant au media à la date mentionnée. Si aucune clé n'existe pour le media et la date mentionnée, la clé
	 *         privée par défaut est retournée.
	 * @throws AppServiceException levée si problème de lecture de clé
	 */
	PrivateKey getPrivateEncryptionKey(UUID mediaId, LocalDateTime mediaUpdatedDate) throws AppServiceException;
}
