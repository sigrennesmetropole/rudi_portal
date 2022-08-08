/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.FormSectionDefinition;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.facet.bpmn.entity.form.FormSectionDefinitionEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		SectionDefinitionMapper.class })
public interface FormSectionDefinitionMapper
		extends AbstractMapper<FormSectionDefinitionEntity, FormSectionDefinition> {

	@Override
	@Mapping(ignore = true, target = "sectionDefinition")
	FormSectionDefinitionEntity dtoToEntity(FormSectionDefinition dto);

	@Override
	@Mapping(ignore = true, target = "sectionDefinition")
	void dtoToEntity(FormSectionDefinition dto, @MappingTarget FormSectionDefinitionEntity entity);
}
