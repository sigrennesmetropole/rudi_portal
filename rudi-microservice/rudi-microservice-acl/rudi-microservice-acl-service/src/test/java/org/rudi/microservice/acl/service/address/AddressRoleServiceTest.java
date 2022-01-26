package org.rudi.microservice.acl.service.address;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.microservice.acl.core.bean.AddressRole;
import org.rudi.microservice.acl.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.acl.core.bean.AddressType;
import org.rudi.microservice.acl.service.SpringBootTestApplication;
import org.rudi.microservice.acl.storage.dao.address.AddressRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Class de test du service AddressRoleService
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
public class AddressRoleServiceTest {

	@Autowired
	private AddressRoleService addressRoleService;

	@Autowired
	private AddressRoleDao addressRoleDao;

	private AddressRole roleMailHotline;

	private long nbAddressRoleAvantInitData;
	// TODO on ajoute ce paramètre pour ce test car pour une raison inconnue Flyway est lancé sur les postes de dev mais pas sur la PIC (cf address_role de type "address_role" ajoutés par le fichier src/main/resources/bdd/V1__initialisation.sql)
	private long nbAddressRoleDeTypeEmailApresMigrationFlyway;
	private AddressRole addressRoleCree;

	@BeforeEach
	public void initData() {

		nbAddressRoleAvantInitData = addressRoleDao.count();

		final AddressRoleSearchCriteria criteria = new AddressRoleSearchCriteria();
		criteria.setType(AddressType.EMAIL);
		nbAddressRoleDeTypeEmailApresMigrationFlyway = addressRoleService.searchAddressRoles(criteria).size();

		roleMailHotline = addressRoleService.createAddressRole(initRoleAdresseMail());

	}

	@AfterEach
	public void cleanData() {
		if (nbAddressRoleAvantInitData == 0) {
			addressRoleDao.deleteAll();
		} else {
			if (roleMailHotline != null) {
				addressRoleService.deleteAddressRole(roleMailHotline.getUuid());
			}
			if (addressRoleCree != null) {
				addressRoleService.deleteAddressRole(addressRoleCree.getUuid());
			}
		}

		assertEquals(nbAddressRoleAvantInitData, addressRoleDao.count());
	}

	private AddressRole initRoleTelephonePro() {
		AddressRole addressRole = new AddressRole();
		addressRole.setCode("PRO");
		addressRole.setLabel("téléphone professionnel");
		addressRole.setType(AddressType.PHONE);
		addressRole.setOrder(1);
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		addressRole.setOpeningDate(openingDate);

		LocalDateTime closingDate = LocalDateTime.of(openingDate.getYear() + 10, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		addressRole.setClosingDate(closingDate);

		return addressRole;
	}

	private AddressRole initRoleAdresseMail() {
		AddressRole addressRole = new AddressRole();
		addressRole.setCode("HOTLINE");
		addressRole.setLabel("mail hotline");
		addressRole.setType(AddressType.EMAIL);
		addressRole.setOrder(1);
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		addressRole.setOpeningDate(openingDate);

		LocalDateTime closingDate = LocalDateTime.of(openingDate.getYear() + 10, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		addressRole.setClosingDate(closingDate);

		return addressRole;
	}

	@Test
	@Disabled
	public void testCRUDAddressRole() {

		assertNotNull(addressRoleService);
		assertNotNull(roleMailHotline);

		long nbAddressRole = addressRoleDao.count();

		// création d'un role d'adresse
		// ----------------------------
		AddressRole telephonePro = initRoleTelephonePro();
		LocalDateTime openingDate = telephonePro.getOpeningDate();
		LocalDateTime closingDate = telephonePro.getClosingDate();

		addressRoleCree = addressRoleService.createAddressRole(telephonePro);

		assertEquals("PRO", addressRoleCree.getCode());
		assertEquals("téléphone professionnel", addressRoleCree.getLabel());
		assertEquals(AddressType.PHONE, addressRoleCree.getType());
		assertEquals(Integer.valueOf(1), addressRoleCree.getOrder());
		assertEquals(openingDate, addressRoleCree.getOpeningDate());
		assertEquals(closingDate, addressRoleCree.getClosingDate());

		assertEquals(nbAddressRole + 1, addressRoleDao.count());

		// chargement d'un role d'adresse
		// -------------------------------
		AddressRole addresseRoleChargee = addressRoleService.getAddressRole(addressRoleCree.getUuid());
		assertEquals("PRO", addresseRoleChargee.getCode());
		assertEquals("téléphone professionnel", addresseRoleChargee.getLabel());

		// modification d'un role d'adresse
		// --------------------------------
		addresseRoleChargee.setCode("PRO1");
		addresseRoleChargee.setClosingDate(null);

		AddressRole updatedAddressRole = addressRoleService.updateAddressRole(addresseRoleChargee);
		assertEquals("PRO1", updatedAddressRole.getCode());
		assertNull(updatedAddressRole.getClosingDate());

		// recherche
		// ---------

		// recherche sans critères
		List<AddressRole> addressRoles = addressRoleService.searchAddressRoles(new AddressRoleSearchCriteria());
		assertEquals(nbAddressRole + 1, addressRoles.size());

		// recherche tous critères
		AddressRoleSearchCriteria criteria = new AddressRoleSearchCriteria();
		criteria.setType(AddressType.PHONE);
		criteria.setActive(true);
		addressRoles = addressRoleService.searchAddressRoles(criteria);
		assertEquals(1, addressRoles.size());
		assertEquals(updatedAddressRole.getUuid(), addressRoles.get(0).getUuid());

		// recherche par type
		criteria = new AddressRoleSearchCriteria();
		criteria.setType(AddressType.EMAIL);
		addressRoles = addressRoleService.searchAddressRoles(criteria);
		assertEquals(nbAddressRoleDeTypeEmailApresMigrationFlyway + 1, addressRoles.size());
		assertEquals(roleMailHotline.getUuid(), addressRoles.get(0).getUuid());
	}
}
