package org.rudi.facet.apimaccess.api.application;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.apimaccess.api.AbstractManagerAPI;
import org.rudi.facet.apimaccess.api.ManagerAPIProperties;
import org.rudi.facet.apimaccess.api.MonoUtils;
import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationKey;
import org.rudi.facet.apimaccess.bean.ApplicationKeyGenerateRequest;
import org.rudi.facet.apimaccess.bean.ApplicationKeys;
import org.rudi.facet.apimaccess.bean.ApplicationScope;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationToken;
import org.rudi.facet.apimaccess.bean.ApplicationTokenGenerateRequest;
import org.rudi.facet.apimaccess.bean.Applications;
import org.rudi.facet.apimaccess.bean.EndpointKeyType;
import org.rudi.facet.apimaccess.bean.OauthGrantType;
import org.rudi.facet.apimaccess.exception.APIEndpointException;
import org.rudi.facet.apimaccess.exception.ApplicationKeysNotFoundException;
import org.rudi.facet.apimaccess.exception.ApplicationOperationException;
import org.rudi.facet.apimaccess.exception.ApplicationTokenGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_WEBCLIENT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.APPLICATION_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.KEYMAPPING_ID;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.LIMIT;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.OFFSET;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.QUERY;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.SORT_BY;
import static org.rudi.facet.apimaccess.constant.QueryParameterKey.SORT_ORDER;
import static org.rudi.facet.apimaccess.service.impl.APIsServiceImpl.getInterfaceContractFromContext;

