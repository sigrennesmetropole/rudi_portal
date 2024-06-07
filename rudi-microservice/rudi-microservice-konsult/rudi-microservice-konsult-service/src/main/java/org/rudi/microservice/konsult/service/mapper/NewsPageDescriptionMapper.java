package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.NewsPageDescription;
import org.rudi.microservice.konsult.core.customization.NewsPageDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MultilingualTextMapper.class, AssetsPageOrderMapper.class })
public interface NewsPageDescriptionMapper {

	@Mapping(source = "titles1", target = "title1")
	@Mapping(source = "titles2", target = "title2")
	NewsPageDescription dataToDto(NewsPageDescriptionData data, @Context Locale locale);
}
