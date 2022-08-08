/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionDefinitionMapper extends AbstractMapper<SectionDefinitionEntity, SectionDefinition> {

}
