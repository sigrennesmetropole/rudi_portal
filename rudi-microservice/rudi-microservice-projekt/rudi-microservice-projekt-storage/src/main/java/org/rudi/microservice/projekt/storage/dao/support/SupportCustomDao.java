package org.rudi.microservice.projekt.storage.dao.support;

import org.rudi.microservice.projekt.core.bean.SupportSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportCustomDao {
	Page<SupportEntity> searchSupports(SupportSearchCriteria searchCriteria, Pageable pageable);
}
