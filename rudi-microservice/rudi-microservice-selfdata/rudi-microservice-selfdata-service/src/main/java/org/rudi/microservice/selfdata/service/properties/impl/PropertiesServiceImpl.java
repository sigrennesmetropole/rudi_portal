package org.rudi.microservice.selfdata.service.properties.impl;

import java.util.List;

import org.rudi.microservice.selfdata.core.bean.FrontOfficeProperties;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpring;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpringServlet;
import org.rudi.microservice.selfdata.core.bean.FrontOfficePropertiesSpringServletMultipart;
import org.rudi.microservice.selfdata.core.bean.SelfdataRequestAllowedAttachementType;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataRequestAttachementProperties;
import org.rudi.microservice.selfdata.service.properties.PropertiesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class PropertiesServiceImpl implements PropertiesService {

	@Value("${spring.servlet.multipart.max-file-size}")
	private String springServletMultipartMaxFileSize;

	final SelfdataRequestAttachementProperties attachementProperties;

	@Override
	public FrontOfficeProperties getFrontOfficeProperties() {
		return new FrontOfficeProperties()
				.spring(new FrontOfficePropertiesSpring()
						.servlet(new FrontOfficePropertiesSpringServlet()
								.multipart(new FrontOfficePropertiesSpringServletMultipart()
										.maxFileSize(springServletMultipartMaxFileSize))));
	}

	@Override
	public List<SelfdataRequestAllowedAttachementType> getAllowedAttachementTypes() {
		return attachementProperties.getTypes();
	}
}
