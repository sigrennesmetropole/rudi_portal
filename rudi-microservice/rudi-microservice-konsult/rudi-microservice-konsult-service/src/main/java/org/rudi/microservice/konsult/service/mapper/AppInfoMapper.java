/**
 * 
 */
package org.rudi.microservice.konsult.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.core.ApplicationInformation;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.konsult.core.bean.AppInfo;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface AppInfoMapper extends AbstractMapper<ApplicationInformation, AppInfo> {

	@Override
	@InheritInverseConfiguration
	ApplicationInformation dtoToEntity(AppInfo dto);

	@Override
	AppInfo entityToDto(ApplicationInformation entity);

}
