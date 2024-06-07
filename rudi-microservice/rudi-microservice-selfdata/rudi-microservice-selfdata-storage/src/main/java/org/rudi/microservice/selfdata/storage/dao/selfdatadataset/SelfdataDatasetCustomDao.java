package org.rudi.microservice.selfdata.storage.dao.selfdatadataset;

import java.util.List;

import org.rudi.microservice.selfdata.storage.entity.selfdatadataset.SelfdataDatasetEntity;

public interface SelfdataDatasetCustomDao {

	/**
	 * Recherche des dernières demandes effectuées sur des jeux de données selfdata
	 * @param criteria les critères de recherche
	 * @return une liste de demandes
	 */
	List<SelfdataDatasetEntity> searchSelfdataDatasets(SelfdataDatasetCustomSearchCriteria criteria);
}
