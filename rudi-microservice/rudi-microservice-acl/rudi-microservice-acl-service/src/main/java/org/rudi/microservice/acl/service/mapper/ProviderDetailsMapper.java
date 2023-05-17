package org.rudi.microservice.acl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.ProviderDetailsDto;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class, UserInfoEndpointMapper.class })
public interface ProviderDetailsMapper {
	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Mapping(source = "configurationMetadata", target = "configurationMetadata", ignore = true)
	ProviderDetailsDto entityToDto(ClientRegistration.ProviderDetails entity);
}
