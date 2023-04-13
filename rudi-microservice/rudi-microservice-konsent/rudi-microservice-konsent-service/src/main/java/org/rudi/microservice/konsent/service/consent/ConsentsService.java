package org.rudi.microservice.konsent.service.consent;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.Consent;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 */
public interface ConsentsService {

	/**
	 * Recherche les consentements de l'utilisateur connecté
	 *
	 * @param searchCriteria les critères de filtrage/tri pour la recherche
	 * @return une page de consentements que l'utilisateur connecté a emis
	 */
	PagedConsentList searchConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) throws AppServiceException;

	/**
	 * Création de consentement
	 *
	 * @param treatmentVersionUuid l'UUID de traitements auquel on consent
	 * @return le consentements créé
	 */
	Consent createConsent(UUID treatmentVersionUuid) throws AppServiceException;

	/**
	 * Recherches les consentemens de mes traitements en tant que porteur de projet
	 *
	 * @param criteria les critères de filtrage/tri pour la recherche
	 * @return une page de consentements d'autres utilisateurs pour "mes" traitements
	 */
	PagedConsentList searchMyTreatmentsConsents(ConsentSearchCriteria criteria) throws AppServiceException;

	/**
	 * Revocation d'un consentement
	 * 
	 * @param consentUuid l'uuid du consentement
	 * @throws AppServiceException si le consentement est invalid ou déjà revoqué
	 */
	void revokeConsent(UUID consentUuid) throws AppServiceException;

	/**
	 * méthode utilisé par le tache schédulée de controle de validité
	 * 
	 * @param consentUuids
	 */
	void checkConsentValidities(List<UUID> consentUuids) throws AppServiceException;

	void checkRevokeValidities(List<UUID> consentUuids) throws AppServiceException;
}
