package org.rudi.microservice.konsult.service.customization.impl;

import java.io.IOException;
import java.util.Locale;

import org.apache.tika.utils.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.microservice.konsult.core.bean.CustomizationDescription;
import org.rudi.microservice.konsult.service.customization.CustomizationService;
import org.rudi.microservice.konsult.service.helper.customization.CustomizationHelper;
import org.rudi.microservice.konsult.service.mapper.CustomizationDescriptionMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomizationServiceImpl implements CustomizationService {

	private final CustomizationHelper customizationHelper;
	private final CustomizationDescriptionMapper customizationDescriptionMapper;


	@Override
	public CustomizationDescription getCustomizationDescription(String lang) throws IOException {
		Locale locale = Locale.FRANCE;
		if(!StringUtils.isBlank(lang)){
			locale = Locale.forLanguageTag(lang);
		}
		return customizationDescriptionMapper.dataToDto(customizationHelper.getCustomizationDescriptionData(), locale);
	}

	@Override
	public DocumentContent loadResources(String resourceName) throws IOException{
		return customizationHelper.loadResources(resourceName);
	}
}
