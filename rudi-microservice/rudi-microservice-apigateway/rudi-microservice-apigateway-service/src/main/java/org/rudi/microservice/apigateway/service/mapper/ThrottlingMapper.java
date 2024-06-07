package org.rudi.microservice.apigateway.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ThrottlingMapper extends AbstractMapper<ThrottlingEntity, Throttling> {

	@Override
	@InheritInverseConfiguration
	ThrottlingEntity dtoToEntity(Throttling dto);

	@Override
	void dtoToEntity(Throttling dto, @MappingTarget ThrottlingEntity entity);

	@Override
	Throttling entityToDto(ThrottlingEntity entity);

}
