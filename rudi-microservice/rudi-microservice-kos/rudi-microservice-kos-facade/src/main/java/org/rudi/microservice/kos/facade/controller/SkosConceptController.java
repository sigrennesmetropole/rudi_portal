package org.rudi.microservice.kos.facade.controller;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.kos.core.bean.Language;
import org.rudi.microservice.kos.core.bean.SimpleSkosConcept;
import org.rudi.microservice.kos.core.bean.SimpleSkosConceptPageResult;
import org.rudi.microservice.kos.core.bean.SkosConceptLabel;
import org.rudi.microservice.kos.core.bean.SkosConceptSearchCriteria;
import org.rudi.microservice.kos.core.bean.SkosRelationType;
import org.rudi.microservice.kos.facade.controller.api.SkosConceptsApi;
import org.rudi.microservice.kos.service.skos.SkosConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SkosConceptController implements SkosConceptsApi {

    @Autowired
    private UtilPageable utilPageable;

    @Autowired
    private SkosConceptService skosConceptService;

    @Override
    public ResponseEntity<SimpleSkosConceptPageResult> searchSkosConcepts(@Valid Integer limit, @Valid Integer offset, @Valid String order,
                                                                          @Valid Language lang, @Valid String text, @Valid List<SkosRelationType> types,
                                                                          @Valid List<String> roles, @Valid List<String> codes, @Valid List<String> schemes,
                                                                          @Valid List<SkosConceptLabel> labels) throws Exception {

        SkosConceptSearchCriteria skosConceptSearchCriteria = SkosConceptSearchCriteria.builder()
                .labels(labels).codes(codes).codesScheme(schemes)
                .text(text).roles(roles).lang(lang).types(types)
                .build();

        Pageable pageable = utilPageable.getPageable(offset, limit, order);

        Page<SimpleSkosConcept> page = skosConceptService.searchSkosConcepts(skosConceptSearchCriteria, pageable);
        SimpleSkosConceptPageResult result = new SimpleSkosConceptPageResult();
        result.setTotal(page.getTotalElements());
        result.setElements(page.getContent());

        return ResponseEntity.ok(result);
    }
}
