package org.rudi.microservice.selfdata.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface SelfdataInformationRequestMapper extends AssetDescriptionMapper<SelfdataInformationRequestEntity, SelfdataInformationRequest> {

	@Override
	@InheritInverseConfiguration
	SelfdataInformationRequestEntity dtoToEntity(SelfdataInformationRequest dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Override
	void dtoToEntity(SelfdataInformationRequest dto, @MappingTarget SelfdataInformationRequestEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	SelfdataInformationRequest entityToDto(SelfdataInformationRequestEntity entity);

	@AfterMapping
	default void updateType(@MappingTarget SelfdataInformationRequest dto, SelfdataInformationRequestEntity entity) {
		dto.setObjectType(dto.getClass().getSimpleName());
	}
}
