/**
 *
 */
package org.rudi.microservice.acl.service.role.impl;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;
import org.rudi.microservice.acl.service.mapper.RoleMapper;
import org.rudi.microservice.acl.service.role.RoleService;
import org.rudi.microservice.acl.storage.dao.role.RoleCustomDao;
import org.rudi.microservice.acl.storage.dao.role.RoleDao;
import org.rudi.microservice.acl.storage.entity.role.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des roles d'utilisateurs
 * 
 * @author MCY12700
 *
 */
@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private RoleCustomDao roleCustomDao;

	@Autowired
	private RoleMapper roleMapper;

	@Override
	public List<Role> searchRoles(RoleSearchCriteria searchCriteria) {
		return roleMapper.entitiesToDto(roleCustomDao.searchRoles(searchCriteria));
	}

	@Override
	public Role getRole(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		RoleEntity entity = roleDao.findByUUID(uuid);
		return roleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public Role createRole(Role role) {
		RoleEntity entity = roleMapper.dtoToEntity(role);
		entity.setUuid(UUID.randomUUID());
		roleDao.save(entity);
		return roleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public Role updateRole(Role role) {
		if (role.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
		RoleEntity entity = roleDao.findByUUID(role.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + role.getUuid());
		}
		roleMapper.dtoToEntity(role, entity);
		entity = roleDao.save(entity);
		return roleMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRole(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		RoleEntity entity = roleDao.findByUUID(uuid);
		roleDao.delete(entity);
	}

}
