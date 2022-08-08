/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.facet.bpmn.entity.form.ProcessFormDefinitionEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		FormSectionDefinitionMapper.class })
public interface ProcessFormDefinitionMapper
		extends AbstractMapper<ProcessFormDefinitionEntity, ProcessFormDefinition> {

	@Override
	@Mapping(ignore = true, target = "formDefinition")
	ProcessFormDefinitionEntity dtoToEntity(ProcessFormDefinition dto);

	@Override
	@Mapping(ignore = true, target = "formDefinition")
	void dtoToEntity(ProcessFormDefinition dto, @MappingTarget ProcessFormDefinitionEntity entity);

}
