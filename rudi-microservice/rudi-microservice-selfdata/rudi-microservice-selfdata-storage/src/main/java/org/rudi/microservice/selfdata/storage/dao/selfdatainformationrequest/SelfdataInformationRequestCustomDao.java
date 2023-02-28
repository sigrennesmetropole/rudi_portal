package org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest;

import java.util.UUID;

import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SelfdataInformationRequestCustomDao {

	/**
	 * Recherche des demandes selfdata
	 *
	 * @param criteria les critères de recherche appliqués
	 * @param pageable critères de pagination/tri
	 * @return une page de demandes
	 */
	Page<SelfdataInformationRequestEntity> searchSelfdataInformationRequests(
			SelfdataInformationRequestCustomSearchCriteria criteria, Pageable pageable);

	/**
	 * @param userLogin   login de l'utilisateur
	 * @param datasetUuid UUID du JDD sur lequel il a fait sa demande
	 * @return La dernière demande d'accès de l'utilisateur sur ce JDD
	 */
	SelfdataInformationRequestEntity getLastSelfdataInformationRequest(String userLogin, UUID datasetUuid);
}
