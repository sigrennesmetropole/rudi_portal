package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.AssetsPageOrder;
import org.rudi.microservice.konsult.core.customization.AssetsPageOrderData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MultilingualTextMapper.class})
public interface AssetsPageOrderMapper {


	@Mapping(source = "libelles", target = "libelle")
	AssetsPageOrder dataToDto(AssetsPageOrderData data, @Context Locale locale);
}
