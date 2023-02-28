/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.core.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.common.core.json.ObjectMapperUtils;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.Project;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 */
@Component
@RequiredArgsConstructor
public class SubtypeMapperConfigurer {

	private final ObjectMapper objectMapper;

	@PostConstruct
	public void addSubTypes() {
		objectMapper.registerSubtypes(ObjectMapperUtils.namedTypesWithSimpleNames(Arrays.asList(
				Project.class,
				LinkedDataset.class,
				NewDatasetRequest.class
		)));
	}

}
