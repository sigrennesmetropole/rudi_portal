package org.rudi.facet.dataverse.model.search;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.rudi.facet.dataverse.bean.SearchType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@Value
public class SearchParams {

	public static final int MAX_RESULTS_PER_PAGE = 1000;
	EnumSet<SearchType> type;
	@NonNull
	String q;
	String subtree;
	List<String> filterQuery;
	String sortBy;
	String sortOrder;
	Integer perPage;
	Integer start;
	Boolean showRelevance;
	Boolean showFacets;
	/**
	 * @see <a href="https://github.com/IQSS/dataverse/issues/7863">GitHub Merge Request</a>
	 */
	Set<String> metadatafields;

	public static class SearchParamsBuilder {
		/**
		 * Sets results per page. Maximum is 1000
		 *
		 * @param perPage if > 1000, will set to 1000
		 * @return
		 * @throws IllegalArgumentException if <code>perPage</code> &lt= 0
		 */
		public SearchParamsBuilder perPage(Integer perPage) {
			if (perPage != null && perPage > MAX_RESULTS_PER_PAGE) {
				perPage = MAX_RESULTS_PER_PAGE;
			}
			this.perPage = perPage;
			return this;
		}
	}
}
