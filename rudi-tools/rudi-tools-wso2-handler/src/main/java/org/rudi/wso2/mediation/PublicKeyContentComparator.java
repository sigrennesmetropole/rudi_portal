package org.rudi.wso2.mediation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.model.API;

/**
 * Compare les clés publiques en se basant sur leur contenu.
 */
class PublicKeyContentComparator implements PublicKeyComparator {
	/**
	 * Nom de la propriété envoyée par l'IRISA contenant la deuxième ligne de la clé publique utilisée pour chiffrer
	 */
	static final String PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY = "pubKeyCut";
	private static final Log LOGGER = LogFactory.getLog(PublicKeyContentComparator.class);

	@Override
	public boolean usesSamePublicKey(EncryptedMediaHandler encryptedMediaHandler, API engagedApi) throws PublicKeyComparatorException {
		final var apiPublicKeyPartialContent = AdditionalPropertiesUtil.getAdditionalProperty(PUBLIC_KEY_PARTIAL_CONTENT_ADDITIONAL_PROPERTY, engagedApi);
		LOGGER.debug("apiPublicKeyPartialContent = " + apiPublicKeyPartialContent);
		if (apiPublicKeyPartialContent == null) {
			return false;
		}

		final var portalPublicKeyContent = getPublicKeyContent(encryptedMediaHandler);
		LOGGER.debug("portalPublicKeyContent     = " + portalPublicKeyContent);
		if (portalPublicKeyContent == null) {
			return false;
		}

		return portalPublicKeyContent.startsWith(apiPublicKeyPartialContent);
	}

	private String getPublicKeyContent(EncryptedMediaHandler encryptedMediaHandler) throws PublicKeyComparatorException {
		try {
			return encryptedMediaHandler.getPublicKeyContent();
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new PublicKeyComparatorException("Cannot get portal public key content", e);
		}
	}
}
