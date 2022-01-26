package org.rudi.microservice.acl.storage.dao.user;

import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Permet d'obtenir une liste de users paginée et triée
 */
public interface UserCustomDao {
	Page<UserEntity> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable);
}
