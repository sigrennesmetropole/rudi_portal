package org.rudi.microservice.projekt.service.targetaudience;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.core.bean.TargetAudienceSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TargetAudienceService {
	/**
	 * Search for target audience
	 *
	 * @return paged targetAudience list
	 */
	Page<TargetAudience> searchTargetAudiences(TargetAudienceSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException if entity was not found
	 */
	TargetAudience getTargetAudience(UUID uuid);

	/**
	 * Create a targetAudience
	 */
	TargetAudience createTargetAudience(TargetAudience targetAudience) throws AppServiceException;

	/**
	 * Update a targetAudience entity
	 */
	TargetAudience updateTargetAudience(TargetAudience targetAudience) throws IllegalArgumentException;

	/**
	 * Delete a targetAudience entity
	 */
	void deleteTargetAudience(UUID uuid);
}
