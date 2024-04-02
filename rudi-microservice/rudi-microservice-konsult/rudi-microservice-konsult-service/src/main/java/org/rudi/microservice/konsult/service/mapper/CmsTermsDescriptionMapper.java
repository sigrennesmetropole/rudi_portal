package org.rudi.microservice.konsult.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.microservice.konsult.core.bean.CmsTermsDescription;
import org.rudi.microservice.konsult.core.customization.CmsTermsDescriptionData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CmsTermsDescriptionMapper {

	CmsTermsDescription dataToDto(CmsTermsDescriptionData data);

}
