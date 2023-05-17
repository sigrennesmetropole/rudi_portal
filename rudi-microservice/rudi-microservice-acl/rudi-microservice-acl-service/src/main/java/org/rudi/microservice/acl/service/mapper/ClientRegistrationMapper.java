package org.rudi.microservice.acl.service.mapper;

import java.util.Locale;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.AuthorizationGrantTypeDto;
import org.rudi.microservice.acl.core.bean.ClientAuthenticationMethodDto;
import org.rudi.microservice.acl.core.bean.ClientRegistrationDto;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class, ProviderDetailsMapper.class })
public interface ClientRegistrationMapper {
	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Mapping(source = "clientAuthenticationMethod.value", target = "clientAuthenticationMethod.value", ignore = true)
	@Mapping(source = "authorizationGrantType.value", target = "authorizationGrantType.value", ignore = true)
	ClientRegistrationDto entityToDto(ClientRegistration entity);

	@AfterMapping
	default void updateEnum(ClientRegistration clientRegistration, @MappingTarget ClientRegistrationDto registrationDto) {
		AuthorizationGrantTypeDto authorizationGrantTypeDto = registrationDto.getAuthorizationGrantType();
		if (clientRegistration.getAuthorizationGrantType() != null && clientRegistration.getAuthorizationGrantType().getValue() != null) {
			authorizationGrantTypeDto.setValue(Enum.valueOf(org.rudi.microservice.acl.core.bean.AuthorizationGrantTypeDto.ValueEnum.class, clientRegistration.getAuthorizationGrantType().getValue().toUpperCase(Locale.ROOT)));
		}

		ClientAuthenticationMethodDto authenticationMethodDto = registrationDto.getClientAuthenticationMethod();
		if (clientRegistration.getClientAuthenticationMethod() != null && clientRegistration.getClientAuthenticationMethod().getValue() != null) {
			authenticationMethodDto.setValue(Enum.valueOf(ClientAuthenticationMethodDto.ValueEnum.class, clientRegistration.getClientAuthenticationMethod().getValue().toUpperCase(Locale.ROOT)));
		}
	}
}