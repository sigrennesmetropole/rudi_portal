package org.rudi.facet.dataverse.api.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.bean.SearchItemInfo;
import org.springframework.stereotype.Component;

/*
* Pour la recherche multi type
* */
@Component
public class SearchOperationAPI extends AbstractSearchOperationAPI<SearchItemInfo> {
	public SearchOperationAPI(ObjectMapper objectMapper) {
		super(objectMapper);
	}
}
