/**
 * 
 */
package org.rudi.microservice.providers.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.providers.core.bean.AddressRole;
import org.rudi.microservice.providers.storage.entity.address.AddressRoleEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface AddressRoleMapper extends AbstractMapper<AddressRoleEntity, AddressRole> {

	@Override
	@InheritInverseConfiguration
	@Mapping(source = "uuid", target = "uuid", ignore = true)
	AddressRoleEntity dtoToEntity(AddressRole dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	AddressRole entityToDto(AddressRoleEntity entity);

	@Mapping(source = "uuid", target = "uuid", ignore = true)
	void dtoToEntity(AddressRole dto, @MappingTarget AddressRoleEntity entity);

}
