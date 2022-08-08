package org.rudi.microservice.projekt.storage.dao.newdatasetrequest;

import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewDatasetRequestCustomDao {
	Page<NewDatasetRequestEntity> searchNewDatasetRequest(NewDatasetRequestSearchCriteria searchCriteria, Pageable pageable);
}