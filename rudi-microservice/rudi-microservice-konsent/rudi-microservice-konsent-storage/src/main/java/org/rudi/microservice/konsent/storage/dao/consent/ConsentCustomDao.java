package org.rudi.microservice.konsent.storage.dao.consent;

import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsentCustomDao {
	Page<ConsentEntity> searchConsents(ConsentSearchCriteria searchCriteria, Pageable pageable);

	Page<ConsentEntity> searchMyConsents(ConsentSearchCriteria searchCriteria, Pageable pageable);
}
