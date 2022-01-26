package org.rudi.facet.kaccess.helper.search.mapper;

import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
		name = "features.metadataBlocks")
public class SearchElementDatasetMapperWithMetadataBlocks extends SearchElementDatasetMapper {

	public SearchElementDatasetMapperWithMetadataBlocks(MetadataBlockHelper metadataBlockHelper) {
		super(metadataBlockHelper);
	}

	@Override
	protected DatasetMetadataBlock getDatasetMetadataBlock(SearchDatasetInfo searchDatasetInfo) {
		return searchDatasetInfo.getMetadataBlocks();
	}
}
