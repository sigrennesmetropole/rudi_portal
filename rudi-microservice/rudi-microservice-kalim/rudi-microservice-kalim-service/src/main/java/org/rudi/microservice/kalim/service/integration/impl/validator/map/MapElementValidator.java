package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.Set;

import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

public interface MapElementValidator<T> {
	Set<IntegrationRequestErrorEntity> validate(T t, String interfaceContractToUse);

	default boolean canBeUsedBy(IntegrationRequestTreatmentHandler handler) {
		return true;
	}

	default boolean isToBeUse(T t) {
		return false;
	}
}
