package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.strukture.storage.entity.address.AddressRoleEntity;

import java.util.List;

/**
 * Permet d'obtenir une liste de rôles pour les adresses
 */
public interface AddressRoleCustomDao {

	List<AddressRoleEntity> searchAddressRoles(AddressRoleSearchCriteria searchCriteria);
}
