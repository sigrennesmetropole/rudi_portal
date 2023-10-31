package org.rudi.microservice.projekt.service.reutilisationstatus;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.springframework.data.domain.Page;

public interface ReutilisationStatusService {

	/**
	 * Create a new ReutilisationStatus
	 *
	 * @param reutilisationStatus
	 * @return
	 */
	ReutilisationStatus createReutilisationStatus(ReutilisationStatus reutilisationStatus);

	/**
	 * get a specific ReutilisationStatus tagetted by its UUID
	 *
	 * @param uuid UUID of the targetted ReutilisationStatus
	 * @return ReutilisationStatus
	 */
	ReutilisationStatus getReutilisationStatus(UUID uuid);

	/**
	 * Search for ReutilisationsStatus
	 *
	 * @return Page<ReutilisationStatus> -> List paginated of ReutilsiationStatus
	 */
	Page<ReutilisationStatus> searchReutilisationStatus(ReutilisationStatusSearchCriteria criteria, Pageable pageable);

	/**
	 * Update ReutilisationStatus tagetted by its UUID
	 *
	 * @param uuid UUID of the targetted ReutilisationStatus
	 * @param reutilisationStatus object used to do modifications
	 * @return ReutilisationStatus modified
	 */
	ReutilisationStatus updateReutilisationStatus(UUID uuid, ReutilisationStatus reutilisationStatus);

	/**
	 * get ReutilisationStatus targetted by it code
	 *
	 * @param code code of the targetted ReutilisationStatus
	 * @return existing ReutilisationStatus
	 */
	ReutilisationStatus getReutilisationStatusByCode(String code);
}
