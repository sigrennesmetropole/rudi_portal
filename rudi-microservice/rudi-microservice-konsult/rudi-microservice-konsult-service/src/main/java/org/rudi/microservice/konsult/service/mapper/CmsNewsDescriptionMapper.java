package org.rudi.microservice.konsult.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.CmsNewsDescription;
import org.rudi.microservice.konsult.core.customization.CmsNewsDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CmsNewsDescriptionMapper {

	CmsNewsDescription dataToDto(CmsNewsDescriptionData data);

}
