package org.rudi.microservice.acl.service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.UserInfoEndpointDto;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface UserInfoEndpointMapper {
	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	UserInfoEndpointDto entityToDto(ClientRegistration.ProviderDetails.UserInfoEndpoint entity);
}
