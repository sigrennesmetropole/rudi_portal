/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CmsRequest {

	private static final String QUERY_OPERATOR = "query=";

	private List<String> categories;

	private List<String> filters;

	private Locale locale;

	private String query;

	private String nodeType;

	public List<String> getSanitizedFilters() {
		if (CollectionUtils.isNotEmpty(filters)) {
			return filters.stream().filter(filter -> !filter.startsWith(QUERY_OPERATOR)).collect(Collectors.toList());
		}
		return filters;
	}

	public String getQuery() {
		if (StringUtils.isEmpty(query) && CollectionUtils.isNotEmpty(filters)) {
			query = filters.stream().filter(filter -> filter.startsWith(QUERY_OPERATOR))
					.map(value -> value.substring(QUERY_OPERATOR.length())).findFirst().orElse(null);
		}
		return query;
	}

}
