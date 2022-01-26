/**
 *
 */
package org.rudi.microservice.acl.service.role;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;

/**
 * @author MCY12700
 *
 */
public interface RoleService {

	/**
	 * List all Role
	 *
	 * @return providers list
	 */
	List<Role> searchRoles(RoleSearchCriteria searchCriteria);

	/**
	 * provider
	 *
	 * @return Role list
	 */
	Role getRole(UUID uuid);

	/**
	 * Create a Role
	 *
	 * @param role
	 * @return
	 */
	Role createRole(Role role);

	/**
	 * Update a Role entity
	 *
	 * @param role
	 * @return
	 */
	Role updateRole(Role role);

	/**
	 * Delete a Role entity
	 *
	 * @param uuid
	 */
	void deleteRole(UUID uuid);

}
