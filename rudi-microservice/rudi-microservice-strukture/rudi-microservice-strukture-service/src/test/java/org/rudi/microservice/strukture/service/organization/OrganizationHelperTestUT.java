package org.rudi.microservice.strukture.service.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationHelper;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@StruktureSpringBootTest
public class OrganizationHelperTestUT {

	@Autowired
	OrganizationDao organizationDao;

	@Autowired
	OrganizationHelper organizationHelper;

	@MockBean
	OrganizationMembersHelper organizationMembersHelper;

	@MockBean
	DatasetService datasetService;

	@AfterEach
	public void cleanData() {
		organizationDao.deleteAll();
	}

	@Test
	void searchAdministrators_retrieves_corresponding_user() throws AppServiceException {

		// Création d'une organisation avec 1 admin et 1 pas admin
		OrganizationEntity organization = createOrganization();

		UUID member1Uuid = UUID.randomUUID();
		UUID member2Uuid = UUID.randomUUID();

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setUserUuid(member1Uuid);
		member1.setRole(OrganizationRole.ADMINISTRATOR);
		member1.setAddedDate(LocalDateTime.now());

		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setUserUuid(member2Uuid);
		member2.setRole(OrganizationRole.EDITOR);
		member2.setAddedDate(LocalDateTime.now());

		organization.setMembers(Set.of(member1, member2));
		organizationDao.save(organization);

		// Quand on va demander au Helper de chercher les users correspondants, il fonctionne
		User administratorUser = new User().uuid(member1Uuid);
		when(organizationMembersHelper.searchCorrespondingUsers(any(), any())).thenReturn(List.of(administratorUser));

		// Recherche des utilisateurs administrateurs de l'organisation
		List<User> users = organizationHelper.searchUserAdministrators(organization.getUuid());

		// On récupère que le user administrateur 1
		assertThat(CollectionUtils.isNotEmpty(users)).isTrue();
		Long numberOfMember1 = users.stream().filter(user -> member1Uuid.equals(user.getUuid())).count();
		Long numberOfMember2 = users.stream().filter(user -> member2Uuid.equals(user.getUuid())).count();
		assertThat(numberOfMember1).isEqualTo(1);
		assertThat(numberOfMember2).isEqualTo(0);
	}

	@Test
	void searchAdministratos_works_on_high_volumetry() throws AppServiceException {

		// Création d'une organisation avec 100 administrateurs et 50 éditeurs
		OrganizationEntity organization = createOrganization();
		organization.setMembers(new HashSet<>());

		for (int i = 0; i < 100; i++) {
			OrganizationMemberEntity member = new OrganizationMemberEntity();
			member.setUserUuid(UUID.randomUUID());
			member.setRole(OrganizationRole.ADMINISTRATOR);
			member.setAddedDate(LocalDateTime.now());

			organization.getMembers().add(member);
		}

		for (int i = 0; i < 50; i++) {
			OrganizationMemberEntity member = new OrganizationMemberEntity();
			member.setUserUuid(UUID.randomUUID());
			member.setRole(OrganizationRole.EDITOR);
			member.setAddedDate(LocalDateTime.now());

			organization.getMembers().add(member);
		}

		organizationDao.save(organization);

		// Quand on appelle la recherche des users correspondant, ACL répond autant d'users que demandé
		when(organizationMembersHelper.searchCorrespondingUsers(any(), any()))
				.thenAnswer((Answer<List<User>>) invocation -> {
					List<OrganizationMemberEntity> membersInvoked = invocation.getArgument(0);
					List<User> usersRetrieved = new ArrayList<>();
					membersInvoked.forEach(memberInvoked -> usersRetrieved.add(new User()));
					return usersRetrieved;
				});

		// Recherche des utilisateurs administrateurs de l'organisation
		List<User> users = organizationHelper.searchUserAdministrators(organization.getUuid());

		// On vérifie qu'on récupère que les 100 administrateurs
		assertThat(CollectionUtils.isNotEmpty(users)).isTrue();
		assertThat(users.size()).isEqualTo(100);
	}

	@Test
	void searchAdministrators_fails_on_uknown_organization() {

		// Création d'une organisation
		createOrganization();

		// Recherche des utilisateurs administrateurs de l'organisation
		assertThatExceptionOfType(AppServiceNotFoundException.class)
				.as("La recherche des administrateurs d'une organisation inconnue renvoie 404")
				.isThrownBy(() -> organizationHelper.searchUserAdministrators(UUID.randomUUID()));
	}

	private OrganizationEntity createOrganization() {
		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setName("Liksi");
		organization.setDescription("petite ESN");
		organization.setUrl("http://liksi.com");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);

		LocalDateTime date2 = LocalDateTime.of(2025, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setClosingDate(date2);

		return organizationDao.save(organization);
	}
}
