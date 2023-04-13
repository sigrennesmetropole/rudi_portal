package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TreatmentVersionCustomDao {

	/**
	 * Cherche et retourne les versions d'un traitement selon des critères
	 *
	 * @param searchCriteria critères de recherche
	 * @param pageable       élements de construction d'une page
	 * @return une page de version
	 */
	Page<TreatmentVersionEntity> searchTreatmentVersions(TreatmentVersionSearchCriteria searchCriteria, Pageable pageable);
}
