package org.rudi.facet.apimaccess.exception;

import java.util.UUID;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.rudi.facet.apimaccess.bean.APIDescription;

public class APINotFoundException extends APIManagerException {

	private static final long serialVersionUID = -345692680613837943L;

	public APINotFoundException(APIDescription apiDescription) {
		super("Aucune API ne possède le nom " + apiDescription.getName());
	}

	public APINotFoundException(@Nullable UUID globalId, UUID mediaId) {
		super(getMessage(globalId, mediaId));
	}

	@NotNull
	private static String getMessage(@org.jetbrains.annotations.Nullable UUID globalId, UUID mediaId) {
		if (globalId == null) {
			return "Aucune API ne correspond à l'information mediaId = " + mediaId;
		} else {
			return "Aucune API ne correspond aux informations globalId = " + globalId + " et mediaId = " + mediaId;
		}
	}
}
