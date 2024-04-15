package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.CmsProjectValuesDescription;
import org.rudi.microservice.konsult.core.customization.CmsProjectValuesDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MultilingualTextMapper.class })
public interface CmsProjectValuesDescriptionMapper {

	@Mapping(source = "titles1", target = "title1")
	@Mapping(source = "titles2", target = "title2")
	@Mapping(source = "descriptions", target = "description")
	CmsProjectValuesDescription dataToDto(CmsProjectValuesDescriptionData data, @Context Locale locale);

}
