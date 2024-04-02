package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.HeroDescription;
import org.rudi.microservice.konsult.core.customization.HeroDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MultilingualTextMapper.class })
public interface HeroDescriptionMapper {

	@Mapping(source = "titles1", target = "title1")
	@Mapping(source = "titles2", target = "title2")
	HeroDescription dataToDto(HeroDescriptionData data, @Context Locale locale);


}
