/**
 * 
 */
package org.rudi.microservice.acl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.User;

/**
 * Mapper qui convertit un AuthenticatedUser en User
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface AuthenticatedUserMapper {

	/**
	 * Convertit un AuthenticatedUser en User.
	 *
	 * @param entity entity to transform to dto
	 * @return User
	 */
	@Mapping(target = "addresses", source = "email", ignore = true)
	@Mapping(target = "company", source = "organization")
	@Mapping(target = "type", source = "type", ignore = true)
	@Mapping(target = "roles", source = "roles", ignore = true)
	User entityToDto(AuthenticatedUser entity);

}
