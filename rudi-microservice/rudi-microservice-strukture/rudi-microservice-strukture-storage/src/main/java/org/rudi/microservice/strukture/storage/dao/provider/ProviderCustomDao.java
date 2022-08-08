package org.rudi.microservice.strukture.storage.dao.provider;

import org.rudi.microservice.strukture.core.bean.ProviderSearchCriteria;
import org.rudi.microservice.strukture.storage.entity.provider.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Permet d'obtenir une liste de provider paginée et triée
 */
public interface ProviderCustomDao {
	Page<ProviderEntity> searchProviders(ProviderSearchCriteria searchCriteria, Pageable pageable);
}
