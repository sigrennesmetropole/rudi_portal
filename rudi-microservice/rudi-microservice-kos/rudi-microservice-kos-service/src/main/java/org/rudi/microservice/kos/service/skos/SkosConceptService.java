package org.rudi.microservice.kos.service.skos;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.microservice.kos.core.bean.SimpleSkosConcept;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkosConceptService {

    /**
     * List all providers
     *
     * @return providers list
     */
    Page<SimpleSkosConcept> searchSkosConcepts(SkosConceptSearchCriteria searchCriteria, Pageable pageable);

    DocumentContent downloadSkosConceptIcon(String resourceName) throws IOException;

}
