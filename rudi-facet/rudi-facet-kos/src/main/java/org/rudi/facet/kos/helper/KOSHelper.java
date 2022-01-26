package org.rudi.facet.kos.helper;

import org.rudi.microservice.kos.core.bean.SimpleSkosConceptPageResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Component
public class KOSHelper {

	private static final String LICENCE_SCHEME_CODE = "scheme-licence";
	private static final String KEYWORD_SCHEME_CODE = "scheme-keyword";
	private static final String THEME_CONCEPT_ROLE = "theme";

	private final WebClient loadBalancedWebClient;
	private final String searchSkosConceptsPath;

	public KOSHelper(
			@Qualifier("rudi_oauth2_builder") WebClient.Builder loadBalancedWebClientBuilder,
			@Value("${rudi.facet.kos.service.api.url:${rudi.facet.kos.service.url:lb://RUDI-KOS}/kos/v1}") String apiUrl,
			@Value("${rudi.facet.kos.service.api.searchSkosConcepts.path:/skosConcepts}") String searchSkosConceptsPath) {
		this.loadBalancedWebClient = loadBalancedWebClientBuilder
				.baseUrl(apiUrl)
				.build();
		this.searchSkosConceptsPath = searchSkosConceptsPath;
	}

	public boolean skosConceptExists(SkosConceptSearchCriteria searchCriteria) {
		final SimpleSkosConceptPageResult result = searchSkosConcepts(searchCriteria);
		return (result != null && result.getTotal() >= 1);
	}

	public boolean skosConceptLicenceExists(String code) {
		SkosConceptSearchCriteria searchCriteria = SkosConceptSearchCriteria.builder()
				.codes(Collections.singletonList(code))
				.schemes(Collections.singletonList(LICENCE_SCHEME_CODE))
				.build();
		return skosConceptExists(searchCriteria);
	}

	public boolean skosConceptThemeExists(String code) {
		SkosConceptSearchCriteria searchCriteria = SkosConceptSearchCriteria.builder()
				.codes(Collections.singletonList(code))
				.schemes(Collections.singletonList(KEYWORD_SCHEME_CODE))
				.roles(Collections.singletonList(THEME_CONCEPT_ROLE))
				.build();
		return skosConceptExists(searchCriteria);
	}

	private SimpleSkosConceptPageResult searchSkosConcepts(SkosConceptSearchCriteria searchCriteria) {
		return loadBalancedWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(searchSkosConceptsPath)
						.queryParam("limit", searchCriteria.getLimit())
						.queryParam("offset", searchCriteria.getOffset())
						.queryParam("order", searchCriteria.getOrder())
						.queryParam("lang", searchCriteria.getLang())
						.queryParam("text", searchCriteria.getText())
						.queryParam("types", searchCriteria.getTypes())
						.queryParam("roles", searchCriteria.getRoles())
						.queryParam("codes", searchCriteria.getCodes())
						.queryParam("schemes", searchCriteria.getSchemes())
						.queryParam("labels", searchCriteria.getLabels())
						.build())
				.retrieve()
				.bodyToMono(SimpleSkosConceptPageResult.class)
				.block();
	}
}
