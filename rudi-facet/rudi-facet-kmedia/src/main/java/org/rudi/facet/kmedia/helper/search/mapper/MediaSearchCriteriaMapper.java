package org.rudi.facet.kmedia.helper.search.mapper;

import org.rudi.facet.dataverse.api.search.mapper.DatasetSearchCriteriaMapper;
import org.rudi.facet.dataverse.bean.SearchType;
import org.rudi.facet.dataverse.helper.query.FilterQuery;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kmedia.bean.MediaSearchCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;

import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_AFFILIATION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_IDENTIFIER;
import static org.rudi.facet.kmedia.constant.CitationMediaField.KIND_OF_DATA;

/**
 * Mapper qui construit la requete SOLR pour le dataverse Rudi Media à partir des critères de recherche définis dans MediaSearchCriteria.
 * <p>
 * L'API de recherche du dataverse (qui s'appuie sur SOLR) est décrite ici : https://guides.dataverse.org/en/latest/api/search.html
 * <p>
 * La syntaxe des requêtes SOLR est décrite ici : https://solr.apache.org/guide/8_5/common-query-parameters.html
 */
@Component
public class MediaSearchCriteriaMapper extends DatasetSearchCriteriaMapper {

	private final String mediaDataAlias;

	public MediaSearchCriteriaMapper(@Value("${dataverse.api.rudi.media.data.alias}") String mediaDataAlias) {
		this.mediaDataAlias = mediaDataAlias;
	}

	public SearchParams mediaSearchCriteriaToSearchParams(MediaSearchCriteria mediaSearchCriteria) {
		final String query = new FilterQuery()
				.withExactMatch()
				.add(AUTHOR_IDENTIFIER, mediaSearchCriteria.getMediaAuthorIdentifier())
				.add(AUTHOR_AFFILIATION, mediaSearchCriteria.getMediaAuthorAffiliation())
				.add(KIND_OF_DATA, mediaSearchCriteria.getKindOfData())
				.joinWithAnd();

		// tri
		final var criteresTri = extractSortParams(mediaSearchCriteria.getOrder());

		return SearchParams.builder().q(query).type(EnumSet.of(SearchType.DATASET)).subtree(mediaDataAlias)
				.start(mediaSearchCriteria.getOffset()).perPage(mediaSearchCriteria.getLimit()).filterQuery(Collections.emptyList())
				.sortBy(criteresTri.field).sortOrder(criteresTri.order.stringValue).showFacets(false).build();
	}

	@Override
	protected String getSortField(String sortBy) {
		// tri par défaut : ascendant sur le name (champ de CitationMedata)
		// Remarque : le dataverse ne permet de trier que par name ou par date
		String sortField = "name";

		if (sortBy != null) {

			switch (sortBy) {
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
