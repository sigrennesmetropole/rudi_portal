package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

public interface ElementValidator<T> {

	Set<IntegrationRequestErrorEntity> validate(T t);

	default boolean canBeUsedBy(IntegrationRequestTreatmentHandler handler) {
		return true;
	}
}
