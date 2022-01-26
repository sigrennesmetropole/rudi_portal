package org.rudi.microservice.acl.storage.dao.address;

import java.util.List;

import org.rudi.microservice.acl.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.acl.storage.entity.address.AddressRoleEntity;

/**
 * Permet d'obtenir une liste de rôles pour les adresses
 */
public interface AddressRoleCustomDao {

	List<AddressRoleEntity> searchAddressRoles(AddressRoleSearchCriteria searchCriteria);
}
