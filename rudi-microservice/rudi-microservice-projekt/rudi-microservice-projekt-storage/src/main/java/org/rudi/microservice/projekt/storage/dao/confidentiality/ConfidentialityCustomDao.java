package org.rudi.microservice.projekt.storage.dao.confidentiality;

import org.rudi.microservice.projekt.core.bean.ConfidentialitySearchCriteria;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConfidentialityCustomDao {
	Page<ConfidentialityEntity> searchConfidentialities(ConfidentialitySearchCriteria searchCriteria, Pageable pageable);
}
