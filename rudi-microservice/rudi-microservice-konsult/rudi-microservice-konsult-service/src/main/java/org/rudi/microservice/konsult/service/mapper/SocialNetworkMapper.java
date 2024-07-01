package org.rudi.microservice.konsult.service.mapper;

import java.util.Locale;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.SocialNetwork;
import org.rudi.microservice.konsult.core.customization.SocialNetworkData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SocialNetworkMapper {

	SocialNetwork dataToDto(SocialNetworkData data, @Context Locale locale);
}
