package org.rudi.microservice.template.service.domaina;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.template.service.TemplateSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Class de test de la couche service de domaina
 */
@TemplateSpringBootTest
class TemplateServiceTest {

	@Autowired
	private TemplateService templateService;

	@Test
	void testCRUDTemplate() {

		assertNotNull(templateService);

	}

}
