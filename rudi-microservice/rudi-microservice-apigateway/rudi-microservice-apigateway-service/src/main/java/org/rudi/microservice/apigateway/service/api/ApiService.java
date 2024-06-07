package org.rudi.microservice.apigateway.service.api;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface ApiService {

	/**
	 * List all Api
	 * 
	 * @return Api list
	 */
	Page<Api> searchApis(ApiSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @param uuid
	 * @return an api
	 * @throws AppServiceException
	 */
	Api getApi(UUID uuid) throws AppServiceException;

	/**
	 * Create a Api
	 */
	Api createApi(Api api) throws AppServiceException;

	/**
	 * Update a Api entity
	 */
	Api updateApi(Api api) throws AppServiceException;

	/**
	 * Delete a Api entity
	 */
	void deleteApi(UUID uuid) throws AppServiceException;

}
