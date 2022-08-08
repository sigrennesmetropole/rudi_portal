package org.rudi.microservice.konsult.facade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.konsult.core.bean.FrontOfficeProperties;
import org.rudi.microservice.konsult.facade.controller.api.PropertiesApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PropertiesApiImpl implements PropertiesApi {
	private static final String FRONT_OFFICE_JSON = "konsult-front-office.json";

	private final ResourceHelper resourceHelper;
	private final ObjectMapper objectMapper;

	@Override
	public ResponseEntity<FrontOfficeProperties> getFrontOfficeProperties() throws Exception {
		return getProperties(FRONT_OFFICE_JSON);
	}

	@Nonnull
	private ResponseEntity<FrontOfficeProperties> getProperties(String filename) throws AppServiceException {
		try {
			val resource = getResource(filename);
			val properties = objectMapper.readValue(resource.getInputStream(), FrontOfficeProperties.class);
			return ResponseEntity.ok(properties);
		} catch (IOException e) {
			throw new AppServiceException("Error loading " + filename, e);
		}
	}

	@Nonnull
	private Resource getResource(String filename) {
		return resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(filename);
	}
}
