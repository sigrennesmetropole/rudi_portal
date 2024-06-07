package org.rudi.microservice.acl.storage.dao.projectkey;

import org.rudi.microservice.acl.core.bean.ProjectKeystoreSearchCriteria;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeystoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Permet d'obtenir une liste de project keystore paginée et triée
 */
public interface ProjectKeystoreCustomDao {
	Page<ProjectKeystoreEntity> searchUsers(ProjectKeystoreSearchCriteria searchCriteria, Pageable pageable);
}
