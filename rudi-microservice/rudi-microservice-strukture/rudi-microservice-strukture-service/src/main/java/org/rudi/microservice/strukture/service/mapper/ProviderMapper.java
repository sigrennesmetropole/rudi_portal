/**
 *
 */
package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.strukture.core.bean.Provider;
import org.rudi.microservice.strukture.storage.entity.provider.ProviderEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ProviderMapper extends AbstractMapper<ProviderEntity, Provider> {

	@Override
	@InheritInverseConfiguration
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "nodeProviders", source = "nodeProviders", ignore = true)
	ProviderEntity dtoToEntity(Provider dto);

	@Override
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "nodeProviders", source = "nodeProviders", ignore = true)
	Provider entityToDto(ProviderEntity entity);

	@Override
	@Mapping(target = "addresses", source = "addresses", ignore = true)
	@Mapping(target = "nodeProviders", source = "nodeProviders", ignore = true)
	void dtoToEntity(Provider dto, @MappingTarget ProviderEntity entity);

}
