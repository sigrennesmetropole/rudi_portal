package org.rudi.microservice.projekt.storage.dao.linkeddataset;

import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom DAO pour les linkedDataset
 * 
 * @author FNI18300
 *
 */
public interface LinkedDatasetCustomDao {

	Page<LinkedDatasetEntity> searchLinkedDatasets(LinkedDatasetSearchCriteria searchCriteria, Pageable pageable);
}