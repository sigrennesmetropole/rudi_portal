package org.rudi.microservice.strukture.service.organization;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
public class OrganizationServiceTest {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private OrganizationDao organizationDao;

	@MockBean
	private ACLHelper aclHelper;

	@AfterEach
	void tearDown() {
		organizationDao.deleteAll();
	}

	@Test
	void createOrganization() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Ableton Live");
		organization.setDescription("DAW très bien");
		organization.setUrl("http://www.ableton.com");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);

		LocalDateTime date2 = LocalDateTime.of(2023, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setClosingDate(date2);

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);

		assertNotNull(created);
		assertNotNull(created.getUuid());

		OrganizationEntity inDb = organizationDao.findByUuid(created.getUuid());
		assertNotNull(inDb);
		assertEquals(organization.getName(), inDb.getName());
		assertEquals(organization.getOpeningDate(), inDb.getOpeningDate());
		assertEquals(organization.getClosingDate(), inDb.getClosingDate());
		assertEquals(organization.getDescription(), inDb.getDescription());
		assertEquals(organization.getUrl(), inDb.getUrl());
	}

	@Test
	void createOrganization_name_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Nom OK");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_name_KO() {

		Organization organization = new Organization();
		organization.setName("Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long " +
				"Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, ()-> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_openingDate_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("OpeningDate OK");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_openingDate_KO() {

		Organization organization = new Organization();
		organization.setName("Opening date is missing");

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, ()-> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_description_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Description OK");
		organization.setDescription("Ceci est ma description, courte.");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_description_KO() {

		Organization organization = new Organization();
		organization.setName("Description KO");
		organization.setDescription("Description trop longue de + de 800 caractères ah oui quand même au bout d'un moment" +
				"j'ai envie de dire enfin voilà quoi après bon. S'il faut meubler autant s'entraîner à la dactilographie n'est-ce pas ? " +
				"Car ce texte a été écrit à la main pour rendre les tests authentiques de toute manière la façon dont la phrase est tournée" +
				"n'a que peu d'incidence sur le résultat fonctionnel du TU après tout ? nan je crois que ça se rapproche la ? peut-être pas assez" +
				"je ne sais pas réellement. J'avoue je m'amuse un peu avec la musique dans les oreilles et tou AAAA quel dommage je devrais" +
				"écouter du Tenwing ça au moins c'est du lourd, let's go Spotify et Deezer et tout quel artiste de fou et je dis pas ça" +
				"parce que j'ai un intérêt derrière et tout hehehehehehe. Bon la je fais du padding pour être sûr quoicoubehhhh hein quoi ?" +
				"hein ? Apagnan enfin ça s'écrit pas comme ça je crois");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, ()-> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_url_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Url OK");
		organization.setUrl("https://mavieskoa.com");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_url_KO() {

		Organization organization = new Organization();
		organization.setName("Url OK");
		organization.setUrl("https://mavieskoa?query=recherche+trop+longue+faut+pas+faire+ca+surtout+que+je+renvoie+oui.com");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, ()-> organizationService.createOrganization(organization));
	}

	@Test
	void searchOrganization() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Fruity Loops");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		criteria.setUuid(created.getUuid());
		Page<Organization> organizations = organizationService.searchOrganizations(criteria, Pageable.unpaged());
		assertTrue(organizations.get().anyMatch(collected -> collected.getName().equals(organization.getName())));
		assertTrue(organizations.get().anyMatch(collected -> collected.getOpeningDate().equals(organization.getOpeningDate())));
	}

	@Test
	void updateOrganization() throws AppServiceException {

		Organization organization = new Organization();
		organization.setName("Novation");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		created.setName("Teenage Engineering");
		created.setOpeningDate(LocalDateTime.now());

		organizationService.updateOrganization(created);

		OrganizationEntity updated = organizationDao.findByUuid(created.getUuid());
		assertNotEquals(updated.getName(), organization.getName());
		assertNotEquals(updated.getOpeningDate(), organization.getOpeningDate());
	}

	@Test
	void deleteOrganization() throws AppServiceException {

		Organization organization = new Organization();
		organization.setName("Sloclap");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		organizationService.deleteOrganization(created.getUuid());

		assertTrue(CollectionUtils.isEmpty(organizationDao.findAll()));
	}
}
