package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.storage.entity.TargetAudienceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface TargetAudienceMapper extends AbstractMapper<TargetAudienceEntity, TargetAudience> {
	@Override
	@InheritInverseConfiguration
	TargetAudienceEntity dtoToEntity(TargetAudience dto);

	@Override
	TargetAudience entityToDto(TargetAudienceEntity entity);
}
