package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ReutilisationStatusMapper extends AbstractMapper<ReutilisationStatusEntity, ReutilisationStatus> {

	@Override
	@InheritInverseConfiguration
	ReutilisationStatusEntity dtoToEntity(ReutilisationStatus dto);

	@Override
	ReutilisationStatus entityToDto(ReutilisationStatusEntity entity);

}
