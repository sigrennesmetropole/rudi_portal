package org.rudi.microservice.strukture.service.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.strukture.core.bean.AbstractAddress;
import org.rudi.microservice.strukture.core.bean.AddressRole;
import org.rudi.microservice.strukture.core.bean.AddressType;
import org.rudi.microservice.strukture.core.bean.EmailAddress;
import org.rudi.microservice.strukture.core.bean.NodeProvider;
import org.rudi.microservice.strukture.core.bean.PostalAddress;
import org.rudi.microservice.strukture.core.bean.Provider;
import org.rudi.microservice.strukture.core.bean.ProviderSearchCriteria;
import org.rudi.microservice.strukture.core.bean.TelephoneAddress;
import org.rudi.microservice.strukture.core.bean.WebsiteAddress;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.address.AddressRoleService;
import org.rudi.microservice.strukture.storage.dao.address.AbstractAddressDao;
import org.rudi.microservice.strukture.storage.dao.address.AddressRoleDao;
import org.rudi.microservice.strukture.storage.dao.provider.ProviderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Class de test du service des Providers
 */

@StruktureSpringBootTest
class ProviderServiceUT {

	@Autowired
	private ProviderService providerService;

	@Autowired
	AddressRoleService addressRoleService;

	@Autowired
	private ProviderDao providerDao;

	@Autowired
	private AddressRoleDao addressRoleDao;

	@Autowired
	private AbstractAddressDao abstractAddressDao;

	@MockBean
	DatasetService datasetService;

	// données insérées en base before test
	private Provider transportProvider;
	private Provider gazProvider;
	private AddressRole roleTelephonePro;
	private AddressRole roleSiteWebProjet;
	private AddressRole roleMailHotline;

	@BeforeEach
	public void initData() {
		transportProvider = initTransportProvider();
		gazProvider = initGazProvider();

		// création en base de transportProvider et gazProvider
		transportProvider = providerService.createProvider(transportProvider);
		gazProvider = providerService.createProvider(gazProvider);

		// création en base des roles d'adresse
		roleTelephonePro = addressRoleService.createAddressRole(initRoleTelephonePro());
		roleSiteWebProjet = addressRoleService.createAddressRole(initRoleAdresseSiteWeb());
		roleMailHotline = addressRoleService.createAddressRole(initRoleAdresseMailHotline());
	}

	@AfterEach
	public void cleanData() {
		providerDao.deleteAll();
		abstractAddressDao.deleteAll();
		addressRoleDao.deleteAll();
	}

