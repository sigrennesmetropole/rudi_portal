package org.rudi.microservice.apigateway.service.api;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.apigateway.service.ApigatewaySpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Class de test de la couche service de domaina
 */
@ApigatewaySpringBootTest
class ApigatewayServiceTest {

	@Autowired
	private ApiService apigatewayService;

	@Test
	void testCRUDApigateway() {

		assertNotNull(apigatewayService);

	}

}
