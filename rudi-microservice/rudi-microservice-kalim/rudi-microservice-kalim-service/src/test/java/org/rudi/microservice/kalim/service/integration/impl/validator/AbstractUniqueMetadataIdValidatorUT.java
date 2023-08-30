package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AbstractUniqueMetadataIdValidatorUT {

	private AbstractUniqueMetadataIdValidator<?> validator;

	@BeforeEach
	void setUp() {
		validator = Mockito.mock(AbstractUniqueMetadataIdValidator.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	void canBeUsedByPostHandler() {
		final PostIntegrationRequestTreatmentHandler postHandler = mock(PostIntegrationRequestTreatmentHandler.class);
		assertThat(validator.canBeUsedBy(postHandler))
				.as("This validator can be used by POST handler")
				.isTrue();
	}

	@Test
	void canBeUsedByPutHandler() {
		final PutIntegrationRequestTreatmentHandler putHandler = mock(PutIntegrationRequestTreatmentHandler.class);
		assertThat(validator.canBeUsedBy(putHandler))
				.as("This validator cannot be used by PUT handler")
				.isFalse();
	}
}
