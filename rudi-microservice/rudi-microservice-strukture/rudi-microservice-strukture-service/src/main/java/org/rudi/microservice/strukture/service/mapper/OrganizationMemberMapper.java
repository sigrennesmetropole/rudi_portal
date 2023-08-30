package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface OrganizationMemberMapper extends AbstractMapper<OrganizationMemberEntity, OrganizationMember> {

	@Override
	@InheritInverseConfiguration
	OrganizationMemberEntity dtoToEntity(OrganizationMember dto);

	@Override
	@Mapping(target = "userUuid", ignore = true)
	void dtoToEntity(OrganizationMember arg0, @MappingTarget OrganizationMemberEntity arg1);

	@Override
	OrganizationMember entityToDto(OrganizationMemberEntity entity);

}
