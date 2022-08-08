/**
 *
 */
package org.rudi.microservice.strukture.service.address.impl;

import org.rudi.microservice.strukture.core.bean.AddressRole;
import org.rudi.microservice.strukture.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.strukture.service.address.AddressRoleService;
import org.rudi.microservice.strukture.service.mapper.AddressRoleMapper;
import org.rudi.microservice.strukture.storage.dao.address.AddressRoleCustomDao;
import org.rudi.microservice.strukture.storage.dao.address.AddressRoleDao;
import org.rudi.microservice.strukture.storage.entity.address.AddressRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author NTR18299
 *
 */
@Service
@Transactional(readOnly = true)
public class AddressRoleServiceImpl implements AddressRoleService {

	@Autowired
	private AddressRoleDao addressRoleDao;

	@Autowired
	private AddressRoleCustomDao addressRoleCustomDao;

	@Autowired
	private AddressRoleMapper addressRoleMapper;

	@Override
	public List<AddressRole> searchAddressRoles(AddressRoleSearchCriteria searchCriteria) {
		return addressRoleMapper.entitiesToDto(addressRoleCustomDao.searchAddressRoles(searchCriteria));
	}

	@Override
	public AddressRole getAddressRole(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		AddressRoleEntity entity = addressRoleDao.findByUUID(uuid);
		return addressRoleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public AddressRole createAddressRole(AddressRole addressRole) {
		AddressRoleEntity entity = addressRoleMapper.dtoToEntity(addressRole);
		entity.setUuid(UUID.randomUUID());
		addressRoleDao.save(entity);
		return addressRoleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public AddressRole updateAddressRole(AddressRole addressRole) {
		if (addressRole.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
		AddressRoleEntity entity = addressRoleDao.findByUUID(addressRole.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + addressRole.getUuid());
		}
		addressRoleMapper.dtoToEntity(addressRole, entity);
		entity = addressRoleDao.save(entity);
		return addressRoleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAddressRole(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		AddressRoleEntity entity = addressRoleDao.findByUUID(uuid);
		addressRoleDao.delete(entity);
	}

}
