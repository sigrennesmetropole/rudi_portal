package org.rudi.microservice.strukture.service.organization.bean;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationBean;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.rudi.microservice.strukture.service.organization.OrganizationService;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
class OrganizationBeanServiceUT {

	// nécessaire pour créer des organizations
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrganizationBeanService organizationBeanService;
	@Autowired
	private OrganizationDao organizationDao;


	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ProjektHelper projektHelper;
	@MockBean
	OrganizationMembersHelper organizationMembersHelper;

	private UUID metalOwner;
	private UUID familialOwner;
	private UUID macumbaOwner;
	@AfterEach
	void tearDown() {
		organizationDao.deleteAll();
//		projektDao.deleteAll();
	}

	/**
	 * Permet de créer un lot de 5 organizations pour permettre les tests.
	 * Avec des dates de créations et des noms différents pour vérifier les tris.
	 *
	 *
	 * @return List<Organization> : La liste des organizations créées pour le test
	 * @throws AppServiceBadRequestException thown si les process "CreateOrganizationProcessor ne passent pas
	 */
	private List<Organization> createOrganizations() throws Exception {
		List<Organization> organizations = new ArrayList<>();
		metalOwner = UUID.randomUUID();
		familialOwner = UUID.randomUUID();
		macumbaOwner = UUID.randomUUID();
		
		// Organization HellFest
		LocalDateTime hFestCreationDate = LocalDateTime.of(2006, Month.JUNE, 23, 14, 0, 0, 0);
		LocalDateTime hFestEndingDate = LocalDateTime.of(2024, Month.JUNE, 27, 14, 30, 0, 0);
		Organization hellfest = createOneOrganization("Hellfest","Best fest that will you make drive on the Highway To Hell !","https://hellfest.fr/","@C!DcHigway2Hell!",hFestCreationDate, hFestEndingDate);
		organizations.add(hellfest);
		//Rajout d'un User d'organization pour les tris
		addMemberToAnOrganization("as-of-spades@lemmi.jd", metalOwner, hellfest.getUuid());


		//Organization Vieilles Charrues
		LocalDateTime vCharruesCreationDate = LocalDateTime.of(1992, Month.JULY, 4, 13, 0, 0, 0);
		LocalDateTime vCharruesEndingDate = LocalDateTime.of(2024, Month.JULY, 11, 14, 0, 0, 0);
		Organization vieillesCharrues = createOneOrganization("Vieilles Charrues","Un festival pour toute la famille, entre \"power roger\" et sanic","https://www.vieillescharrues.asso.fr/","Vi3ll3sCh@rru3s!",vCharruesCreationDate, vCharruesEndingDate);
		organizations.add(vieillesCharrues);
		//Rajout d'un User d'organization pour les tris
		addMemberToAnOrganization("brezzblock@delta.j", familialOwner, vieillesCharrues.getUuid());

		//Organization Wacken
		LocalDateTime wackenCreationDate = LocalDateTime.of(1990, Month.AUGUST, 24, 14, 0, 0, 0);
		LocalDateTime wackenEndingDate = LocalDateTime.of(2024, Month.JULY, 31, 14, 30, 0, 0);
		Organization wacken = createOneOrganization("Wacken","Wacken ist einen Metalfestival dass in Deutschland passiert.","https://www.wacken.com/en/","Wacken1stw@ckingUup!",wackenCreationDate, wackenEndingDate);
		organizations.add(wacken);
		//Rajout d'un User d'organization pour les tris
		addMemberToAnOrganization("ich-will@metal.de", metalOwner, wacken.getUuid());

		//Organization Motoc
		LocalDateTime motocCreationDate = LocalDateTime.of(2007, Month.AUGUST, 25, 16, 0, 0, 0);
		LocalDateTime motocEndingDate = LocalDateTime.of(2024, Month.AUGUST, 15, 15, 30, 0, 0);
		Organization motocultor = createOneOrganization("Motocultor","Here is some trash ! Bang !","https://www.motocultor-festival.com/","b33r&drugs&Metal",motocCreationDate, motocEndingDate);
		organizations.add(motocultor);
		//Rajout d'un User d'organization pour les tris
		addMemberToAnOrganization("motocultor@le-gros-bordel.bzh", metalOwner, motocultor.getUuid());

		//Organization Macumba
		LocalDateTime macumbaCreationDate = LocalDateTime.of(2021, Month.AUGUST, 5, 14, 0, 0, 0);
		LocalDateTime macumbaEndingDate = LocalDateTime.of(2024, Month.AUGUST, 2, 14, 30, 0, 0);
		Organization macumba = createOneOrganization("Macumba","What am I doing here ?","https://www.macumba-festival.fr/","bataille2Poubelles!",macumbaCreationDate, macumbaEndingDate);
		organizations.add(macumba);
		//Rajout d'un User d'organization pour les tris
		addMemberToAnOrganization("macumba@tous-les-soirs.drunk", macumbaOwner, macumba.getUuid());


		return organizations;
	}

