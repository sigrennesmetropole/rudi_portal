package org.rudi.microservice.projekt.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		LinkedDatasetMapper.class, NewDatasetRequestMapper.class })
public interface ProjectMapper extends AssetDescriptionMapper<ProjectEntity, Project> {

	@Override
	@InheritInverseConfiguration
	ProjectEntity dtoToEntity(Project dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité. On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles
	 * sont recréées en base)
	 */
	@Override
	@Mapping(target = "confidentiality", ignore = true)
	@Mapping(target = "desiredSupports", ignore = true)
	@Mapping(target = "territorialScale", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "datasetRequests", ignore = true)
	@Mapping(target = "linkedDatasets", ignore = true)
	@Mapping(target = "processDefinitionKey", ignore = true)
	@Mapping(target = "processDefinitionVersion", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "functionalStatus", ignore = true)
	@Mapping(target = "initiator", ignore = true)
	@Mapping(target = "updator", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "assignee", ignore = true)
	@Mapping(target = "targetAudiences", ignore = true)
	void dtoToEntity(Project dto, @MappingTarget ProjectEntity entity);

	@Override
	Project entityToDto(ProjectEntity entity);

	@AfterMapping
	default void updateType(@MappingTarget Project dto, ProjectEntity entity) {
		dto.setObjectType(dto.getClass().getSimpleName());
	}

}
