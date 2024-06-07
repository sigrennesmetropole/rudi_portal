package org.rudi.microservice.apigateway.storage.dao.api;

import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author FNI18300
 *
 */
public interface ApiCustomDao {
	Page<ApiEntity> searchApis(ApiSearchCriteria searchCriteria, Pageable pageable);
}
