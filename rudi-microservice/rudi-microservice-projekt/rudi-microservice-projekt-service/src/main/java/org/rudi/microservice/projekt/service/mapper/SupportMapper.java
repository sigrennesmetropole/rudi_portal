package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.Support;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface SupportMapper extends AbstractMapper<SupportEntity, Support> {

	@Override
	@InheritInverseConfiguration
	SupportEntity dtoToEntity(Support dto);

	@Override
	Support entityToDto(SupportEntity entity);

}
