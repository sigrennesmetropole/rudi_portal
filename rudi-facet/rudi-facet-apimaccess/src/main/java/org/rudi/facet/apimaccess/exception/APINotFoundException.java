package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.APIDescription;

import javax.annotation.Nullable;
import java.util.UUID;

public class APINotFoundException extends APIManagerException {
	public APINotFoundException(APIDescription apiDescription) {
		super("Aucune API ne possède le nom " + apiDescription.getName());
	}

	public APINotFoundException(@Nullable UUID globalId, UUID mediaId) {
		super("Aucune API ne correspond aux informations globalId = " + globalId + " et mediaId = " + mediaId);
	}
}
