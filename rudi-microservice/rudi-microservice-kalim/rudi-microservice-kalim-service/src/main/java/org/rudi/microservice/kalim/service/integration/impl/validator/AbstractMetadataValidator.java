package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

public abstract class AbstractMetadataValidator<T> implements ElementValidator<T> {

	protected abstract T getMetadataElementToValidate(Metadata metadata);

	public Set<IntegrationRequestErrorEntity> validateMetadata(Metadata metadata) {
		return this.validate(getMetadataElementToValidate(metadata));
	}
}
