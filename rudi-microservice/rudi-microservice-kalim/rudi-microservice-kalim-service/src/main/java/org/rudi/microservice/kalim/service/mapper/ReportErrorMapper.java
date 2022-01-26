package org.rudi.microservice.kalim.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kalim.core.bean.ReportError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {MapperUtils.class})
@Component
public interface ReportErrorMapper extends AbstractMapper<IntegrationRequestErrorEntity, org.rudi.microservice.kalim.core.bean.ReportError> {

    @Override
    @InheritInverseConfiguration
    IntegrationRequestErrorEntity dtoToEntity(ReportError dto);

    @Override
    @Mapping(source = "code", target = "errorCode")
    @Mapping(source = "message", target = "errorMessage")
    ReportError entityToDto(IntegrationRequestErrorEntity entity);

}
