package org.rudi.microservice.projekt.service.support;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Support;
import org.rudi.microservice.projekt.core.bean.SupportSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface SupportService {

	/**
	 * Search for supports
	 *
	 * @return paged support list
	 */
	Page<Support> searchSupports(SupportSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 * @return
	 */
	@Nonnull
	Support getSupport(UUID uuid);

	/**
	 * Create a support
	 */
	Support createSupport(Support support) throws AppServiceException;

	/**
	 * Update a support entity
	 */
	Support updateSupport(Support support) throws AppServiceException;

	/**
	 * Delete a support entity
	 */
	void deleteSupport(UUID uuid);
}
