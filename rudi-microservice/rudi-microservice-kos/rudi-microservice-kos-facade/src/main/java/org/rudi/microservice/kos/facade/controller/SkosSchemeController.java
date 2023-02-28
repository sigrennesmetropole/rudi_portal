package org.rudi.microservice.kos.facade.controller;

import java.util.List;
import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.kos.core.bean.SkosConcept;
import org.rudi.microservice.kos.core.bean.SkosScheme;
import org.rudi.microservice.kos.core.bean.SkosSchemePageResult;
import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.facade.controller.api.SkosSchemesApi;
import org.rudi.microservice.kos.service.skos.SkosSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KOS_ADMINISTRATOR;

@RestController
public class SkosSchemeController implements SkosSchemesApi {

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private SkosSchemeService skosSchemeService;

	@Override
	public ResponseEntity<SkosScheme> getSkosScheme(UUID skosSchemeUuid) throws Exception {
		return ResponseEntity.ok(skosSchemeService.getSkosScheme(skosSchemeUuid));
	}

	@Override
	public ResponseEntity<SkosSchemePageResult> searchSkosSchemes(Boolean active, Integer limit, Integer offset,
			String order) throws Exception {
		SkosSchemeSearchCriteria skosSchemeSearchCriteria = SkosSchemeSearchCriteria.builder().active(active).build();

		Pageable pageable = utilPageable.getPageable(offset, limit, order);

		Page<SkosScheme> page = skosSchemeService.searchSkosSchemes(skosSchemeSearchCriteria, pageable);
		SkosSchemePageResult result = new SkosSchemePageResult();
		result.setTotal(page.getTotalElements());
		result.setElements(page.getContent());

		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<SkosScheme> createSkosScheme(SkosScheme skosScheme) throws Exception {
		return ResponseEntity.ok(skosSchemeService.createSkosScheme(skosScheme));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteSkosScheme(UUID uuid) throws Exception {
		skosSchemeService.deleteSkosScheme(uuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<SkosScheme> updateSkosScheme(SkosScheme skosScheme) throws Exception {
		return ResponseEntity.ok(skosSchemeService.updateSkosScheme(skosScheme));
	}

	@Override
	public ResponseEntity<SkosConcept> getSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid) throws Exception {
		return ResponseEntity.ok(skosSchemeService.getSkosConcept(skosSchemeUuid, skosConceptUuid));
	}

	@Override
	public ResponseEntity<List<SkosConcept>> getTopConcepts(UUID skosSchemeUuid) throws Exception {
		return ResponseEntity.ok(skosSchemeService.getTopConcepts(skosSchemeUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<SkosConcept> createSkosConcept(UUID skosSchemeUuid, SkosConcept skosConcept,
			Boolean asTopConcept) throws Exception {
		return ResponseEntity.ok(skosSchemeService.createSkosConcept(skosSchemeUuid, skosConcept, asTopConcept));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<SkosConcept> updateSkosConcept(UUID skosSchemeUuid, SkosConcept skosConcept,
			Boolean asTopConcept) throws Exception {
		return ResponseEntity.ok(skosSchemeService.updateSkosConcept(skosSchemeUuid, skosConcept, asTopConcept));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KOS_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteSkosConcept(UUID skosSchemeUuid, UUID skosConceptUuid) throws Exception {
		skosSchemeService.deleteSkosConcept(skosSchemeUuid, skosConceptUuid);
		return ResponseEntity.ok().build();
	}
}
