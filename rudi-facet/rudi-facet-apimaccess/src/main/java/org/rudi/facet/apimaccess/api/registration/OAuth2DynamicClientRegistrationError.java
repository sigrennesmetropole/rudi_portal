package org.rudi.facet.apimaccess.api.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@NoArgsConstructor
@Getter
class OAuth2DynamicClientRegistrationError {
	@Nullable
	String error;
	@Nullable
	@JsonProperty("error_description")
	String errorDescription;
}
