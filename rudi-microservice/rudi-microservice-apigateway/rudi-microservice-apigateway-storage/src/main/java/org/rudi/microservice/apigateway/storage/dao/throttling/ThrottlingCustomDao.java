package org.rudi.microservice.apigateway.storage.dao.throttling;

import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author FNI18300
 *
 */
public interface ThrottlingCustomDao {
	Page<ThrottlingEntity> searchThrottlings(ThrottlingSearchCriteria searchCriteria, Pageable pageable);
}
