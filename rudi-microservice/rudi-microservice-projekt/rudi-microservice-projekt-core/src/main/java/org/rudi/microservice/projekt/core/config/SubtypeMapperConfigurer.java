/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.core.config;

import javax.annotation.PostConstruct;

import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

/**
 * @author FNI18300
 *
 */
@Component
public class SubtypeMapperConfigurer {

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void addSubTypes() {
		objectMapper.registerSubtypes(new NamedType(Project.class, "Project"),
				new NamedType(LinkedDataset.class, "LinkedDataset"),
				new NamedType(NewDatasetRequest.class, "NewDatasetRequest"));
	}

}
