package org.rudi.facet.kaccess.helper.search.mapper;

import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;
import org.springframework.stereotype.Component;

/**
 * Cette version de {@link SearchElementDatasetMapper} n'est pas aussi performante que {@link SearchElementDatasetMapperWithMetadatafields}.
 * En effet, pour chaque DataSet, on appelle Dataverse pour obtenir tous les champs manquants.
 * Cependant cette implémentation est la seule qui fonctionne tant que la MR Dataverse (cf lien) pour ajouter les metadatafields n'a pas été mergée
 * et elle également <b>toujours utilisée</b> pour la récupération des métadonnées d'un JDD unitaire.
 *
 * @see <a href="https://github.com/IQSS/dataverse/issues/7863">Merge Request sur le GitHub de Dataverse</a>
 */
@Component
public class SearchElementDatasetMapperWithGetDataset extends SearchElementDatasetMapper {

	private final DatasetOperationAPI datasetOperationAPI;

	public SearchElementDatasetMapperWithGetDataset(MetadataBlockHelper metadataBLockHelper, DatasetOperationAPI datasetOperationAPI) {
		super(metadataBLockHelper);
		this.datasetOperationAPI = datasetOperationAPI;
	}

	@Override
	protected DatasetMetadataBlock getDatasetMetadataBlock(SearchDatasetInfo searchDatasetInfo) throws DataverseAPIException {
		final Dataset dataset = datasetOperationAPI.getDataset(searchDatasetInfo.getGlobalId());
		return dataset.getLatestVersion().getMetadataBlocks();
	}
}
