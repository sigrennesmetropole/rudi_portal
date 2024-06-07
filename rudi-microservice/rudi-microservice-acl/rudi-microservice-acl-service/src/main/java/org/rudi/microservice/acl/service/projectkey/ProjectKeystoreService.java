/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.projectkey;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.acl.core.bean.ProjectKey;
import org.rudi.microservice.acl.core.bean.ProjectKeystore;
import org.rudi.microservice.acl.core.bean.ProjectKeystoreSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface ProjectKeystoreService {

	ProjectKeystore createProjectKeystore(ProjectKeystore projectKeystore) throws AppServiceException;

	void deleteProjectKeystore(UUID projectKeystoreUuid) throws AppServiceException;

	ProjectKey createProjectKey(UUID projectKeystoreUuid, ProjectKey projectKey) throws AppServiceException;

	void deleteProjectKey(UUID projectKeystoreUuid, UUID projectKeyUuid) throws AppServiceException;

	Page<ProjectKeystore> searchProjectKey(ProjectKeystoreSearchCriteria searchCriteria, Pageable pageable);

	ProjectKeystore getProjectKeystoreByUUID(UUID projectKeystoreUuid) throws AppServiceException;
}
