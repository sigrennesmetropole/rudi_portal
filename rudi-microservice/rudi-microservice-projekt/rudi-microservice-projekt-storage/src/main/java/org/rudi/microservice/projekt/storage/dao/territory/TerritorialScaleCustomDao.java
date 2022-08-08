package org.rudi.microservice.projekt.storage.dao.territory;

import org.rudi.microservice.projekt.core.bean.TerritorialScaleSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TerritorialScaleCustomDao {
	Page<TerritorialScaleEntity> searchTerritorialScales(TerritorialScaleSearchCriteria searchCriteria, Pageable pageable);
}
