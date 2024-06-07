/**
 * 
 */
package org.rudi.microservice.acl.service.mapper.projectkey;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.ProjectKey;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeyEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ProjectKeyLightMapper {

	/**
	 * Convertit un UserEntity en User.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Mapping(target = "clientId", source = "client.login")
	ProjectKey entityToDto(ProjectKeyEntity entity);

}
