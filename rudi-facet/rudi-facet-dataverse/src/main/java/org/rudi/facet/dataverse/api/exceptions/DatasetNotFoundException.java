package org.rudi.facet.dataverse.api.exceptions;

import java.util.UUID;

public class DatasetNotFoundException extends DataverseAPIException {
	private DatasetNotFoundException(String message) {
		super(message);
	}

	public static DatasetNotFoundException fromPersistentId(String persistentId) {
		return new DatasetNotFoundException(buildMessage("persistentId", persistentId));
	}

	public static DatasetNotFoundException fromGlobalId(UUID globalId) {
		return new DatasetNotFoundException(buildMessage("globalId", globalId));
	}

	private static String buildMessage(String fieldName, Object fielValue) {
		return String.format("Le Dataset de %s=\"%s\" est introuvable", fieldName, fielValue);
	}
}
