package org.rudi.microservice.kos.storage.dao.skos;

import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkosSchemeCustomDao {
    Page<SkosSchemeEntity> searchSkosSchemes(SkosSchemeSearchCriteria searchCriteria, Pageable pageable);
}
