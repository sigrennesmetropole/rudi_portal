package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.CustomizationDescription;
import org.rudi.microservice.konsult.core.customization.CustomizationDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { HeroDescriptionMapper.class,
		ProjectsDescriptionMapper.class, KeyFiguresDescriptionMapper.class, CmsNewsDescriptionMapper.class,
		CmsProjectValuesDescriptionMapper.class, CmsTermsDescriptionMapper.class, FooterDescriptionMapper.class, NewsPageDescriptionMapper.class })
public interface CustomizationDescriptionMapper {

	CustomizationDescription dataToDto(CustomizationDescriptionData descriptionData, @Context Locale locale);

}
