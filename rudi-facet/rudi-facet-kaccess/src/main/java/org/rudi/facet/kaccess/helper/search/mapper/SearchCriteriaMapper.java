package org.rudi.facet.kaccess.helper.search.mapper;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.search.mapper.DatasetSearchCriteriaMapper;
import org.rudi.facet.dataverse.api.search.mapper.SortParam;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.helper.query.FilterQuery;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DOI;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GDPR_SENSITIVE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GLOBAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LOCAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESTRICTED_ACCESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TEMPORAL_SPREAD_END_DATE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TEMPORAL_SPREAD_START_DATE;

/**
 * Mapper qui construit la requete SOLR pour le dataverse à partir des critères de recherche définis dans DatasetSearchCriteria.
 * <p>
 * L'API derecherche du dataverse (qui s'appuie sur SOLR) est décrite ici : https://guides.dataverse.org/en/latest/api/search.html
 * <p>
 * La syntaxe des requêtes SOLR est décrite ici : https://solr.apache.org/guide/8_5/common-query-parameters.html
 */
@Component
@RequiredArgsConstructor
public class SearchCriteriaMapper extends DatasetSearchCriteriaMapper {

	private static final String DATAVERSE_DATE_PROPERTY = "date";

	private static final String DATAVERSE_NAME_PROPERTY = "name";

	private static final String DATAVERSE_SCORE_PROPERTY = "score";

	/**
	 * Liste des caractères qui sont considérés comme des séparateurs par le ClassicTokenizer
	 */
	private static final String DELIMITER_FOR_CLASSIC_TOKENIZER_REGEX = "[^-/_A-Za-zÀ-ÖØ-öø-ÿ0-9]";

	private static final FieldSpec[] FREE_TEXT_FIELDS = { RudiMetadataField.RESOURCE_TITLE,
			RudiMetadataField.SYNOPSIS_TEXT };
	private static final String SCORE_DESC_ORDER = "-" + DATAVERSE_SCORE_PROPERTY;

	private final DateTimeMapper dateTimeMapper;

	@Value("${dataverse.api.rudi.data.alias}")
	private String rudiAlias;

	public SearchParams datasetSearchCriteriaToSearchParams(DatasetSearchCriteria datasetSearchCriteria,
			boolean withFacets) {

		// tri
		final var sortParam = extractSortParams(datasetSearchCriteria);

		return SearchParams.builder().q(getQuery(datasetSearchCriteria))
				.type(EnumSet.of(SearchType.DATASET)).subtree(rudiAlias).start(datasetSearchCriteria.getOffset())
				.perPage(datasetSearchCriteria.getLimit()).filterQuery(createFilterQueryFrom(datasetSearchCriteria))
				.sortBy(sortParam.field).sortOrder(sortParam.order.stringValue).showFacets(withFacets).build();
	}

	@Nonnull
	private String getQuery(DatasetSearchCriteria datasetSearchCriteria) {
		if (BooleanUtils.isTrue(datasetSearchCriteria.getOrderByScoreOfKeywords())) {
			return getQueryFromKeywords(datasetSearchCriteria);
		} else {
			return getQueryFromFreeText(datasetSearchCriteria);
		}
	}

	@Nonnull
	private String getQueryFromKeywords(DatasetSearchCriteria datasetSearchCriteria) {
		final var freeText = datasetSearchCriteria.getFreeText();
		if (StringUtils.isNotEmpty(freeText)) {
			throw new IllegalArgumentException("Cannot use freeText criterion when orderByScoreOfKeywords is true");
		}
		final var keywords = datasetSearchCriteria.getKeywords();
		if (CollectionUtils.isEmpty(keywords)) {
			throw new IllegalArgumentException("Missing keywords criterion whereas orderByScoreOfKeywords is true");
		}
		return new FilterQuery()
				.add(RudiMetadataField.KEYWORDS, keywords)
				.addAnyFieldWithAnyValue()
				.joinWithOr();
	}

	@Nonnull
	protected String getQueryFromFreeText(DatasetSearchCriteria datasetSearchCriteria) {
		final var freeText = datasetSearchCriteria.getFreeText();

		if (StringUtils.isNotBlank(freeText)) {
			// recherche texte libre : on cherche sa présence dans le titre OU le résumé court (synopsis)
			final String sanitizedFreeText = sanitize(freeText);
			final String[] terms = sanitizedFreeText.split(" ");

			val query = new FilterQuery();
			for (final FieldSpec field : FREE_TEXT_FIELDS) {
				val fieldQuery = new FilterQuery();
				for (final String term : terms) {
					fieldQuery.addWithWildcard(field, term);
				}
				query.add(fieldQuery.joinWithAnd());
			}
			return query.joinWithOr();
		}

		// q est obligatoire dans la requete solr
		return FilterQuery.ANY_VALUE;
	}

	@Nonnull
	private String sanitize(@Nonnull final String freeText) {
		return freeText.replaceAll(DELIMITER_FOR_CLASSIC_TOKENIZER_REGEX, " ").replaceAll(" +", " ").trim();
	}

