package org.rudi.microservice.kos.facade.controller;

import java.util.List;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.helper.ControllerHelper;
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
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkosConceptController implements SkosConceptsApi {

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private SkosConceptService skosConceptService;

	@Autowired
	private ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<SimpleSkosConceptPageResult> searchSkosConcepts(Integer limit, Integer offset, String order,
			Language lang, String text, List<SkosRelationType> types, List<String> roles, List<String> codes,
			List<String> schemes, List<SkosConceptLabel> labels) throws Exception {

		SkosConceptSearchCriteria skosConceptSearchCriteria = SkosConceptSearchCriteria.builder().labels(labels)
				.codes(codes).codesScheme(schemes).text(text).roles(roles).lang(lang).types(types).build();

		Pageable pageable = utilPageable.getPageable(offset, limit, order);

		Page<SimpleSkosConcept> page = skosConceptService.searchSkosConcepts(skosConceptSearchCriteria, pageable);
		SimpleSkosConceptPageResult result = new SimpleSkosConceptPageResult();
		result.setTotal(page.getTotalElements());
		result.setElements(page.getContent());

		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<Resource> downloadSkosConceptIcon(String resourceName) throws Exception {
		DocumentContent resource = skosConceptService.downloadSkosConceptIcon(resourceName);
		return controllerHelper.downloadableResponseEntity(resource);
	}
}
