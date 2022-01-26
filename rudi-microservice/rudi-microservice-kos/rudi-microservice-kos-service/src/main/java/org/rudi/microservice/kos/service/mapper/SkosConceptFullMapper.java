package org.rudi.microservice.kos.service.mapper;

import java.util.ArrayList;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		SkosConceptTranslationMapper.class, DictionaryListMapper.class, SkosSchemeMapper.class })
public interface SkosConceptFullMapper extends AbstractMapper<SkosConceptEntity, SkosConcept> {

	@Override
	@InheritInverseConfiguration
	@Mapping(target = "ofScheme", ignore = true)
	@Mapping(target = "uuid", ignore = true)
	SkosConceptEntity dtoToEntity(SkosConcept dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	@Mapping(target = "conceptCode", source = "code")
	@Mapping(target = "conceptId", source = "uuid")
	@Mapping(target = "prefLabel", source = "preferedLabels")
	@Mapping(target = "altLabels", source = "alternateLabels")
	SkosConcept entityToDto(SkosConceptEntity entity);

	@InheritConfiguration
	void dtoToEntity(SkosConcept dto, @MappingTarget SkosConceptEntity entity);

	@AfterMapping
	default void updateDtoRelationConcepts(SkosConceptEntity entity, @MappingTarget SkosConcept dto) {
		dto.setBroaderConcepts(new ArrayList<>());
		dto.setNarrowerConcepts(new ArrayList<>());
		dto.setRelativeConcepts(new ArrayList<>());
		dto.setSiblingConcepts(new ArrayList<>());
		if (CollectionUtils.isNotEmpty(entity.getRelationConcepts())) {
			entity.getRelationConcepts().forEach(skosRelationConceptEntity -> {
				if (skosRelationConceptEntity.getType().equals(SkosRelationType.RELATIVE)) {
					dto.getRelativeConcepts().add(entityToDto(skosRelationConceptEntity.getTarget()));
				}
				if (skosRelationConceptEntity.getType().equals(SkosRelationType.NARROWING)) {
					dto.getNarrowerConcepts().add(entityToDto(skosRelationConceptEntity.getTarget()));
				}
				if (skosRelationConceptEntity.getType().equals(SkosRelationType.SIBLING)) {
					dto.getSiblingConcepts().add(entityToDto(skosRelationConceptEntity.getTarget()));
				}
				if (skosRelationConceptEntity.getType().equals(SkosRelationType.BROADER)) {
					dto.getBroaderConcepts().add(entityToDto(skosRelationConceptEntity.getTarget()));
				}
			});
		}
	}
}
