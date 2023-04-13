package org.rudi.microservice.konsent.storage.dao.treatment;

import java.util.UUID;

import javax.persistence.NoResultException;

import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TreatmentsCustomDao {
	/**
	 * Cherche des traitements selon une criteria. Les traitements retournés ne contiennent pas de version
	 *
	 * @param searchCriteria critères de recherche
	 * @param pageable       la page à retourner
	 * @return une page de Traitement
	 */
	Page<TreatmentEntity> searchTreatments(TreatmentSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * Retourne un traitement par son uuid.
	 *
	 * @param uuid              du traitement
	 * @param statusIsValidated param facultatif permettant de dire si on cherche un traitement ayant une version publiée déjà. pdf vaut false
	 * @return un Traitement
	 */
	TreatmentEntity getTreatmentByUuidAndStatus(UUID uuid, Boolean statusIsValidated);

	/**
	 * Retourne le traitement associé à une version de traitement
	 *
	 * @param treatmentVersionUuid uuid de la version du traitement
	 * @return treatmentEntity
	 */
	TreatmentEntity getTreatmentByVersionUuid(UUID treatmentVersionUuid) throws NoResultException;
}
