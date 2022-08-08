package org.rudi.microservice.projekt.service.confidentiality;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.core.bean.ConfidentialitySearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ConfidentialityService {

	/**
	 * Search for confidentialities
	 *
	 * @return paged confidentiality list
	 */
	Page<Confidentiality> searchConfidentialities(ConfidentialitySearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException if entity was not found
	 */
	Confidentiality getConfidentiality(UUID uuid);

	/**
	 * Create a confidentiality
	 */
	Confidentiality createConfidentiality(Confidentiality confidentiality) throws AppServiceException;

	/**
	 * Update a confidentiality entity
	 */
	Confidentiality updateConfidentiality(Confidentiality confidentiality) throws AppServiceException;

	/**
	 * Delete a confidentiality entity
	 */
	void deleteConfidentiality(UUID uuid);

	/**
	 * Get a confidentiality by code
	 */
	Confidentiality getConfidentialityByCode(String code);
}
