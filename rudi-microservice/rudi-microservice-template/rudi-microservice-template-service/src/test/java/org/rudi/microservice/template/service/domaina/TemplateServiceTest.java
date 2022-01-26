package org.rudi.microservice.template.service.domaina;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.microservice.template.service.SpringBootTestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Class de test de la couche service de domaina
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
public class TemplateServiceTest {

	@Autowired
	private TemplateService templateService;

	@BeforeEach
	public void initData() {
	}

	@Test
	public void testCRUDTemplate() {

		assertNotNull(templateService);

	}

}
