/**
 * 
 */
package org.rudi.microservice.acl.service.mapper.user;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface UserLightMapper extends AbstractMapper<UserEntity, User> {

	@Override
	@InheritInverseConfiguration
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "roles", source = "roles", ignore = true)
	UserEntity dtoToEntity(User dto);

	/**
	 * Convertit un UserEntity en User.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "password", source = "password", ignore = true)
	User entityToDto(UserEntity entity);

	@Override
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "roles", source = "roles", ignore = true)
	@Mapping(target = "password", source = "password", ignore = true)
	@Mapping(target = "login", source = "login", ignore = true)
	@Mapping(target = "type", source = "type", ignore = true)
	@Mapping(target = "failedAttempt", source = "failedAttempt", ignore = true)
	@Mapping(target = "lastFailedAttempt", source = "lastFailedAttempt", ignore = true)
	@Mapping(target = "lastConnexion", source = "lastConnexion", ignore = true)
	@Mapping(target = "accountLocked", source = "accountLocked", ignore = true)
	void dtoToEntity(User dto, @MappingTarget UserEntity entity);

}
