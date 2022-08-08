/**
 * 
 */
package org.rudi.facet.bpmn.mapper.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.FieldDefinition;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.facet.bpmn.bean.form.FormDefinition;
import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.helper.form.FormDefinitionHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SectionMapper {

	@Autowired
	private FormDefinitionHelper formDefinitionHelper;

	/**
	 * @param dto dto to transform to entity
	 * @return entity
	 */
	@Mapping(source = "label", target = "label")
	@Mapping(source = "name", target = "name")
	public abstract Section entityToDto(SectionDefinitionEntity sectionDefinitionEntity) throws FormDefinitionException;

	@AfterMapping
	public void afterMapping(SectionDefinitionEntity sectionDefinitionEntity, @MappingTarget Section section)
			throws FormDefinitionException {
		if (StringUtils.isNotEmpty(sectionDefinitionEntity.getDefinition())) {
			FormDefinition formDefinition = formDefinitionHelper.hydrateForm(sectionDefinitionEntity.getDefinition());
			if (formDefinition != null && CollectionUtils.isNotEmpty(formDefinition.getFieldDefinitions())) {
				List<Field> fields = new ArrayList<>();
				for (FieldDefinition fieldDefinition : formDefinition.getFieldDefinitions()) {
					Field field = new Field();
					field.setDefinition(fieldDefinition);
					fields.add(field);
				}
				section.setFields(fields);
			}
		}
	}
}
