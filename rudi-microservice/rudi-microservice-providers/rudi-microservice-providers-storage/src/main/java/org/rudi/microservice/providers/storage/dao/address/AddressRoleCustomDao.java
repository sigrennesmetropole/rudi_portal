package org.rudi.microservice.providers.storage.dao.address;

import java.util.List;

import org.rudi.microservice.providers.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.providers.storage.entity.address.AddressRoleEntity;

/**
 * Permet d'obtenir une liste de rôles pour les adresses
 */
public interface AddressRoleCustomDao {

	List<AddressRoleEntity> searchAddressRoles(AddressRoleSearchCriteria searchCriteria);
}
