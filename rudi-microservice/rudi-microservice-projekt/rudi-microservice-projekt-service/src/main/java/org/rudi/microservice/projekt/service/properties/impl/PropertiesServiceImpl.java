package org.rudi.microservice.projekt.service.properties.impl;

import java.util.Arrays;

import org.rudi.microservice.projekt.core.bean.FrontOfficeProperties;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesProjekt;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesProjektProjectMedia;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesProjektProjectMediaLogo;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesSpring;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesSpringServlet;
import org.rudi.microservice.projekt.core.bean.FrontOfficePropertiesSpringServletMultipart;
import org.rudi.microservice.projekt.service.project.ProjectMediaProperties;
import org.rudi.microservice.projekt.service.properties.PropertiesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PropertiesServiceImpl implements PropertiesService {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String springServletMultipartMaxFileSize;

	private final ProjectMediaProperties projectMediaProperties;

	@Override
	public FrontOfficeProperties getFrontOfficeProperties() {
		return new FrontOfficeProperties()
				.projekt(new FrontOfficePropertiesProjekt()
						.projectMedia(new FrontOfficePropertiesProjektProjectMedia()
								.logo(new FrontOfficePropertiesProjektProjectMediaLogo()
										.extensions(Arrays.asList(projectMediaProperties.getLogo().getExtensions())))))
				.spring(new FrontOfficePropertiesSpring()
						.servlet(new FrontOfficePropertiesSpringServlet()
								.multipart(new FrontOfficePropertiesSpringServletMultipart()
										.maxFileSize(springServletMultipartMaxFileSize))));
	}
}
