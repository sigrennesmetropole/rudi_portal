/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.facet.bpmn.entity.form.FormDefinitionEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		FormSectionDefinitionMapper.class })
public interface FormDefinitionMapper extends AbstractMapper<FormDefinitionEntity, FormDefinition> {

	@Override
	@Mapping(ignore = true, target = "formSectionDefinitions")
	FormDefinitionEntity dtoToEntity(FormDefinition dto);

	@Override
	@Mapping(ignore = true, target = "formSectionDefinitions")
	void dtoToEntity(FormDefinition dto, @MappingTarget FormDefinitionEntity entity);

}
