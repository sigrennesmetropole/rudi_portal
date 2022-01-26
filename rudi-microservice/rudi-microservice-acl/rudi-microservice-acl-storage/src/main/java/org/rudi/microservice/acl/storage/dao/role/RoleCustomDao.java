package org.rudi.microservice.acl.storage.dao.role;

import java.util.List;

import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;
import org.rudi.microservice.acl.storage.entity.role.RoleEntity;

/**
 * Permet d'obtenir une liste de rôles pour utilisateurs
 */
public interface RoleCustomDao {

	List<RoleEntity> searchRoles(RoleSearchCriteria searchCriteria);
}
