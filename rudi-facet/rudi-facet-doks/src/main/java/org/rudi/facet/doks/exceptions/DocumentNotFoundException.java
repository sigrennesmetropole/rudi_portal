package org.rudi.facet.doks.exceptions;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.doks.entity.DocumentEntity;

public class DocumentNotFoundException extends AppServiceNotFoundException {
	private static final long serialVersionUID = -3449063854815043841L;

	public DocumentNotFoundException(UUID uuid) {
		super(DocumentEntity.class, uuid);
	}
}