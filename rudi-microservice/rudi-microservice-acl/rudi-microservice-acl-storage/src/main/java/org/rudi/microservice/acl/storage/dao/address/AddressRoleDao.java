package org.rudi.microservice.acl.storage.dao.address;

import org.rudi.common.storage.dao.StampedRepository;
import org.rudi.microservice.acl.storage.entity.address.AddressRoleEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les AddressRole
 *
 * @author NTR18299
 */
@Repository
public interface AddressRoleDao extends StampedRepository<AddressRoleEntity> {

}
