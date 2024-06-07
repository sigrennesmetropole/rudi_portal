package org.rudi.microservice.apigateway.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.apigateway.core.bean.ApiParameter;
import org.rudi.microservice.apigateway.storage.entity.api.ApiParameterEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ApiParameterMapper extends AbstractMapper<ApiParameterEntity, ApiParameter> {

	@Override
	@InheritInverseConfiguration
	ApiParameterEntity dtoToEntity(ApiParameter dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité. On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles
	 * sont recréées en base)
	 */
	@Override
	void dtoToEntity(ApiParameter dto, @MappingTarget ApiParameterEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	ApiParameter entityToDto(ApiParameterEntity entity);

}
