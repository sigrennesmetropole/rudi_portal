package org.rudi.facet.apimaccess.exception;

import java.util.UUID;

import javax.annotation.Nullable;

public class APINotUniqueException extends APIManagerException {

	private static final long serialVersionUID = 6554644646848348016L;

	public APINotUniqueException(@Nullable UUID globalId, UUID mediaId, int count) {
		super(String.format(
				"Il y a %s API qui correspondent aux informations globalId = %s et mediaId = %s. Veuillez vérifier la cohérence des API créées dans l'API Manager.",
				count, globalId, mediaId));
	}
}
