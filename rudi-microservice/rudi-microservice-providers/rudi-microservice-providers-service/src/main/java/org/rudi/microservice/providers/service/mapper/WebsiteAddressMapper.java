/**
 * 
 */
package org.rudi.microservice.providers.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.providers.core.bean.WebsiteAddress;
import org.rudi.microservice.providers.storage.entity.address.WebsiteAddressEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface WebsiteAddressMapper extends AbstractMapper<WebsiteAddressEntity, WebsiteAddress> {

	@Override
	@InheritInverseConfiguration
	WebsiteAddressEntity dtoToEntity(WebsiteAddress dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	WebsiteAddress entityToDto(WebsiteAddressEntity entity);

}
