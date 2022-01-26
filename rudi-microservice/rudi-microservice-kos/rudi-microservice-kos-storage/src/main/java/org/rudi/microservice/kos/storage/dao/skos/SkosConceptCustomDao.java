package org.rudi.microservice.kos.storage.dao.skos;

import org.rudi.microservice.kos.core.bean.SimpleSkosConceptProjection;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkosConceptCustomDao {
    Page<SimpleSkosConceptProjection> searchSkosConcepts(SkosConceptSearchCriteria searchCriteria, Pageable pageable);
}
