package org.rudi.microservice.apigateway.service.throttling;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface ThrottlingService {

	/**
	 * List all Throttling
	 * 
	 * @return Throttling list
	 */
	Page<Throttling> searchThrottlings(ThrottlingSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @param uuid
	 * @return an api
	 * @throws AppServiceException
	 */
	Throttling getThrottling(UUID uuid) throws AppServiceException;

	/**
	 * Create a Throttling
	 */
	Throttling createThrottling(Throttling throttling) throws AppServiceException;

	/**
	 * Update a Throttling entity
	 */
	Throttling updateThrottling(Throttling throttling) throws AppServiceException;

	/**
	 * Delete a Throttling entity
	 */
	void deleteThrottling(UUID uuid) throws AppServiceException;

}
