package org.rudi.facet.bpmn.mapper.workflow;

import org.activiti.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProcessDefinitionMapper {

	/**
	 * Converti une définition de processus
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	org.rudi.bpmn.core.bean.ProcessDefinition entityToDto(ProcessDefinition entity);
}
