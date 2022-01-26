package org.rudi.microservice.acl.service.role;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;
import org.rudi.microservice.acl.service.SpringBootTestApplication;
import org.rudi.microservice.acl.storage.dao.role.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Class de test du service RoleService
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
public class RoleServiceTest {

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleDao roleDao;

	// donnees existant en base par défaut
	private Role utilisateurRole;
	private Role adminRole;
	private Role moduleProviderRole;

	@BeforeEach
	public void initData() {
		// roleUtilisateur
		RoleSearchCriteria roleSearchCriteria = new RoleSearchCriteria();
		roleSearchCriteria.setActive(true);
		roleSearchCriteria.setCode("USER");
		List<Role> roles = roleService.searchRoles(roleSearchCriteria);
		assertEquals(1, roles.size());
		utilisateurRole = roles.get(0);

		// roleAdmin
		roleSearchCriteria.setCode("ADMINISTRATOR");
		roles = roleService.searchRoles(roleSearchCriteria);
		assertEquals(1, roles.size());
		adminRole = roles.get(0);

		// roleModuleProvider
		roleSearchCriteria.setCode("MODULE_PROVIDER");
		roles = roleService.searchRoles(roleSearchCriteria);
		assertEquals(1, roles.size());
		moduleProviderRole = roles.get(0);
	}

	@AfterEach
	public void cleanData() {
		// roleDao.deleteAll();
	}

	@Test
	public void testCRUDRole() {

		assertNotNull(roleService);

		assertNotNull(adminRole);
		assertNotNull(utilisateurRole);
		assertNotNull(moduleProviderRole);

		long nbRole = roleDao.count();

		// création d'un role
		// ------------------
		Role role = new Role();
		role.setCode("TEST");
		role.setLabel("Role de test");
		role.setOrder(999);
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		LocalDateTime closingDate = LocalDateTime.of(openingDate.getYear() + 10, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		role.setOpeningDate(openingDate);
		role.setClosingDate(closingDate);

		Role roleCree = roleService.createRole(role);

		assertEquals("TEST", roleCree.getCode());
		assertEquals("Role de test", roleCree.getLabel());
		assertEquals(999, roleCree.getOrder());
		assertEquals(openingDate, roleCree.getOpeningDate());
		assertEquals(closingDate, roleCree.getClosingDate());

		assertEquals(nbRole + 1, roleDao.count());

		// chargement d'un role
		// --------------------
		Role roleCharge = roleService.getRole(roleCree.getUuid());
		assertEquals("TEST", roleCharge.getCode());
		assertEquals("Role de test", roleCharge.getLabel());

		// modification d'un role
		// -----------------------
		roleCharge.setCode("TESTMODIF");
		roleCharge.setClosingDate(null);

		Role updatedRole = roleService.updateRole(roleCharge);
		assertEquals("TESTMODIF", updatedRole.getCode());
		assertNull(updatedRole.getClosingDate());

		// recherche
		// ---------

		// recherche sans critères
		List<Role> roles = roleService.searchRoles(new RoleSearchCriteria());
		assertEquals(nbRole + 1, roles.size());

		// recherche tous critères
		RoleSearchCriteria criteria = new RoleSearchCriteria();
		criteria.setActive(true);
		criteria.setCode("TESTMODIF");
		criteria.setLabel("Role de test");

		roles = roleService.searchRoles(criteria);

		assertEquals(1, roles.size());
		assertEquals(updatedRole.getUuid(), roles.get(0).getUuid());

		// suppression d'un role
		// ----------------------
		roleService.deleteRole(updatedRole.getUuid());
		assertEquals(nbRole, roleDao.count());

	}
}
