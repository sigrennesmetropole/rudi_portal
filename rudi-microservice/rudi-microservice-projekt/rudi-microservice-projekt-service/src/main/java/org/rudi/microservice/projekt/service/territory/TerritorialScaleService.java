package org.rudi.microservice.projekt.service.territory;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.core.bean.TerritorialScaleSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TerritorialScaleService {

	/**
	 * Search for territorial scales
	 *
	 * @return paged territorialScale list
	 */
	Page<TerritorialScale> searchTerritorialScales(TerritorialScaleSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException if entity was not found
	 */
	TerritorialScale getTerritorialScale(UUID uuid);

	/**
	 * Create a territorialScale
	 */
	TerritorialScale createTerritorialScale(TerritorialScale territorialScale) throws AppServiceException;

	/**
	 * Update a territorialScale entity
	 */
	TerritorialScale updateTerritorialScale(TerritorialScale territorialScale) throws AppServiceException;

	/**
	 * Delete a territorialScale entity
	 */
	void deleteTerritorialScale(UUID uuid);
}
