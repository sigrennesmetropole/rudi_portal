/**
 *
 */
package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		NodeProviderMapper.class, AbstractAddressMapper.class })
public interface ProviderFullMapper extends AbstractMapper<ProviderEntity, Provider> {

	@Override
	@InheritInverseConfiguration
	ProviderEntity dtoToEntity(Provider dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	Provider entityToDto(ProviderEntity entity);

	@Override
	void dtoToEntity(Provider dto, @MappingTarget ProviderEntity entity);

}
