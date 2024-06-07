package org.rudi.microservice.apigateway.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		ApiParameterMapper.class, ThrottlingMapper.class })
public interface ApiMapper extends AbstractMapper<ApiEntity, Api> {

	@Override
	@InheritInverseConfiguration
	@Mapping(source = "apiId", target = "uuid")
	@Mapping(target = "throttlings", ignore = true)
	@Mapping(target = "parameters", ignore = true)
	ApiEntity dtoToEntity(Api dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité. On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles
	 * sont recréées en base)
	 */
	@Override
	@Mapping(source = "apiId", target = "uuid")
	@Mapping(target = "throttlings", ignore = true)
	@Mapping(target = "parameters", ignore = true)
	void dtoToEntity(Api dto, @MappingTarget ApiEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	@Mapping(target = "apiId", source = "uuid")
	Api entityToDto(ApiEntity entity);

}
