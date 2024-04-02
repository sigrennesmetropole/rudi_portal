package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.KeyFiguresDescription;
import org.rudi.microservice.konsult.core.customization.KeyFiguresDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { KeyFigureMapper.class })
public interface KeyFiguresDescriptionMapper {
	KeyFiguresDescription dataToDto(KeyFiguresDescriptionData data, @Context Locale locale);

}
