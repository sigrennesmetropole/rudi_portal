package org.rudi.microservice.kos.service.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		SkosConceptTranslationMapper.class, DictionaryListMapper.class, SkosSchemeMapper.class })
public interface SkosConceptMapper extends AbstractMapper<SkosConceptEntity, SkosConcept> {

	@Override
	@InheritInverseConfiguration
	@Mapping(target = "ofScheme", ignore = true)
	@Mapping(target = "relationConcepts", ignore = true)
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

	@Override
	@InheritConfiguration
	void dtoToEntity(SkosConcept dto, @MappingTarget SkosConceptEntity entity);
}
