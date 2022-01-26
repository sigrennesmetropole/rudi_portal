/**
 * 
 */
package org.rudi.microservice.kos.service.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.kos.core.bean.SkosScheme;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		SkosSchemeTranslationMapper.class })
public interface SkosSchemeMapper extends AbstractMapper<SkosSchemeEntity, SkosScheme> {

	@Override
	@InheritInverseConfiguration
	SkosSchemeEntity dtoToEntity(SkosScheme dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	@Mapping(target = "topConcepts", ignore = true, source = "topConcepts")
	@Mapping(target = "schemeCode", source = "code")
	@Mapping(target = "schemeId", source = "uuid")
	SkosScheme entityToDto(SkosSchemeEntity entity);


	@Override
	@InheritConfiguration
	void dtoToEntity(SkosScheme dto, @MappingTarget SkosSchemeEntity entity);
}
