package org.rudi.facet.apimaccess.helper.search;

import org.junit.jupiter.api.Test;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QueryBuilderTest {

	private final QueryBuilder queryBuilder = new QueryBuilder();

	@Test
	void buildFromAPISearchCriteria() {
		// Exemple extrait du cas d'erreur observé par RUDI-1554
		final APISearchCriteria apiSearchCriteria = new APISearchCriteria()
				.globalId(UUID.fromString("92bfbe72-ecf3-4017-8291-25b9c169bd22"))
				.name("b6ddce4e-bf2d-4d9a-bee5-999d780dfa5d_dwnl")
				.providerCode("PROVIDER_TEST_APIM")
				.providerUuid(UUID.fromString("8b8f28c6-4b22-4fb1-8d9b-21bc4e98ebfc"));
		final var query = queryBuilder.buildFrom(apiSearchCriteria);
		assertThat(query)
				.as("La query ne doit contenir aucun espace superflu")
				.isEqualTo("global_id:92bfbe72-ecf3-4017-8291-25b9c169bd22 name:b6ddce4e-bf2d-4d9a-bee5-999d780dfa5d_dwnl provider_code:PROVIDER_TEST_APIM provider_uuid:8b8f28c6-4b22-4fb1-8d9b-21bc4e98ebfc");
	}

	@Test
	void buildFromApplicationSearchCriteria() {
		final ApplicationSearchCriteria applicationSearchCriteria = new ApplicationSearchCriteria()
				.name("rudi_application");
		final var query = queryBuilder.buildFrom(applicationSearchCriteria);
		assertThat(query).isEqualTo("rudi_application");
	}
}
