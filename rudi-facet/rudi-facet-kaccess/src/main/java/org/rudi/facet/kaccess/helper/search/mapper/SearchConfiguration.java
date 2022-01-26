package org.rudi.facet.kaccess.helper.search.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfiguration {

	/**
	 * @return Le mapper à utiliser lorsqu'il y a plusieurs DataSet à mapper
	 */
	@Bean
	public SearchElementDatasetMapper searchElementDatasetMapperForMultipleDatasets(
			SearchElementDatasetMapperWithGetDataset searchElementDatasetMapperWithGetDataset,
			@Autowired(required = false)
					SearchElementDatasetMapperWithMetadataBlocks searchElementDatasetMapperWithMetadataBlocks) {
		if (searchElementDatasetMapperWithMetadataBlocks != null) {
			return searchElementDatasetMapperWithMetadataBlocks;
		} else {
			return searchElementDatasetMapperWithGetDataset;
		}
	}

	/**
	 * @return Le mapper à utiliser lorsqu'il n'y a qu'un seul DataSet à mapper
	 */
	@Bean
	public SearchElementDatasetMapper searchElementDatasetMapperForOneDataset(SearchElementDatasetMapperWithGetDataset searchElementDatasetMapperWithGetDataset) {
		return searchElementDatasetMapperWithGetDataset;
	}
}
