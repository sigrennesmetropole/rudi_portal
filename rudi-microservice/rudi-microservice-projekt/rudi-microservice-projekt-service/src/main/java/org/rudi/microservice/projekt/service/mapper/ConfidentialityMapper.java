package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ConfidentialityMapper extends AbstractMapper<ConfidentialityEntity, Confidentiality> {

	@Override
	@InheritInverseConfiguration
	ConfidentialityEntity dtoToEntity(Confidentiality dto);

	@Override
	Confidentiality entityToDto(ConfidentialityEntity entity);

}
