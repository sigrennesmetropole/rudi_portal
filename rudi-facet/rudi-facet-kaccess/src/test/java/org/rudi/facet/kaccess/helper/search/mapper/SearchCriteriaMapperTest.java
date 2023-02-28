package org.rudi.facet.kaccess.helper.search.mapper;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rudi.common.core.util.DateTimeUtils.toUTC;

class SearchCriteriaMapperTest {

	private final DateTimeMapper dateTimeMapper = new DateTimeMapper();
	private final SearchCriteriaMapper searchCriteriaMapper = new SearchCriteriaMapper(dateTimeMapper);

	@Test
	void datasetSearchCriteriaToSearchParams_empty() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria();
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams)
				.hasAllNullFieldsOrPropertiesExcept("type", "q", "filterQuery", "sortBy", "sortOrder", "showFacets")
				.hasFieldOrPropertyWithValue("type", EnumSet.of(SearchType.DATASET))
				.hasFieldOrPropertyWithValue("q", "*").hasFieldOrPropertyWithValue("sortBy", "name")
				.hasFieldOrPropertyWithValue("sortOrder", "asc").hasFieldOrPropertyWithValue("showFacets", false);
		assertThat(searchParams.getFilterQuery()).isEmpty();
	}

	@Test
	void datasetSearchCriteriaToSearchParams_globalId() {
		final UUID globalId = UUID.fromString("5141496d-8ae7-4da4-8c32-0a528db9af2e");
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.globalIds(Collections.singletonList(globalId));
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery())
				.containsExactly("rudi_global_id:\"5141496d\\-8ae7\\-4da4\\-8c32\\-0a528db9af2e\"");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_keywords() {
		final List<String> keywords = Arrays.asList("bières", "gratuites");
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().keywords(keywords);
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery()).containsExactly("rudi_keywords_ss:(\"bières\" \"gratuites\")");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_empty_keywords() {
		final List<String> keywords = Collections.emptyList();
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().keywords(keywords);
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery()).isEmpty();
		;
	}

	@Test
	void datasetSearchCriteriaToSearchParams_themes() {
		final List<String> keywords = Arrays.asList("bières", "gratuites");
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().themes(keywords);
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery()).containsExactly("rudi_theme_s:(\"bières\" \"gratuites\")");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_producerNames() {
		final List<String> producerNames = Arrays.asList("Lucas", "Spielberg");
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().producerNames(producerNames);
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery())
				.containsExactly("rudi_producer_organization_name_s:(\"Lucas\" \"Spielberg\")");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_restrictedAccess() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().restrictedAccess(true);
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery())
				.containsExactly("rudi_access_condition_confidentiality_gdpr_sensitive:\"false\"", "rudi_access_condition_confidentiality_restricted_access:\"true\"");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_dates() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.dateDebut(toUTC(LocalDateTime.of(2021, Month.JULY, 28, 8, 30)))
				.dateFin(toUTC(LocalDateTime.of(2021, Month.JULY, 28, 17, 30)));
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery()).containsExactly(
				"rudi_temporal_spread_start_date:[1627453800000000000 TO *]",
				"rudi_temporal_spread_end_date:[* TO 1627486200000000000]");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_doi() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().doi("10.5072/FK2/OFKEB1");
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery()).containsExactly("rudi_doi:\"10.5072\\/FK2\\/OFKEB1\"");
	}

	@Test
	void datasetSearchCriteriaToSearchParams_localId() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.localId("2020.11-Laennec-AQMO-air quality sensors measures");
		final SearchParams searchParams = searchCriteriaMapper
				.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery())
				.containsExactly("rudi_local_id:\"2020.11\\-Laennec\\-AQMO\\-air quality sensors measures\"");
	}

	@ParameterizedTest
	@CsvSource({
			"09/09/2021,           rudi_resource_title:*09\\/09\\/2021* OR rudi_abstract_text:*09\\/09\\/2021*", // one word with handled special characters
			"31/08+,               rudi_resource_title:*31\\/08* OR rudi_abstract_text:*31\\/08*", // one word with not handled special characters
			"911992100 09/09/2021, (rudi_resource_title:*911992100* AND rudi_resource_title:*09\\/09\\/2021*) OR (rudi_abstract_text:*911992100* AND rudi_abstract_text:*09\\/09\\/2021*)", // two words with handled special characters
	})
	void datasetSearchCriteriaToSearchParams_freeText(final String freeText, final String expectedQuery) {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.freeText(freeText);
		final SearchParams searchParams = searchCriteriaMapper.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getQ()).isEqualTo(expectedQuery);
	}

	@Test
	void datasetSearchCriteriaToSearchParamsOrderByScoreOfKeywords() {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.keywords(Arrays.asList("biogaz", "agriculture", "méthane"))
				.orderByScoreOfKeywords(true);
		final SearchParams searchParams = searchCriteriaMapper.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);
		assertThat(searchParams.getFilterQuery())
				.as("Le paramètre fq n'est pas utilisé")
				.isEmpty()
		;
		assertThat(searchParams)
				.as("Le paramètre q est utilisé pour une recherche par keywords non stricte (on veut conserver les JDD sans mots-clés en commun)")
				.hasFieldOrPropertyWithValue("q", "rudi_keywords:(\"biogaz\" \"agriculture\" \"méthane\") OR *:*")
				.as("Les résultats sont ordonnés par score décroissant, pour avoir les JDD avec le plus de mots-clés en commun en premier")
				.hasFieldOrPropertyWithValue("sortBy", "score")
				.hasFieldOrPropertyWithValue("sortOrder", "desc")
		;
	}

}
