package org.rudi.microservice.kalim.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
@Component
public interface IntegrationRequestMapper extends AbstractMapper<IntegrationRequestEntity, IntegrationRequest> {

	@Override
	@InheritInverseConfiguration
	IntegrationRequestEntity dtoToEntity(IntegrationRequest dto);

	@Override
	IntegrationRequest entityToDto(IntegrationRequestEntity entity);

}
