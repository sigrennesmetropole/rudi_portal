package org.rudi.microservice.projekt.storage.dao.newdatasetrequest;

import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface NewDatasetRequestDao extends AssetDescriptionDao<NewDatasetRequestEntity> {

}