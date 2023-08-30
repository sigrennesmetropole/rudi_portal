package org.rudi.facet.kmedia.helper.search.mapper;

import org.junit.jupiter.api.Test;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.bean.MediaSearchCriteria;

import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MediaSearchCriteriaMapperUT {
	private final String mediaDataAlias = "rudi_media_test";
	private final MediaSearchCriteriaMapper mapper = new MediaSearchCriteriaMapper(mediaDataAlias);

	@Test
	void mediaSearchCriteriaToSearchParams() {
		final MediaSearchCriteria mediaSearchCriteria = new MediaSearchCriteria()
				.mediaAuthorIdentifier(UUID.fromString("5596b5b2-b227-4c74-a9a1-719e7c1008c7"))
				.mediaAuthorAffiliation(MediaOrigin.PROVIDER)
				.limit(12)
				.offset(24)
				.kindOfData(KindOfData.LOGO)
				.order("asc");

		final SearchParams searchParams = mapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteria);

		assertThat(searchParams)
				.hasFieldOrPropertyWithValue("type", EnumSet.of(SearchType.DATASET))
				.hasFieldOrPropertyWithValue("q", "authorIdentifier:\"5596b5b2\\-b227\\-4c74\\-a9a1\\-719e7c1008c7\" AND authorAffiliation:\"provider\" AND kindOfData:\"LOGO\"")
				.hasFieldOrPropertyWithValue("subtree", mediaDataAlias)
				.hasFieldOrPropertyWithValue("sortBy", "name")
				.hasFieldOrPropertyWithValue("sortOrder", "asc")
				.hasFieldOrPropertyWithValue("perPage", 12)
				.hasFieldOrPropertyWithValue("start", 24)
				.hasFieldOrPropertyWithValue("showRelevance", null)
				.hasFieldOrPropertyWithValue("showFacets", false)
		;
	}
}
