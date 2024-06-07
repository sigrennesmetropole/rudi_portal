package org.rudi.microservice.selfdata.storage.dao.selfdatadataset;

import java.util.List;

import org.rudi.microservice.selfdata.storage.entity.selfdatadataset.SelfdataDatasetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelfdataDatasetDao extends JpaRepository<SelfdataDatasetEntity, Long> {

	/**
	 * Recherche des selfdata dataset par login d'utilisateur
	 * @param initiator le login de l'utilisateur connecté
	 * @param pageable critères de pagination
	 * @return une liste de demandes
	 */
	List<SelfdataDatasetEntity> findByInitiator(String initiator, Pageable pageable);
}
