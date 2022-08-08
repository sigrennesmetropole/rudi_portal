/**
 *
 */
package org.rudi.microservice.strukture.service.address;

import org.rudi.microservice.strukture.core.bean.AddressRole;
import org.rudi.microservice.strukture.core.bean.AddressRoleSearchCriteria;

import java.util.List;
import java.util.UUID;

/**
 * @author NTR18299
 *
 */
public interface AddressRoleService {

	/**
	 * List all AddressRole
	 *
	 * @return providers list
	 */
	List<AddressRole> searchAddressRoles(AddressRoleSearchCriteria searchCriteria);

	/**
	 * provider
	 *
	 * @return AddressRole list
	 */
	AddressRole getAddressRole(UUID uuid);

	/**
	 * Create a AddressRole
	 *
	 * @param addressRole
	 * @return
	 */
	AddressRole createAddressRole(AddressRole addressRole);

	/**
	 * Update a AddressRole entity
	 *
	 * @param addressRole
	 * @return
	 */
	AddressRole updateAddressRole(AddressRole addressRole);

	/**
	 * Delete a AddressRole entity
	 *
	 * @param uuid
	 */
	void deleteAddressRole(UUID uuid);

}
