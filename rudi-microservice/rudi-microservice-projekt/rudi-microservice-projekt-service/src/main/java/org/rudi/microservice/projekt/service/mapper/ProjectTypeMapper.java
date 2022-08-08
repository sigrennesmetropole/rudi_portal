package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ProjectTypeMapper extends AbstractMapper<ProjectTypeEntity, ProjectType> {

	@Override
	@InheritInverseConfiguration
	ProjectTypeEntity dtoToEntity(ProjectType dto);

	@Override
	ProjectType entityToDto(ProjectTypeEntity entity);

}
