package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface TerritorialScaleMapper extends AbstractMapper<TerritorialScaleEntity, TerritorialScale> {

	@Override
	@InheritInverseConfiguration
	TerritorialScaleEntity dtoToEntity(TerritorialScale dto);

	@Override
	TerritorialScale entityToDto(TerritorialScaleEntity entity);

}
