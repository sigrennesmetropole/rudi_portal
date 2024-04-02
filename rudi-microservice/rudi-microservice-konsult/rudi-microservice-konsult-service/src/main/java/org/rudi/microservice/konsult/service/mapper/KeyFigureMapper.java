package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.KeyFigure;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MultilingualTextMapper.class })
public interface KeyFigureMapper {

	@Mapping(source = "labels", target = "label")
	KeyFigure dataToDto(KeyFigureData data, @Context Locale locale);

}