	private NodeProvider initNodeProvider() {
		NodeProvider nodeProvider = new NodeProvider();
		nodeProvider.setVersion("v4.11.0.0");
		nodeProvider.setUrl("test.com");

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		nodeProvider.setOpeningDate(openingDate);
		return nodeProvider;
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

	private AddressRole initRoleAdresseSiteWeb() {
		AddressRole addressRole = new AddressRole();
		addressRole.setCode("PROJET");
		addressRole.setLabel("site web projet");
		addressRole.setType(AddressType.WEBSITE);
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

	private AddressRole initRoleAdresseMailHotline() {
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

	private Provider initVilleProvider() {

		Provider villeProvider = new Provider();
		villeProvider.setCode("CITY");
		villeProvider.setLabel("Fournisseur City Metropole");

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		villeProvider.setOpeningDate(openingDate);

		LocalDateTime closingDate = LocalDateTime.of(openingDate.getYear() + 1, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		villeProvider.setClosingDate(closingDate);

		return villeProvider;
	}

	private Provider initTransportProvider() {

		transportProvider = new Provider();
		transportProvider.setCode("TRANSP");
		transportProvider.setLabel("Fournisseur Transport");

		Date dateOpening = new Date();
		dateOpening.setTime(dateOpening.getTime() - 10000);
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear() - 5, today.getMonthValue(), today.getDayOfMonth(),
				0, 0);
		transportProvider.setOpeningDate(openingDate);
		transportProvider.setClosingDate(null);

		return transportProvider;
	}

	private Provider initGazProvider() {

		gazProvider = new Provider();
		gazProvider.setCode("GAZ");
		gazProvider.setLabel("Fournisseur Gaz");

		Date dateOpening = new Date();
		dateOpening.setTime(dateOpening.getTime() - 10000);
		gazProvider.setOpeningDate(
				Instant.ofEpochMilli(dateOpening.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());

		gazProvider.setClosingDate(null);

		return gazProvider;
	}

	@Test
	void testCRUDProviders() throws AppServiceException {

		assertNotNull(providerService);
		long nbProvider = providerDao.count();

		// creation
		// ---------
		// initialisation de villeProvider
		Provider villeProvider = initVilleProvider();
		LocalDateTime openingDate = villeProvider.getOpeningDate();
		LocalDateTime closingDate = villeProvider.getClosingDate();

		// creation de villeProvider
		Provider providerCree = providerService.createProvider(villeProvider);

		assertNotNull(providerCree);
		assertNotNull(providerCree.getUuid());
		assertEquals("CITY", providerCree.getCode());
		assertEquals("Fournisseur City Metropole", providerCree.getLabel());
		assertEquals(openingDate, providerCree.getOpeningDate());
		assertEquals(closingDate, providerCree.getClosingDate());
		assertNull(villeProvider.getAddresses());
		assertNull(villeProvider.getNodeProviders());

		assertEquals(nbProvider + 1, providerDao.count());

		// chargement d'un provider light
		// ------------------------------
		Provider villeProviderLight = providerService.getProvider(providerCree.getUuid(), false);
		assertNotNull(villeProviderLight);
		assertEquals(villeProviderLight.getUuid(), providerCree.getUuid());

		// recherche
		// ---------
		// recherche sans critères
		Page<Provider> pageResult = providerService.searchProviders(new ProviderSearchCriteria(),
				PageRequest.of(0, 100));
		assertEquals(nbProvider + 1, pageResult.getNumberOfElements());

		// recherche tous critères
		ProviderSearchCriteria criteriaFull = new ProviderSearchCriteria();
		criteriaFull.setCode("CITY");
		criteriaFull.setLabel("Fournisseur City Metropole");
		criteriaFull.setActive(true);
		criteriaFull.setFull(true);
		pageResult = providerService.searchProviders(criteriaFull, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		Provider selectedProvider = pageResult.toList().get(0);
		assertEquals("CITY", selectedProvider.getCode());
		assertEquals("Fournisseur City Metropole", selectedProvider.getLabel());

		// recherche par code
		ProviderSearchCriteria criteriaByCode = new ProviderSearchCriteria();
		criteriaByCode.setCode("CITY");
		pageResult = providerService.searchProviders(criteriaByCode, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		selectedProvider = pageResult.toList().get(0);
		assertEquals("CITY", selectedProvider.getCode());

		// recherche par dateDebut et dateFin : fournisseur inclus dans la période
		ProviderSearchCriteria criteriaByDate = new ProviderSearchCriteria();
		LocalDateTime dateDebut = openingDate.minusDays(10);
		LocalDateTime dateFin = closingDate.plusYears(1);
		criteriaByDate.setDateDebut(dateDebut);
		criteriaByDate.setDateFin(dateFin);

		pageResult = providerService.searchProviders(criteriaByDate, PageRequest.of(0, 100));

		assertTrue(pageResult.getNumberOfElements() > 0);
		assertTrue(pageResult.toList().contains(providerCree));

		// recherche par dateDebut : fournisseur non actif sur la période
		criteriaByDate.setCode("CITY");
		criteriaByDate.setDateDebut(closingDate.plusDays(1));
		criteriaByDate.setDateFin(null);

		pageResult = providerService.searchProviders(criteriaByDate, PageRequest.of(0, 100));
		assertEquals(0, pageResult.getNumberOfElements());

		// recherche par dateFin : fournisseur actif sur la période
		criteriaByDate.setCode("CITY");
		criteriaByDate.setDateDebut(null);
		criteriaByCode.setDateFin(closingDate);

		pageResult = providerService.searchProviders(criteriaByDate, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		assertTrue(pageResult.toList().contains(providerCree));

		// recherche par label
		ProviderSearchCriteria criteriaByLabel = new ProviderSearchCriteria();
		criteriaByLabel.setLabel("Fournisseur City Metropole");
		pageResult = providerService.searchProviders(criteriaByLabel, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		selectedProvider = pageResult.toList().get(0);
		assertEquals("Fournisseur City Metropole", selectedProvider.getLabel());

		// modification
		// ------------
		villeProviderLight.setCode("FCM");
		villeProviderLight.setLabel("Fournisseur City Modifie");
		LocalDateTime newOpeningDate = LocalDateTime.of(openingDate.getYear() - 1, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		villeProviderLight.setOpeningDate(newOpeningDate);
		LocalDateTime newClosingDate = LocalDateTime.of(closingDate.getYear() + 1, closingDate.getMonthValue(),
				closingDate.getDayOfMonth(), 0, 0);
		villeProviderLight.setClosingDate(newClosingDate);

		Provider providerModifie = providerService.updateProvider(villeProviderLight);

		assertNotNull(providerModifie);
		assertEquals("FCM", providerModifie.getCode());
		assertEquals("Fournisseur City Modifie", providerModifie.getLabel());
		assertEquals(newOpeningDate, providerModifie.getOpeningDate());
		assertEquals(newClosingDate, providerModifie.getClosingDate());

		// recherche avec les anciens code et label
		pageResult = providerService.searchProviders(criteriaByLabel, PageRequest.of(0, 100));
		assertEquals(0, pageResult.getNumberOfElements());

		pageResult = providerService.searchProviders(criteriaByCode, PageRequest.of(0, 100));
		assertEquals(0, pageResult.getNumberOfElements());

		// suppression d'un provider (sans noeuds ni adressses)
		// ----------------------------------------------------
		UUID uuid = providerModifie.getUuid();
		providerService.deleteProvider(uuid);

		assertEquals(nbProvider, providerDao.count());

	}

	@Test
	void testCRUDAddresses() throws AppServiceException {

		assertNotNull(transportProvider);
		assertNotNull(roleSiteWebProjet);

		// Création des différents types d'adresses
		// ----------------------------------------

		// Création d'une adresse web
		WebsiteAddress webAddress = new WebsiteAddress();
		webAddress.setType(AddressType.WEBSITE);
		webAddress.setUrl("https://metropole.rennes.fr");
		webAddress.setAddressRole(roleSiteWebProjet);

		AbstractAddress abstractAddress1 = providerService.createAddress(transportProvider.getUuid(), webAddress);

		assertNotNull(abstractAddress1);
		assertNotNull(abstractAddress1.getUuid());
		assertTrue(abstractAddress1 instanceof WebsiteAddress);
		WebsiteAddress adresseWebCree = (WebsiteAddress) abstractAddress1;

		assertEquals(AddressType.WEBSITE, adresseWebCree.getType());
		assertEquals("https://metropole.rennes.fr", adresseWebCree.getUrl());
		assertEquals(roleSiteWebProjet.getUuid(), adresseWebCree.getAddressRole().getUuid());

		// création d'une adresse postale (pas de role d'adresse)
		PostalAddress postalAddress = new PostalAddress();
		postalAddress.setType(AddressType.POSTAL);
		postalAddress.setRecipientIdentification("Musée des Beaux-Arts");
		postalAddress.setStreetNumber("20 Quai Emile Zola");
		postalAddress.setLocality("35000 Rennes");
		postalAddress.setAdditionalIdentification("bâtiment B");
		postalAddress.setDistributionService("lieu dit les Glénans");

		AbstractAddress abstractAddress2 = providerService.createAddress(transportProvider.getUuid(), postalAddress);

		assertNotNull(abstractAddress2);
		assertNotNull(abstractAddress2.getUuid());
		assertTrue(abstractAddress2 instanceof PostalAddress);
		PostalAddress adressePostaleCreee = (PostalAddress) abstractAddress2;

		assertEquals(AddressType.POSTAL, adressePostaleCreee.getType());
		assertEquals("Musée des Beaux-Arts", adressePostaleCreee.getRecipientIdentification());
		assertEquals("20 Quai Emile Zola", adressePostaleCreee.getStreetNumber());
		assertEquals("35000 Rennes", adressePostaleCreee.getLocality());
		assertEquals("bâtiment B", adressePostaleCreee.getAdditionalIdentification());
		assertEquals("lieu dit les Glénans", adressePostaleCreee.getDistributionService());

		// création d'une adresse mail
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setType(AddressType.EMAIL);
		emailAddress.setEmail("harry.cover@free.fr");
		emailAddress.setAddressRole(roleMailHotline);

		AbstractAddress abstractAddress3 = providerService.createAddress(transportProvider.getUuid(), emailAddress);

		assertNotNull(abstractAddress3);
		assertNotNull(abstractAddress3.getUuid());
		assertTrue(abstractAddress3 instanceof EmailAddress);
		EmailAddress adresseMailCreee = (EmailAddress) abstractAddress3;
		assertEquals(AddressType.EMAIL, adresseMailCreee.getType());
		assertEquals("harry.cover@free.fr", adresseMailCreee.getEmail());
		assertEquals(roleMailHotline.getUuid(), adresseMailCreee.getAddressRole().getUuid());

		// création d'une adresse téléphone
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setType(AddressType.PHONE);
		telephoneAddress.setPhoneNumber("02 33 78 55 00");
		telephoneAddress.setAddressRole(roleTelephonePro);

		AbstractAddress abstractAddress4 = providerService.createAddress(transportProvider.getUuid(), telephoneAddress);

		assertNotNull(abstractAddress4);
		assertNotNull(abstractAddress4.getUuid());
		assertTrue(abstractAddress4 instanceof TelephoneAddress);
		TelephoneAddress adresseTelCreee = (TelephoneAddress) abstractAddress4;
		assertEquals(AddressType.PHONE, adresseTelCreee.getType());
		assertEquals("02 33 78 55 00", adresseTelCreee.getPhoneNumber());
		assertEquals(roleTelephonePro.getUuid(), adresseTelCreee.getAddressRole().getUuid());

		int nbAddressesCreees = 4;

		// chargement d'une adresse d'un fournisseur
		// ----------------------------------------
		abstractAddress1 = providerService.getAddress(transportProvider.getUuid(), adresseWebCree.getUuid());
		assertNotNull(abstractAddress1);
		assertEquals(adresseWebCree.getUuid(), abstractAddress1.getUuid());
		assertTrue(abstractAddress1 instanceof WebsiteAddress);

		// chargement de la liste des adresses d'un fournisseur
		// ----------------------------------------------------
		List<AbstractAddress> providerAddresses = providerService.getAddresses(transportProvider.getUuid());
		assertNotNull(providerAddresses);
		assertEquals(nbAddressesCreees, providerAddresses.size());
		assertTrue(providerAddresses.contains(abstractAddress1));
		assertTrue(providerAddresses.contains(abstractAddress2));
		assertTrue(providerAddresses.contains(abstractAddress3));
		assertTrue(providerAddresses.contains(abstractAddress4));

		// modification d'une adresse
		// --------------------------
		adresseWebCree.setUrl("https://metropole.toulouse.fr");
		AbstractAddress abstractAddressModifiee = providerService.updateAddress(transportProvider.getUuid(),
				adresseWebCree);
		WebsiteAddress adresseWebModifee = (WebsiteAddress) abstractAddressModifiee;
		assertEquals("https://metropole.toulouse.fr", adresseWebModifee.getUrl());

		// chargement d'un provider full (avec adresses et noeuds)
		// ------------------------------
		Provider transportProviderFull = providerService.getProvider(transportProvider.getUuid(), true);
		assertNotNull(transportProviderFull);
		assertEquals(transportProviderFull.getUuid(), transportProvider.getUuid());
		assertEquals(nbAddressesCreees, transportProviderFull.getAddresses().size());
		assertTrue(providerAddresses.contains(abstractAddress1));

		// suppression d'une adresse
		// -------------------------
		providerService.deleteAddress(transportProvider.getUuid(), adresseWebCree.getUuid());

		assertNull(providerService.getAddress(transportProvider.getUuid(), adresseWebCree.getUuid()));

		// suppression d'un fournisseur qui a des adresses
		// -----------------------------------------------
		long nbProvider = providerDao.count();

		providerService.deleteProvider(transportProvider.getUuid());

		assertEquals(nbProvider - 1, providerDao.count());
	}

	@Test
	void testCRUDNodeProvider() throws AppServiceException {

		assertNotNull(gazProvider);

		// Création d'un noeud fournisseur
		// -------------------------------
		NodeProvider nodeProvider = new NodeProvider();
		nodeProvider.setVersion("v1.0.0");
		nodeProvider.setUrl("hello@hello.com");

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		nodeProvider.setOpeningDate(openingDate);

		NodeProvider node1 = providerService.createNode(gazProvider.getUuid(), nodeProvider);

		assertNotNull(node1);
		assertNotNull(node1.getUuid());
		assertEquals("hello@hello.com", node1.getUrl());
		assertEquals("v1.0.0", node1.getVersion());
		assertEquals(openingDate, node1.getOpeningDate());
		assertNull(node1.getClosingDate());

		// second noeud
		NodeProvider node2 = providerService.createNode(gazProvider.getUuid(), initNodeProvider());
		assertNotNull(node2);

		int nbNoeudsCrees = 2;

		// chargement d'une noeud fournisseur
		// ----------------------------------
		NodeProvider loadedNode = providerService.getNode(gazProvider.getUuid(), node1.getUuid());
		assertNotNull(loadedNode);
		assertEquals(node1.getUuid(), loadedNode.getUuid());

		// chargement de la liste des noeuds d'un fournisseur
		// --------------------------------------------------
		List<NodeProvider> nodes = providerService.getNodes(gazProvider.getUuid());
		assertNotNull(nodes);
		assertEquals(nbNoeudsCrees, nodes.size());
		assertTrue(nodes.contains(node1));
		assertTrue(nodes.contains(node2));

		// modification d'un noeud
		// -----------------------
		loadedNode.setUrl("www.urlmodifiee.com");
		loadedNode.setVersion("2.0.0");
		LocalDateTime closingDate = LocalDateTime.of(openingDate.getYear() + 10, openingDate.getMonthValue(),
				openingDate.getDayOfMonth(), 0, 0);
		loadedNode.setClosingDate(closingDate);

		NodeProvider updatedNode = providerService.updateNode(gazProvider.getUuid(), loadedNode);
		assertEquals("www.urlmodifiee.com", updatedNode.getUrl());
		assertEquals("2.0.0", updatedNode.getVersion());
		assertEquals(closingDate, updatedNode.getClosingDate());

		// chargement d'un provider full (avec noeuds)
		// -------------------------------------------------------
		Provider providerFull = providerService.getProvider(gazProvider.getUuid(), true);
		assertNotNull(providerFull);
		assertEquals(providerFull.getUuid(), gazProvider.getUuid());
		assertEquals(nbNoeudsCrees, providerFull.getNodeProviders().size());
		assertTrue(providerFull.getNodeProviders().contains(updatedNode));
		assertTrue(providerFull.getNodeProviders().contains(node2));

		// chargement des fournisseurs ayant un noeud donné
		// ------------------------------------------------
		// chargement light
		ProviderSearchCriteria criteria = new ProviderSearchCriteria();
		criteria.setNodeProviderUuid(updatedNode.getUuid());
		criteria.setFull(false);
		Page<Provider> pageResult = providerService.searchProviders(criteria, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		Provider selectedProvider = pageResult.toList().get(0);
		assertEquals("GAZ", selectedProvider.getCode());

		// chargement full
		criteria.setFull(true);
		pageResult = providerService.searchProviders(criteria, PageRequest.of(0, 100));
		assertEquals(1, pageResult.getNumberOfElements());
		selectedProvider = pageResult.toList().get(0);
		assertEquals("GAZ", selectedProvider.getCode());
		assertEquals(nbNoeudsCrees, selectedProvider.getNodeProviders().size());
		assertTrue(selectedProvider.getNodeProviders().contains(updatedNode));

		// suppression d'un noeud
		// -------------------------
		providerService.deleteNode(gazProvider.getUuid(), updatedNode.getUuid());

		assertNull(providerService.getNode(gazProvider.getUuid(), updatedNode.getUuid()));

		providerFull = providerService.getProvider(gazProvider.getUuid(), true);
		assertNotNull(providerFull);
		assertEquals(providerFull.getUuid(), gazProvider.getUuid());
		assertEquals(nbNoeudsCrees - 1, providerFull.getNodeProviders().size());

		// suppression d'un fournisseur qui a des noeuds
		// -----------------------------------------------
		long nbProvider = providerDao.count();

		providerService.deleteProvider(gazProvider.getUuid());

		assertEquals(nbProvider - 1, providerDao.count());
	}

}
