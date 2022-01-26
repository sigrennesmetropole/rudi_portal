package org.rudi.facet.kaccess.helper.search.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchItemFacets;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacet;
import org.rudi.facet.kaccess.bean.MetadataFacetValues;
import org.rudi.facet.kaccess.bean.MetadataFacets;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class SearchElementDatasetMapper implements SearchElementMapper<SearchDatasetInfo> {

	private final MetadataBlockHelper metadataBLockHelper;

	@Override
	public MetadataList toMetadataList(SearchElements<SearchDatasetInfo> searchElements) throws DataverseAPIException {
		return SearchElementMapper.super.toMetadataList(searchElements)
				.items(searchDatasetInfosToMetadatas(searchElements.getItems()));
	}

	@Override
	public MetadataListFacets toMetadataListFacets(SearchElements<SearchDatasetInfo> searchElements,
			List<String> metadataPropertiesFacets) throws DataverseAPIException {
		return new MetadataListFacets().metadataList(this.toMetadataList(searchElements))
				.facets(searchDatasetInfosToMetadataFacets(searchElements.getFacets(), metadataPropertiesFacets));
	}

	private List<Metadata> searchDatasetInfosToMetadatas(List<SearchDatasetInfo> searchDatasetInfos)
			throws DataverseAPIException {
		List<Metadata> metadatas = new ArrayList<>();
		if (searchDatasetInfos != null) {
			for (SearchDatasetInfo searchDatasetInfo : searchDatasetInfos) {
				metadatas.add(searchDatasetInfoToMetadata(searchDatasetInfo));
			}
		}
		return metadatas;
	}

	private Metadata searchDatasetInfoToMetadata(SearchDatasetInfo searchDatasetInfo) throws DataverseAPIException {
		if (searchDatasetInfo == null) {
			return new Metadata();
		}
		return metadataBLockHelper.datasetMetadataBlockToMetadata(getDatasetMetadataBlock(searchDatasetInfo), getPersistentId(searchDatasetInfo));
	}

	protected abstract DatasetMetadataBlock getDatasetMetadataBlock(SearchDatasetInfo searchDatasetInfo) throws DataverseAPIException;

	private String getPersistentId(SearchDatasetInfo searchDatasetInfo) {
		return searchDatasetInfo.getGlobalId();
	}

	private MetadataFacets searchDatasetInfosToMetadataFacets(List<Map<String, SearchItemFacets>> facets,
			List<String> metadataPropertiesFacets) {
		MetadataFacets metadataFacets = new MetadataFacets();
		if (CollectionUtils.isNotEmpty(facets) && CollectionUtils.isNotEmpty(metadataPropertiesFacets)) {

			// Seul le premier élément de la liste nous intéresse
			Map<String, SearchItemFacets> searchItemFacetMap = facets.get(0);
			searchItemFacetMap.forEach((indexName, searchItemFacets) -> {
				// key est un chaine de caractère index utilisé par le dataverse
				// on retire le prefixe rudi_ et le suffixe _ss ou _s pour retrouver la propriété envoyée en paramètre
				String metadataKey = StringUtils.substringBeforeLast(StringUtils.substringAfter(indexName, "_"), "_");
				if (metadataPropertiesFacets.contains(metadataKey)) {
					List<MetadataFacetValues> metadataFacetValuesList = new ArrayList<>();
					List<Map<String, Integer>> labels = searchItemFacets.getLabels();

					labels.forEach(label -> {
						Map.Entry<String, Integer> entry = label.entrySet().iterator().next();
						String keyLabel = entry.getKey();
						Integer valueLabel = entry.getValue();
						MetadataFacetValues metadataFacetValues = new MetadataFacetValues().value(keyLabel)
								.count(valueLabel);
						metadataFacetValuesList.add(metadataFacetValues);
					});

					MetadataFacet metadataFacet = new MetadataFacet().propertyName(metadataKey)
							.values(metadataFacetValuesList);

					metadataFacets.addItemsItem(metadataFacet);
				}
			});
		}
		return metadataFacets;
	}
}
