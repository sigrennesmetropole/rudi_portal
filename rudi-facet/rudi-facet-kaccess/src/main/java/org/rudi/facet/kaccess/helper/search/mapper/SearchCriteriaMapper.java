package org.rudi.facet.kaccess.helper.search.mapper;

import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.search.mapper.DatasetSearchCriteriaMapper;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.helper.search.mapper.query.FilterQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZoneOffset;
import java.util.EnumSet;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.DOI;
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
public class SearchCriteriaMapper extends DatasetSearchCriteriaMapper {

	/**
	 * Liste des caractères qui sont considérés comme des séparateurs par le ClassicTokenizer
	 */
	private static final String DELIMITER_FOR_CLASSIC_TOKENIZER_REGEX = "[^-/_A-Za-zÀ-ÖØ-öø-ÿ0-9]";

	private static final FieldSpec[] FREE_TEXT_FIELDS = {
			RudiMetadataField.RESOURCE_TITLE,
			RudiMetadataField.SYNOPSIS_TEXT
	};

	@Value("${dataverse.api.rudi.data.alias}")
	private String rudiAlias;

	public SearchParams datasetSearchCriteriaToSearchParams(DatasetSearchCriteria datasetSearchCriteria,
			boolean withFacets) {

		// tri
		String[] criteresTri = extractSortParams(datasetSearchCriteria.getOrder());

		return SearchParams.builder()
				.q(getQuery(datasetSearchCriteria.getFreeText()))
				.type(EnumSet.of(SearchType.DATASET))
				.subtree(rudiAlias)
				.start(datasetSearchCriteria.getOffset())
				.perPage(datasetSearchCriteria.getLimit())
				.filterQuery(createFilterQueryFrom(datasetSearchCriteria))
				.sortBy(criteresTri[0])
				.sortOrder(criteresTri[1])
				.showFacets(withFacets)
				.build();
	}

	@Nonnull
	protected String getQuery(String freeText) {

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
		return "*";
	}

	@Nonnull
	private String sanitize(@Nonnull final String freeText) {
		return freeText
				.replaceAll(DELIMITER_FOR_CLASSIC_TOKENIZER_REGEX, " ")
				.replaceAll(" +", " ")
				.trim();
	}

	@Nonnull
	private FilterQuery createFilterQueryFrom(DatasetSearchCriteria datasetSearchCriteria) {
		val fqFilter = new FilterQuery()
				.withExactMatch();

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getKeywords())) {
			fqFilter.add(RudiMetadataField.KEYWORDS, datasetSearchCriteria.getKeywords());
		}

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getThemes())) {
			fqFilter.add(RudiMetadataField.THEME.getIndex(), datasetSearchCriteria.getThemes());
		}

		if (!CollectionUtils.isEmpty(datasetSearchCriteria.getProducerNames())) {
			fqFilter.add(RudiMetadataField.PRODUCER_ORGANIZATION_NAME.getIndex(), datasetSearchCriteria.getProducerNames());
		}

		if (datasetSearchCriteria.getDateDebut() != null) {
			val minTimestamp = String.valueOf(datasetSearchCriteria.getDateDebut().toEpochSecond(ZoneOffset.UTC));
			fqFilter.add(TEMPORAL_SPREAD_START_DATE, minTimestamp, FilterQuery.ANY_VALUE);
		}

		if (datasetSearchCriteria.getDateFin() != null) {
			val maxTimestamp = String.valueOf(datasetSearchCriteria.getDateFin().toEpochSecond(ZoneOffset.UTC));
			fqFilter.add(TEMPORAL_SPREAD_END_DATE, FilterQuery.ANY_VALUE, maxTimestamp);
		}

		if (datasetSearchCriteria.getGlobalId() != null) {
			fqFilter.add(GLOBAL_ID, datasetSearchCriteria.getGlobalId());
		}

		final Boolean restrictedAccess = datasetSearchCriteria.getRestrictedAccess();
		if (restrictedAccess != null) {
			fqFilter.add(RESTRICTED_ACCESS, restrictedAccess);
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
		// Remarque : le dataverse ne permet de trier que par name ou par date
		// Comme on alimente le champ name avec rudi_ressource_title cela équivaut au tri sur le titre
		var sortField = "name";

		if (sortBy != null) {

			switch (sortBy) {
				case "title":
					sortField = RudiMetadataField.RESOURCE_TITLE.getName();
					break;
				case "producername":
					sortField = RudiMetadataField.PRODUCER_ORGANIZATION_NAME.getName();
					break;
				case "updatedate":
					sortField = RudiMetadataField.DATASET_DATES_UPDATED.getName();
					break;
				case "name":
					sortField = "name";
					break;
				case "date":
					sortField = "date";
					break;
				default:
					break;
			}
		}
		return sortField;
	}

}
