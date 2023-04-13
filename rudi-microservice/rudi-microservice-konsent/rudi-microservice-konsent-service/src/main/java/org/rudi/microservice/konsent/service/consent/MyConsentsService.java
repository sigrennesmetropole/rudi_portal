package org.rudi.microservice.konsent.service.consent;

import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.springframework.data.domain.Pageable;

public interface MyConsentsService {
	/**
	 * @param searchCriteria critères de recherche
	 * @return liste paginée des consentements donnés par l'utilisateur connecté
	 * @throws Exception
	 */
	PagedConsentList searchMyConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) throws Exception;
}
