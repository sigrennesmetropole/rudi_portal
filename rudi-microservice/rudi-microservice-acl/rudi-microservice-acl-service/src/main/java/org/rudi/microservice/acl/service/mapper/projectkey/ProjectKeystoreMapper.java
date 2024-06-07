/**
 * 
 */
package org.rudi.microservice.acl.service.mapper.projectkey;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.ProjectKeystore;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeystoreEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		ProjectKeyLightMapper.class })
public interface ProjectKeystoreMapper extends AbstractMapper<ProjectKeystoreEntity, ProjectKeystore> {

	@Override
	@Mapping(target = "projectKeys", ignore = true)
	ProjectKeystoreEntity dtoToEntity(ProjectKeystore dto);

}
