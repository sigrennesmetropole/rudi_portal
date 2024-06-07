/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_APIGATEWAY_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_PROJEKT_ADMINISTRATOR;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.acl.core.bean.ProjectKey;
import org.rudi.microservice.acl.core.bean.ProjectKeystore;
import org.rudi.microservice.acl.core.bean.ProjectKeystorePageResult;
import org.rudi.microservice.acl.core.bean.ProjectKeystoreSearchCriteria;
import org.rudi.microservice.acl.facade.controller.api.ProjectKeystoresApi;
import org.rudi.microservice.acl.service.projectkey.ProjectKeystoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@RestController
@RequiredArgsConstructor
public class ProjectKeystoreController implements ProjectKeystoresApi {

	private final ProjectKeystoreService projectKeystoreService;

	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + ")")
	public ResponseEntity<ProjectKeystore> createProjectKeystore(ProjectKeystore projectKeystore) throws Exception {
		return ResponseEntity.ok(projectKeystoreService.createProjectKeystore(projectKeystore));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteProjectKeystore(UUID projectKeystoreUuid) throws Exception {
		projectKeystoreService.deleteProjectKeystore(projectKeystoreUuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + ")")
	public ResponseEntity<ProjectKey> createProjectKey(UUID projectKeystoreUuid, ProjectKey projectKey)
			throws Exception {
		return ResponseEntity.ok(projectKeystoreService.createProjectKey(projectKeystoreUuid, projectKey));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteProjectKey(UUID projectKeystoreUuid, UUID projectKeyUuid) throws Exception {
		projectKeystoreService.deleteProjectKey(projectKeystoreUuid, projectKeyUuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + ")")
	public ResponseEntity<ProjectKeystore> getProjectKeystore(UUID projectKeystoreUuid) throws Exception {
		return ResponseEntity.ok(projectKeystoreService.getProjectKeystoreByUUID(projectKeystoreUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_PROJEKT_ADMINISTRATOR + "," + MODULE_APIGATEWAY_ADMINISTRATOR + ")")
	public ResponseEntity<ProjectKeystorePageResult> searchProjectKeystores(List<UUID> projectUuids,
			OffsetDateTime minExpirationDate, OffsetDateTime maxExpirationDate, String clientId, Integer offset,
			Integer limit, String order) throws Exception {
		ProjectKeystoreSearchCriteria searchCriteria = ProjectKeystoreSearchCriteria.builder()
				.projectUuids(projectUuids).minExpirationDate(minExpirationDate).maxExpirationDate(maxExpirationDate)
				.clientId(clientId).build();

		Pageable pageable = utilPageable.getPageable(offset, limit, order);

		Page<ProjectKeystore> page = projectKeystoreService.searchProjectKey(searchCriteria, pageable);

		ProjectKeystorePageResult result = new ProjectKeystorePageResult();
		result.setTotal(page.getTotalElements());
		result.setElements(page.getContent());

		return ResponseEntity.ok(result);
	}
}
