package org.rudi.facet.kaccess.helper.search.mapper;

import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @deprecated À supprimer dès que la MR sur le GitHub de Dataverse est acceptée (dans le cas où le format retenu n'est pas celui géré par ce mapper)
 */
@Component
@ConditionalOnProperty(
		name = "features.metadatafields")
@Deprecated(forRemoval = true)
public class SearchElementDatasetMapperWithMetadatafields extends SearchElementDatasetMapper {

	private final MetadatafieldsToBlocksMapper metadatafieldsToBlocksMapper;

	public SearchElementDatasetMapperWithMetadatafields(MetadataBlockHelper metadataBLockHelper, MetadatafieldsToBlocksMapper metadatafieldsToBlocksMapper) {
		super(metadataBLockHelper);
		this.metadatafieldsToBlocksMapper = metadatafieldsToBlocksMapper;
	}

	@Override
	protected DatasetMetadataBlock getDatasetMetadataBlock(SearchDatasetInfo searchDatasetInfo) {
		/*
		 * À décommenter si le format déclaré dans le OpenAPI redevient :
		 *
		 * metadatafields:
		 *     type: object
		 *     additionalProperties:
		 *       type: object
		 *
		 * C'est à dire côté Java : Map<String, Object> metadatafields
		 */
//		return metadatafieldsMapper.map(searchDatasetInfo.getMetadatafields());
		return metadatafieldsToBlocksMapper.map(Collections.emptyMap());
	}
}
