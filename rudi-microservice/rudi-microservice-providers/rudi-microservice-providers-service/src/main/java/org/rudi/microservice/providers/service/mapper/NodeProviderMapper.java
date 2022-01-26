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
import org.rudi.microservice.providers.core.bean.NodeProvider;
import org.rudi.microservice.providers.storage.entity.provider.NodeProviderEntity;

/**
 * @author NTR18299
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface NodeProviderMapper extends AbstractMapper<NodeProviderEntity, NodeProvider> {

	@Override
	@InheritInverseConfiguration
	@Mapping(source = "uuid", target = "uuid", ignore = true)
	NodeProviderEntity dtoToEntity(NodeProvider dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	NodeProvider entityToDto(NodeProviderEntity entity);

	@Mapping(source = "uuid", target = "uuid", ignore = true)
	void dtoToEntity(NodeProvider dto, @MappingTarget NodeProviderEntity entity);
}
