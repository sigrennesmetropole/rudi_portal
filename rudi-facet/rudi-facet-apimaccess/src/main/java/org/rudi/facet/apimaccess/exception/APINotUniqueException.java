package org.rudi.facet.apimaccess.exception;

import javax.annotation.Nullable;
import java.util.UUID;

public class APINotUniqueException extends APIManagerException {
	public APINotUniqueException(@Nullable UUID globalId, UUID mediaId, int count) {
		super(String.format(
				"Il y a %s API qui correspondent aux informations globalId = %s et mediaId = %s. Veuillez vérifier la cohérence des API créées dans l'API Manager.",
				count,
				globalId,
				mediaId));
	}
}
