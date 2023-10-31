package org.rudi.facet.kos.helper;

import java.util.Collections;

import org.rudi.microservice.kos.core.bean.SimpleSkosConceptPageResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KosHelper {

	private static final String LICENCE_SCHEME_CODE = "scheme-licence";
	private static final String KEYWORD_SCHEME_CODE = "scheme-keyword";
	private static final String THEME_CONCEPT_ROLE = "theme";

	private final WebClient kosWebClient;

	private final KosProperties kosProperties;

	public boolean skosConceptExists(SkosConceptSearchCriteria searchCriteria) {
		final SimpleSkosConceptPageResult result = searchSkosConcepts(searchCriteria);
		return (result != null && result.getTotal() >= 1);
	}

	public boolean skosConceptLicenceExists(String code) {
		SkosConceptSearchCriteria searchCriteria = SkosConceptSearchCriteria.builder()
				.codes(Collections.singletonList(code)).schemes(Collections.singletonList(LICENCE_SCHEME_CODE)).build();
		return skosConceptExists(searchCriteria);
	}

	public boolean skosConceptThemeExists(String code) {
		SkosConceptSearchCriteria searchCriteria = SkosConceptSearchCriteria.builder()
				.codes(Collections.singletonList(code)).schemes(Collections.singletonList(KEYWORD_SCHEME_CODE))
				.roles(Collections.singletonList(THEME_CONCEPT_ROLE)).build();
		return skosConceptExists(searchCriteria);
	}

	private SimpleSkosConceptPageResult searchSkosConcepts(SkosConceptSearchCriteria searchCriteria) {
		return kosWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(kosProperties.getSearchSkosConceptsPath())
						.queryParam("limit", searchCriteria.getLimit()).queryParam("offset", searchCriteria.getOffset())
						.queryParam("order", searchCriteria.getOrder()).queryParam("lang", searchCriteria.getLang())
						.queryParam("text", searchCriteria.getText()).queryParam("types", searchCriteria.getTypes())
						.queryParam("roles", searchCriteria.getRoles()).queryParam("codes", searchCriteria.getCodes())
						.queryParam("schemes", searchCriteria.getSchemes())
						.queryParam("labels", searchCriteria.getLabels()).build())
				.retrieve().bodyToMono(SimpleSkosConceptPageResult.class).block();
	}
}
