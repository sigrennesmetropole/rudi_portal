/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.facet.bpmn.entity.form.FormDefinitionEntity;
import org.rudi.facet.bpmn.entity.form.FormSectionDefinitionEntity;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FormMapper {

	@Autowired
	private SectionMapper sectionMapper;

	/**
	 * 
	 * @param formDefinitionEntity
	 * @return
	 * @throws FormDefinitionException
	 */
	public abstract Form entityToDto(FormDefinitionEntity formDefinitionEntity) throws FormDefinitionException;

	/**
	 * 
	 * @param formDefinitionEntity
	 * @param form
	 * @throws FormDefinitionException
	 */
	@AfterMapping
	public void afterMapping(FormDefinitionEntity formDefinitionEntity, @MappingTarget Form form)
			throws FormDefinitionException {
		if (formDefinitionEntity != null
				&& CollectionUtils.isNotEmpty(formDefinitionEntity.getFormSectionDefinitions())) {
			for (FormSectionDefinitionEntity formSectionDefinitionEntity : formDefinitionEntity
					.getFormSectionDefinitions()) {
				Section section = sectionMapper.entityToDto(formSectionDefinitionEntity.getSectionDefinition());
				section.setReadOnly(formSectionDefinitionEntity.isReadOnly());
				form.addSectionsItem(section);
			}
		}
	}
}
