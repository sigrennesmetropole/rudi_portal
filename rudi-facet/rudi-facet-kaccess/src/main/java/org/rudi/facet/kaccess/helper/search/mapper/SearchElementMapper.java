package org.rudi.facet.kaccess.helper.search.mapper;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.SearchItemInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;

import java.util.List;

public interface SearchElementMapper<T extends SearchItemInfo> {

	default MetadataList toMetadataList(SearchElements<T> searchElements) throws DataverseAPIException {
		return new MetadataList().total(searchElements.getTotal());
	}

	MetadataListFacets toMetadataListFacets(SearchElements<T> searchElements, List<String> metadataPropertiesFacets)
			throws DataverseAPIException;
}
