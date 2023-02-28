package org.rudi.facet.dataverse.model.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rudi.facet.dataverse.bean.SearchItemInfo;

import lombok.Data;

@Data
public class SearchElements<T extends SearchItemInfo> {

	@JsonProperty(value = "total_count")
	private Long total;

	@JsonProperty(value = "start")
	private Long start;

	@JsonProperty("items")
	@Valid
	private List<T> items = null;

	@JsonProperty("facets")
	@Valid
	private List<Map<String, SearchItemFacets>> facets = null;

	public SearchElements<T> total(Long total) {
		this.total = total;
		return this;
	}

	public SearchElements<T> items(List<T> items) {
		this.items = items;
		return this;
	}

	public SearchElements<T> addItemsItem(T t) {
		if (this.items == null) {
			this.items = new ArrayList<>();
		}
		this.items.add(t);
		return this;
	}

}
