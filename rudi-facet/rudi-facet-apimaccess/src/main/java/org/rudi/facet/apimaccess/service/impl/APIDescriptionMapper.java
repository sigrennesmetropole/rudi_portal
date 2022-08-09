package org.rudi.facet.apimaccess.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.APIEndpointConfig;
import org.rudi.facet.apimaccess.bean.APIEndpointConfigHttp;
import org.rudi.facet.apimaccess.bean.APIEndpointType;
import org.rudi.facet.apimaccess.bean.APITransportEnum;
import org.rudi.facet.apimaccess.bean.GatewayEnvironmentsEnum;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.wso2.carbon.apimgt.rest.api.publisher.API;

import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.EXTENSION;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.GLOBAL_ID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.INTERFACE_CONTRACT;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.MEDIA_UUID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_CODE;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_UUID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.DEFAULT_API_VERSION;
import static org.rudi.facet.apimaccess.helper.api.APIContextHelper.buildAPIContext;

@Component
class APIDescriptionMapper {

	private static final String DECODED_SPACE = " ";
	private static final String ENCODED_SPACE = "%20";

	API map(APIDescription apiDescription, LimitingPolicies limitingPolicies, String[] apiCategories) {
		final var api = new API()
				.policies(limitingPolicies.getList().stream().map(LimitingPolicy::getName).collect(Collectors.toList()))
				.categories(Arrays.asList(apiCategories))
				.isDefaultVersion(true)
				.transport(Arrays.asList(APITransportEnum.HTTP.getValue(), APITransportEnum.HTTPS.getValue()))
				.gatewayEnvironments(Collections.singletonList(GatewayEnvironmentsEnum.PRODUCTION_AND_SANDBOX.getValue()))
				.version(StringUtils.isNotEmpty(apiDescription.getVersion()) ? apiDescription.getVersion() : DEFAULT_API_VERSION);
		map(apiDescription, api);
		return api;
	}

	void map(APIDescription apiDescription, API api) {
		api
				.name(apiDescription.getName())
				.endpointConfig(new APIEndpointConfigHttp()
						.endpointType(APIEndpointType.HTTP)
						.productionEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl()))
						.sandboxEndpoints(new APIEndpointConfig().url(apiDescription.getEndpointUrl())))
				.context(buildAPIContext(apiDescription))
				.additionalProperties(buildAdditionalProperties(apiDescription));
	}

	@Nonnull
	private MediaType computeMediaType(APIDescription apiDescription) {
		final MediaType mediaType;
		try {
			mediaType = MediaType.parseMediaType(apiDescription.getMediaType());
		} catch (InvalidMediaTypeException e) {
			throw new IllegalArgumentException("Le media type est invalide : " + apiDescription.getMediaType(), e);
		}
		return mediaType;
	}

	@Nonnull
	private Map<String, String> buildAdditionalProperties(APIDescription apiDescription) {
		final Map<String, String> additionalProperties = new HashMap<>(Map.of(
				PROVIDER_UUID, apiDescription.getProviderUuid().toString(),
				PROVIDER_CODE, apiDescription.getProviderCode(),
				GLOBAL_ID, apiDescription.getGlobalId().toString(),
				MEDIA_UUID, apiDescription.getMediaUuid().toString(),
				INTERFACE_CONTRACT, apiDescription.getInterfaceContract(),
				EXTENSION, computeMediaType(apiDescription).getSubtype()));

		if (apiDescription.getAdditionalProperties() != null) {
			additionalProperties.putAll(apiDescription.getAdditionalProperties());
		}

		return encodeAdditionalPropertiesForPublisher(additionalProperties);
	}

	/**
	 * @return la map additionalProperties contenant uniquement des propriétés acceptables en tant que "API Properties" par WSO2 Publisher
	 */
	private Map<String, String> encodeAdditionalPropertiesForPublisher(Map<String, String> additionalProperties) {
		return additionalProperties.entrySet().stream()
				.collect(Collectors.toMap(entry -> encodeAdditionalPropertyNameForPublisher(entry.getKey()), Map.Entry::getValue))
				;
	}

	/**
	 * @return le nom de la propriété acceptable par WSO2 Publisher (les espaces ne sont pas acceptés notamment, mais les caractères accentués le sont)
	 */
	private String encodeAdditionalPropertyNameForPublisher(String value) {
		if (value == null) {
			return null;
		}
		return value.replace(DECODED_SPACE, ENCODED_SPACE);
		// Si on souhaite rendre visible la propriété dans devportal (case à cocher dans Publisher), il faut suffixer
		// le nom de la propriété par "__display"
	}
}