	/**
	 *  Crée une organization à l'aide des paramètres donnés
	 *
	 * @param name : nom de l'organization
	 * @param description : description de l'oganization
	 * @param url : url de l'irganization
	 * @param password : mot de passe du USER d'organizatoin
	 * @param openingDate : date d'ouverture (création) de l'organization
	 * @param closingDate : @Nullable : date de fermeture de l'organization
	 * @return Organization : l'organization créée en BDD
	 *
	 * @throws AppServiceBadRequestException thown si les process "CreateOrganizationProcessor ne passent pas
	 */
	private Organization createOneOrganization(String name, String description, String url, String password, LocalDateTime openingDate, LocalDateTime closingDate) throws AppServiceBadRequestException {
		Organization organization = new Organization();
		organization.setName(name);
		organization.setDescription(description);
		organization.setUrl(url);

		organization.setOpeningDate(openingDate);

		if(closingDate != null){
			organization.setClosingDate(closingDate);
		}

		UUID organizationUuid = UUID.randomUUID();

		User userOrganisation = new User();
		userOrganisation.setUuid(organizationUuid);
		userOrganisation.setLogin(organizationUuid.toString());
		userOrganisation.setPassword(password);
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);


		return organizationService.createOrganization(organization);
	}

	/**
	 * Permet de rajouter un user à une organization
	 *
	 * @param login login du user
	 * @param userUuid uuid du user
	 * @param organizationUuid organization cible
	 * @throws Exception exception
	 */
	private void addMemberToAnOrganization(String login, UUID userUuid, UUID organizationUuid) throws Exception {
		OrganizationMember member = new OrganizationMember();
		member.setUserUuid(userUuid);
		member.setLogin(login);
		member.setUuid(organizationUuid);

		mockAuthenticationData(login, userUuid);
		mockExternalCalls();

		organizationService.addOrganizationMember(organizationUuid, member);
	}

	/**
	 *  mock les accès extérieurs pour la création des membres d'organization
	 *
	 * @throws AppServiceException exceptions
	 */
	private void mockExternalCalls() throws AppServiceException {
		when(organizationMembersHelper.isConnectedUserOrganizationAdministrator(any())).thenReturn(true);

		doNothing().when(projektHelper).notifyUserHasBeenAdded(any(), any());
	}

	/**
	 * mock l'authenticatedUser pour la création des membres d'organization
	 *
	 * @param login login du member
	 * @param userUuid uuid du member
	 * @throws AppServiceException exception
	 */
	private void mockAuthenticationData(String login, UUID userUuid) throws AppServiceException {
		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(login);
		User user = new User().login(authenticatedUser.getLogin()).uuid(userUuid);
		when(aclHelper.getUserByLogin(any())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(organizationMembersHelper.getUserByLoginOrByUuid(any(), any())).thenReturn(user);
	}


	@Test
	@DisplayName("Vérifier que je récupère bien l'ensemble des organization quand je ne met aucun paramètre.")
	void testSearchAllOrganizations() throws Exception {
		List<Organization> organizations = createOrganizations();

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		Pageable pageable = Pageable.unpaged();

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("Les listes doivent être équivalentes.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
					.allMatch(bean -> organizations.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));

	}

	@Test
	@DisplayName("Vérifie l'absence que je peux bien remonter les Organizations liées à un utilisateur")
	void testSearchMyOrganizations() throws Exception {
		List<Organization> organizations = createOrganizations();
		List<Organization> metalOrganizations = organizations.stream().filter(o -> o.getName().equals("Hellfest") || o.getName().equals("Wacken") || o.getName().equals("Motocultor")).collect(Collectors.toList());

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		criteria.setUserUuid(metalOwner);
		Pageable pageable = Pageable.unpaged();

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("La liste ne doit plus que contenir les 3 organizations metal.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
				.allMatch(bean -> metalOrganizations.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));
	}

	@Test
	@DisplayName("Vérifie le tri par date ASC")
	void testSearchOrganizationsOrderByDateASC() throws Exception {
		List<Organization> organizations = createOrganizations();
		List<Organization> sortedOrganization = organizations.stream().sorted(Comparator.comparing(Organization::getOpeningDate)).collect(Collectors.toList());

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		Pageable pageable = PageRequest.of(0,10, Sort.by("openingDate"));

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("La liste doit toujours contenir le même nombre d'organizations.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
				.allMatch(bean -> organizationBeans.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));
		for(int i = 0; i< organizationBeans.getTotalElements(); i++){
			OrganizationBean bean = organizationBeans.getContent().get(i);
			Organization o = sortedOrganization.get(i);
			assertThat(bean.getUuid())
					.as(String.format("Les listes doivent être dans le même ordre :: %s == %s index=%d",bean.getName(), o.getName(), i))
					.isEqualTo(o.getUuid());
		}
	}

	@Test
	@DisplayName("Vérifie le tri par date DESC")
	void testSearchOrganizationsOrderByDateDESC() throws Exception {
		List<Organization> organizations = createOrganizations();
		List<Organization> sortedOrganization = organizations.stream().sorted(Comparator.comparing(Organization::getOpeningDate)).collect(Collectors.toList());

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		Pageable pageable = PageRequest.of(0,10, Sort.by("openingDate").descending());

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("La liste doit toujours contenir le même nombre d'organizations.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
				.allMatch(bean -> organizationBeans.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));
		for(int i = 0; i< organizationBeans.getTotalElements(); i++){
			OrganizationBean bean = organizationBeans.getContent().get(i);
			Organization o = sortedOrganization.get(sortedOrganization.size()-i-1);
			assertThat(bean.getUuid())
					.as(String.format("Les listes doivent être dans le même ordre :: %s == %s index=%d",bean.getName(), o.getName(), i))
					.isEqualTo(o.getUuid());
		}
	}

	@Test
	@DisplayName("Vérifie le tri par nom d'organization ASC")
	void testSearchOrganizationsOrderByNameASC() throws Exception {
		List<Organization> organizations = createOrganizations();
		List<Organization> sortedOrganization = organizations.stream().sorted(Comparator.comparing(Organization::getName)).collect(Collectors.toList());

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		Pageable pageable = PageRequest.of(0,10, Sort.by("name"));

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("La liste doit toujours contenir le même nombre d'organizations.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
				.allMatch(bean -> organizationBeans.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));
		for(int i = 0; i< organizationBeans.getTotalElements(); i++){
			OrganizationBean bean = organizationBeans.getContent().get(i);
			Organization o = sortedOrganization.get(i);
			assertThat(bean.getUuid())
					.as(String.format("Les listes doivent être dans le même ordre :: %s == %s index=%d",bean.getName(), o.getName(), i))
					.isEqualTo(o.getUuid());
		}
	}

	@Test
	@DisplayName("Vérifie le tri par nom d'organization DESC")
	void testSearchOrganizationsOrderByNameDESC() throws Exception {
		List<Organization> organizations = createOrganizations();
		List<Organization> sortedOrganization = organizations.stream().sorted(Comparator.comparing(Organization::getName)).collect(Collectors.toList());

		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		Pageable pageable = PageRequest.of(0,10, Sort.by("name").descending());

		Page<OrganizationBean> organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);
		assertThat(organizationBeans)
				.as("La liste ne doit pas être nul, mais contenir les organizations créées au préalable.").isNotEmpty()
				.as("La liste doit toujours contenir le même nombre d'organizations.") // parcours la liste des beans et vérifie que pour chaque bean il y a une organization correspondante
				.allMatch(bean -> organizationBeans.stream().anyMatch(organization -> organization.getUuid().equals(bean.getUuid())));
		for(int i = 0; i< organizationBeans.getTotalElements(); i++){
			OrganizationBean bean = organizationBeans.getContent().get(i);
			Organization o = sortedOrganization.get(sortedOrganization.size()-i-1);
			assertThat(bean.getUuid())
					.as(String.format("Les listes doivent être dans le même ordre :: %s == %s index=%d",bean.getName(), o.getName(), i))
					.isEqualTo(o.getUuid());
		}
	}
}
