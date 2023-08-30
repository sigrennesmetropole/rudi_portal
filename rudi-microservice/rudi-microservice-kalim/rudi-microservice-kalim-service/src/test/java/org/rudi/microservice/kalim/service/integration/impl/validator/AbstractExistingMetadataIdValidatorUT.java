package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AbstractExistingMetadataIdValidatorUT {

	private AbstractExistingMetadataIdValidator<?> validator;

	@BeforeEach
	void setUp() {
		validator = Mockito.mock(AbstractExistingMetadataIdValidator.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	void canBeUsedByPostHandler() {
		final PostIntegrationRequestTreatmentHandler postHandler = mock(PostIntegrationRequestTreatmentHandler.class);
		assertThat(validator.canBeUsedBy(postHandler))
				.as("This validator cannot be used by POST handler")
				.isFalse();
	}

	@Test
	void canBeUsedByPutHandler() {
		final PutIntegrationRequestTreatmentHandler putHandler = mock(PutIntegrationRequestTreatmentHandler.class);
		assertThat(validator.canBeUsedBy(putHandler))
				.as("This validator can be used by PUT handler")
				.isTrue();
	}
}
