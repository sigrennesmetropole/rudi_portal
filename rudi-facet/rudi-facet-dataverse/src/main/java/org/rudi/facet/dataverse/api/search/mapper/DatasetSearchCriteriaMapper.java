package org.rudi.facet.dataverse.api.search.mapper;

import org.springframework.stereotype.Component;

@Component
public abstract class DatasetSearchCriteriaMapper {

	protected abstract String getSortField(String sortBy);

	protected SortParam extractSortParams(String sort) {

		String sortBy = null;
		// ordre de tri par défaut : ascendant
		var orderBy = SortParam.Order.ASC;

		if (sort != null) {

			// On récupère les critères de tri séparés par une virgule
			final String[] filter = sort.split(",");

			// le dataverse ne permet de trier que sur un seul champ
			if (filter.length == 1) {

				String f = filter[0];

				if (f.startsWith("-")) {
					// signe - : ordre descendant
					orderBy = SortParam.Order.DESC;
					sortBy = f.substring(1);
				} else {
					// pas de signe : odre ascendant
					orderBy = SortParam.Order.ASC;
					sortBy = f;
				}
			}
		}
		return new SortParam(getSortField(sortBy), orderBy);
	}

}