	@Nonnull
	private FilterQuery createFilterQueryFrom(DatasetSearchCriteria datasetSearchCriteria) {
		val fqFilter = new FilterQuery().withExactMatch();

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getKeywords()) && BooleanUtils.isNotTrue(datasetSearchCriteria.getOrderByScoreOfKeywords())) {
			fqFilter.add(RudiMetadataField.KEYWORDS.getIndex(), datasetSearchCriteria.getKeywords());
		}

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getThemes())) {
			fqFilter.add(RudiMetadataField.THEME.getIndex(), datasetSearchCriteria.getThemes());
		}

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getProducerNames())) {
			fqFilter.add(RudiMetadataField.PRODUCER_ORGANIZATION_NAME.getIndex(),
					datasetSearchCriteria.getProducerNames());
		}

		if (datasetSearchCriteria.getDateDebut() != null) {
			val minTimestamp = dateTimeMapper.toDataverseTimestamp(datasetSearchCriteria.getDateDebut());
			fqFilter.add(TEMPORAL_SPREAD_START_DATE, minTimestamp, FilterQuery.ANY_VALUE);
		}

		if (datasetSearchCriteria.getDateFin() != null) {
			val maxTimestamp = dateTimeMapper.toDataverseTimestamp(datasetSearchCriteria.getDateFin());
			fqFilter.add(TEMPORAL_SPREAD_END_DATE, FilterQuery.ANY_VALUE, maxTimestamp);
		}

		final var globalIds = datasetSearchCriteria.getGlobalIds();
		if (globalIds != null) {
			if (globalIds.size() == 1) {
				fqFilter.add(GLOBAL_ID, IterableUtils.first(globalIds));
			} else {
				fqFilter.add(GLOBAL_ID, globalIds);
			}
		}

		final Boolean restrictedAccess = datasetSearchCriteria.getRestrictedAccess();

		if (Boolean.TRUE.equals(restrictedAccess)) {
			// Dans le cas d'un jdd restreint
			fqFilter.add(RESTRICTED_ACCESS, true);
			fqFilter.add(GDPR_SENSITIVE, Boolean.TRUE, true);
		} else if (Boolean.FALSE.equals(restrictedAccess)) {
			// Dans le cas d'un jdd Ouvert
			fqFilter.add(RESTRICTED_ACCESS, Boolean.TRUE, true);
		}

		final Boolean gdprSensitive = datasetSearchCriteria.getGdprSensitive();

		if (Boolean.TRUE.equals(gdprSensitive)) {
			// Dans le cas d'un jdd selfdata
			fqFilter.add(GDPR_SENSITIVE, true);
			fqFilter.add(RESTRICTED_ACCESS, true);
		} else if (Boolean.FALSE.equals(gdprSensitive)) {
			// Dans le cas d'un jdd qui est tout sauf selfdata
			fqFilter.add(GDPR_SENSITIVE, Boolean.TRUE, true);
		}

		final String doi = datasetSearchCriteria.getDoi();
		if (doi != null) {
			fqFilter.add(DOI, doi);
		}

		final String localId = datasetSearchCriteria.getLocalId();
		if (localId != null) {
			fqFilter.add(LOCAL_ID, localId);
		}

		return fqFilter;
	}

	@Override
	protected String getSortField(String sortBy) {
		// tri par défaut : ascendant sur le name (champ de CitationMedata)
		// Comme on alimente le champ name avec rudi_ressource_title cela équivaut au tri sur le titre
		var sortField = DATAVERSE_NAME_PROPERTY;

		if (sortBy != null) {
			if (sortBy.equalsIgnoreCase(DATAVERSE_DATE_PROPERTY)) {
				sortField = DATAVERSE_DATE_PROPERTY;
			} else if (sortBy.equalsIgnoreCase(DATAVERSE_SCORE_PROPERTY)) {
				sortField = DATAVERSE_SCORE_PROPERTY;
			} else if (!sortBy.equalsIgnoreCase(DATAVERSE_NAME_PROPERTY)
					&& !sortBy.equalsIgnoreCase(RudiMetadataField.RESOURCE_TITLE.getLocalName())) {
				FieldSpec sortFieldSpec = RudiMetadataField.RUDI_ELEMENT_SPEC.findFieldByName(sortBy);
				if (sortFieldSpec != null) {
					sortField = sortFieldSpec.getSortableField().getName();
				}
			}
		}
		return sortField;
	}

	private SortParam extractSortParams(DatasetSearchCriteria datasetSearchCriteria) {
		final var sourceOrder = datasetSearchCriteria.getOrder();
		final String order;
		if (BooleanUtils.isTrue(datasetSearchCriteria.getOrderByScoreOfKeywords())) {
			if (StringUtils.isNotEmpty(sourceOrder)) {
				throw new IllegalArgumentException("Cannot use order criterion when orderByScoreOfKeywords is true");
			}
			order = SCORE_DESC_ORDER;
		} else {
			order = sourceOrder;
		}
		return extractSortParams(order);
	}

}