@Component
public class ApplicationOperationAPI extends AbstractManagerAPI {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationOperationAPI.class);

	private static final String APPLICATION_PATH = "/applications"; //NOSONAR
	private static final String APPLICATION_GET_PATH = APPLICATION_PATH + "/{applicationId}";
	private static final String APPLICATION_GENERATE_KEYS = APPLICATION_GET_PATH + "/generate-keys";
	private static final String APPLICATION_OAUTH_KEYS = APPLICATION_GET_PATH + "/oauth-keys";
	private static final String APPLICATION_GENERATE_TOKEN = APPLICATION_OAUTH_KEYS + "/{keyMappingId}/generate-token";


	private final String temporaryDirectory;

	ApplicationOperationAPI(
			@Qualifier(API_MACCESS_WEBCLIENT) WebClient webClient,
			ManagerAPIProperties managerAPIProperties,
			@Value("${temporary.directory:${java.io.tmpdir}}") String temporaryDirectory
	) {
		super(webClient, managerAPIProperties);
		this.temporaryDirectory = temporaryDirectory;
	}

	public Applications searchApplication(ApplicationSearchCriteria applicationSearchCriteria, String username) throws ApplicationOperationException {

		final Mono<Applications> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(APPLICATION_PATH),
				uriBuilder -> uriBuilder
						.queryParam(OFFSET, applicationSearchCriteria.getOffset())
						.queryParam(LIMIT, applicationSearchCriteria.getLimit())
						.queryParam(SORT_BY, applicationSearchCriteria.getSortBy())
						.queryParam(SORT_ORDER, applicationSearchCriteria.getSortOrder())
						.queryParam(QUERY, applicationSearchCriteria.getQuery())
						.build())
				.retrieve()
				.bodyToMono(Applications.class);
		return MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(applicationSearchCriteria, username, e));
	}

	public Application getApplication(String applicationId, String username) throws ApplicationOperationException {
		final Mono<Application> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(APPLICATION_GET_PATH), Map.of(APPLICATION_ID, applicationId))
				.retrieve()
				.bodyToMono(Application.class);
		return MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(applicationId, username, e));
	}

	public Application createApplication(Application application, String username) throws ApplicationOperationException {
		final Mono<Application> mono = populateRequestWithRegistrationId(HttpMethod.POST, username, buildDevPortalURIPath(APPLICATION_PATH))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(application), Application.class)
				.retrieve()
				.bodyToMono(Application.class);
		return MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(application, username, e));
	}

	public void deleteApplication(String applicationId, String username) throws ApplicationOperationException {
		final Mono<Void> mono = populateRequestWithRegistrationId(HttpMethod.DELETE, username, buildDevPortalURIPath(APPLICATION_GET_PATH), Map.of(APPLICATION_ID, applicationId))
				.retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(applicationId, username, e));
	}

	public void generateKeysApplication(String applicationId, String username) throws ApplicationOperationException {
		ApplicationKeyGenerateRequest applicationKeyGenerateRequest = new ApplicationKeyGenerateRequest()
				.keyManager("Resident Key Manager")
				.keyType(EndpointKeyType.PRODUCTION)
				.scopes(Collections.singletonList(ApplicationScope.DEFAULT))
				.grantTypesToBeSupported(Arrays.asList(OauthGrantType.URN_IETF_PARAMS_OAUTH_GRANT_TYPE_JWT_BEARER, OauthGrantType.PASSWORD, OauthGrantType.CLIENT_CREDENTIALS, OauthGrantType.REFRESH_TOKEN))
				.validityTime("3600");

		final Mono<Void> mono = populateRequestWithRegistrationId(HttpMethod.POST, username, buildDevPortalURIPath(APPLICATION_GENERATE_KEYS), Map.of(APPLICATION_ID, applicationId))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationKeyGenerateRequest), ApplicationKeyGenerateRequest.class)
				.retrieve()
				.bodyToMono(Void.class);
		MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(applicationId, username, e));
	}

	public ApplicationKeys getApplicationKeyList(String applicationId, String username) throws ApplicationOperationException {
		final Mono<ApplicationKeys> mono = populateRequestWithRegistrationId(HttpMethod.GET, username, buildDevPortalURIPath(APPLICATION_OAUTH_KEYS), Map.of(APPLICATION_ID, applicationId))
				.retrieve()
				.bodyToMono(ApplicationKeys.class);
		return MonoUtils.blockOrThrow(mono, e -> new ApplicationOperationException(applicationId, username, e));
	}

	public ApplicationToken generateApplicationToken(String applicationId, String keyMappingId, ApplicationTokenGenerateRequest applicationTokenGenerateRequest, String username) throws ApplicationTokenGenerationException {
		final Mono<ApplicationToken> mono = populateRequestWithRegistrationId(HttpMethod.POST, username, buildDevPortalURIPath(APPLICATION_GENERATE_TOKEN),
				Map.of(APPLICATION_ID, applicationId, KEYMAPPING_ID, keyMappingId))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(applicationTokenGenerateRequest), ApplicationTokenGenerateRequest.class)
				.retrieve()
				.bodyToMono(ApplicationToken.class);
		return MonoUtils.blockOrThrow(mono, e -> new ApplicationTokenGenerationException(applicationId, keyMappingId, applicationTokenGenerateRequest, username, e));
	}

	public DocumentContent getAPIContent(String context, String version, String applicationId, String username) throws ApplicationOperationException, ApplicationKeysNotFoundException, APIEndpointException, IOException, ApplicationTokenGenerationException {
		ApplicationKeys applicationKeys = getApplicationKeyList(applicationId, username);
		if (applicationKeys.getCount() == 0) {
			throw new ApplicationKeysNotFoundException(applicationId, username);
		}
		Optional<ApplicationKey> optionalApplicationKey = applicationKeys.getList()
				.stream()
				.filter(apk -> apk.getKeyType() == EndpointKeyType.PRODUCTION)
				.findFirst();
		if (optionalApplicationKey.isEmpty()) {
			throw new ApplicationKeysNotFoundException(applicationId, username, EndpointKeyType.PRODUCTION);
		}

		ApplicationKey applicationKey = optionalApplicationKey.get();
		ApplicationTokenGenerateRequest applicationTokenGenerateRequest = new ApplicationTokenGenerateRequest()
				.consumerSecret(applicationKey.getConsumerSecret())
				.validityPeriod(3600L)
				.scopes(Collections.emptyList());
		ApplicationToken applicationToken = generateApplicationToken(applicationId, applicationKey.getKeyMappingId(), applicationTokenGenerateRequest, username);

		final var apiAccessUrl = buildAPIAccessUrl(context, version);
		ClientResponse response = webClient.get()
				.uri(apiAccessUrl)
				.headers(httpHeaders -> httpHeaders.setBearerAuth(applicationToken.getAccessToken()))
				.exchange()
				.block();

		if (response == null) {
			throw new APIEndpointException(apiAccessUrl);
		}

		if (response.statusCode().isError()) {
			throw new APIEndpointException(apiAccessUrl, response.statusCode());
		}

		Flux<DataBuffer> dataBufferFlux = response.bodyToFlux(DataBuffer.class);
		HttpHeaders httpHeaders = response.headers().asHttpHeaders();

		String fileName = httpHeaders.getContentDisposition().getFilename();
		String contentType = httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE);

		// il peut arriver qu'on ne puisse pas récupérer le nom du fichier téléchargé
		if (StringUtils.isEmpty(fileName)) {
			final String defaultFileName = getInterfaceContractFromContext(context);
			LOGGER.error("Impossible de détecter le nom du fichier téléchargé. Par défaut on utilise \"{}\".", defaultFileName);
			fileName = defaultFileName;
		}

		final File tempFile = File.createTempFile("rudi", FilenameUtils.getExtension(fileName), new File(temporaryDirectory));

		DataBufferUtils.write(dataBufferFlux, tempFile.toPath())
				.block();

		return new DocumentContent(fileName, contentType, tempFile);
	}


}
