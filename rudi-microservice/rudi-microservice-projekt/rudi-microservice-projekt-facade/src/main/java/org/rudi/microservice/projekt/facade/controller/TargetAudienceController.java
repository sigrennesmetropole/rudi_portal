package org.rudi.microservice.projekt.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.projekt.core.bean.PagedTargetAudienceList;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.core.bean.TargetAudienceSearchCriteria;
import org.rudi.microservice.projekt.facade.controller.api.TargetAudienceApi;
import org.rudi.microservice.projekt.service.targetaudience.TargetAudienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;

@RestController
@RequiredArgsConstructor
public class TargetAudienceController implements TargetAudienceApi {
	private final TargetAudienceService targetAudienceService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<TargetAudience> createTargetAudience(TargetAudience targetAudience) throws Exception {
		val created = targetAudienceService.createTargetAudience(targetAudience);
		return ResponseEntity.ok(created);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteTargetAudience(UUID uuid) {
		targetAudienceService.deleteTargetAudience(uuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<TargetAudience> getTargetAudience(UUID uuid) {
		return ResponseEntity.ok(targetAudienceService.getTargetAudience(uuid));
	}

	@Override
	public ResponseEntity<PagedTargetAudienceList> searchTargetAudiences(Integer limit, Integer offset, String order) {
		val searchCriteria = new TargetAudienceSearchCriteria();
		val pageable = utilPageable.getPageable(offset, limit, order);
		val page = targetAudienceService.searchTargetAudiences(searchCriteria, pageable);

		return ResponseEntity.ok(new PagedTargetAudienceList()
				.total(page.getTotalElements())
				.elements(page.toList())
		);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> updateTargetAudience(TargetAudience targetAudience) throws IllegalArgumentException {
		targetAudienceService.updateTargetAudience(targetAudience);
		return ResponseEntity.noContent().build();
	}
}
