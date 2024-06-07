/**
 * 
 */
package org.rudi.microservice.acl.service.mapper.role;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.storage.entity.role.RoleEntity;

/**
 * @author MCY12700
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface RoleMapper extends AbstractMapper<RoleEntity, Role> {

	@Override
	@InheritInverseConfiguration
	RoleEntity dtoToEntity(Role dto);

	/**
	 * Converti un RoleEntity en Role.
	 *
	 * @param entity entity to transform to dto
	 * @return Role
	 */
	@Override
	Role entityToDto(RoleEntity entity);

	@Override
	void dtoToEntity(Role dto, @MappingTarget RoleEntity entity);

}
