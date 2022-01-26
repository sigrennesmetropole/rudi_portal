/**
 * 
 */
package org.rudi.microservice.template.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.template.core.bean.Template;
import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface TemplateMapper extends AbstractMapper<TemplateEntity, Template> {

	@Override
	@InheritInverseConfiguration
	TemplateEntity dtoToEntity(Template dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	Template entityToDto(TemplateEntity entity);

}
