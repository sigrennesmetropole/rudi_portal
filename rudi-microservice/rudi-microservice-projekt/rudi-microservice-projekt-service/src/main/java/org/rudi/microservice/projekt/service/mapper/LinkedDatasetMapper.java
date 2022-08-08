package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface LinkedDatasetMapper extends AssetDescriptionMapper<LinkedDatasetEntity, LinkedDataset> {

	@Override
	@InheritInverseConfiguration
	LinkedDatasetEntity dtoToEntity(LinkedDataset dto);

	@Override
	LinkedDataset entityToDto(LinkedDatasetEntity entity);

	/**
	 * Utilisé uniquement pour la modification d'une entité. On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles
	 * sont recréées en base)
	 */
	@Override
	@Mapping(target = "processDefinitionKey", ignore = true)
	@Mapping(target = "processDefinitionVersion", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "functionalStatus", ignore = true)
	@Mapping(target = "initiator", ignore = true)
	@Mapping(target = "updator", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "assignee", ignore = true)
	void dtoToEntity(LinkedDataset dto, @MappingTarget LinkedDatasetEntity entity);

	@AfterMapping
	default void updateType(@MappingTarget LinkedDataset dto, LinkedDatasetEntity entity) {
		dto.setObjectType(dto.getClass().getSimpleName());
	}
}
