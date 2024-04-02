package org.rudi.microservice.konsult.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.CmsProjectValuesDescription;
import org.rudi.microservice.konsult.core.customization.CmsProjectValuesDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CmsProjectValuesDescriptionMapper {

	CmsProjectValuesDescription dataToDto(CmsProjectValuesDescriptionData data);

}
