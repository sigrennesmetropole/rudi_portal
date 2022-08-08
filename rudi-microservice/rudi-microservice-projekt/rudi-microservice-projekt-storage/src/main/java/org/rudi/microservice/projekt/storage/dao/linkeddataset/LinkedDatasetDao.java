package org.rudi.microservice.projekt.storage.dao.linkeddataset;

import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les LinkedDataset
 * 
 * @author FNI18300
 *
 */
@Repository
public interface LinkedDatasetDao extends AssetDescriptionDao<LinkedDatasetEntity> {

}