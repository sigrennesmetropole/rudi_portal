package org.rudi.microservice.selfdata.service.properties.impl;

import org.rudi.microservice.selfdata.core.bean.FrontOfficeProperties;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpring;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpringServlet;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpringServletMultipart;
import org.rudi.microservice.selfdata.service.properties.PropertiesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PropertiesServiceImpl implements PropertiesService {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String springServletMultipartMaxFileSize;

	@Override
	public FrontOfficeProperties getFrontOfficeProperties() {
		return new FrontOfficeProperties()
				.spring(new FrontOfficePropertiesSpring()
						.servlet(new FrontOfficePropertiesSpringServlet()
								.multipart(new FrontOfficePropertiesSpringServletMultipart()
										.maxFileSize(springServletMultipartMaxFileSize))));
	}
}
