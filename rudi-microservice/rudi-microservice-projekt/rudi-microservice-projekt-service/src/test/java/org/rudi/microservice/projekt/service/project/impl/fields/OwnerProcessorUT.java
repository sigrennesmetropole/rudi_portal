package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.Role;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.RolesHelper;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerProcessorUT {
	@InjectMocks
	private OwnerProcessor ownerProcessor;
	@Mock
	private UtilContextHelper utilContextHelper;
	@Mock
	private ACLHelper aclHelper;
	@Mock
	private OrganizationHelper organizationHelper;
	@Mock
	private RolesHelper rolesHelper;

	@Test
	void process_user_create_himself() {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final ProjectEntity projectToCreate = new ProjectEntity();
		projectToCreate.setOwnerType(OwnerType.USER);
		projectToCreate.setOwnerUuid(user.getUuid());

		assertThatCode(() -> ownerProcessor.process(projectToCreate, null))
				.as("Le porteur de projet peut créer un projet en son nom")
				.doesNotThrowAnyException();
	}

	@Test
	void process_user_create_someoneElse() {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);


		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);


		final User otherUser = new User()
				.uuid(UUID.fromString("343ed690-8092-4d29-b371-1bc24ad79747"))
				.login("john.connor");
		when(aclHelper.getUserByUUID(otherUser.getUuid())).thenReturn(otherUser);

		// On est pas moderateur dans ce test
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(false);

		final ProjectEntity projectToCreate = new ProjectEntity();
		projectToCreate.setOwnerType(OwnerType.USER);
		projectToCreate.setOwnerUuid(otherUser.getUuid());

		assertThatThrownBy(() -> ownerProcessor.process(projectToCreate, null))
				.as("Le porteur de projet ne peut pas créer un projet au nom de quelqu'un d'autre")
				.isInstanceOf(AppServiceForbiddenException.class)
				.hasMessage("Authenticated user must be moderator or must be the same user as existing project manager");
	}

	@Test
	void process_user_update_himself() {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.USER);
		existingProject.setOwnerUuid(user.getUuid());

		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.USER);
		modifiedProject.setOwnerUuid(user.getUuid());

		assertThatCode(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("Le porteur de projet peut créer un projet en son nom")
				.doesNotThrowAnyException();
	}

	@Test
	void process_user_update_someoneElsesProject_without_moderator_role() {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);


		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final User otherUser = new User()
				.uuid(UUID.fromString("343ed690-8092-4d29-b371-1bc24ad79747"))
				.login("john.connor");
		when(aclHelper.getUserByUUID(otherUser.getUuid())).thenReturn(otherUser);

		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.USER);
		existingProject.setOwnerUuid(otherUser.getUuid());

		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.USER);
		modifiedProject.setOwnerUuid(user.getUuid());

		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(false);

		assertThatThrownBy(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("Le porteur de projet ne peut pas modifier le projet de quelqu'un d'autre sans être modératur")
				.isInstanceOf(AppServiceForbiddenException.class)
				.hasMessage("Authenticated user must be moderator or must be the same user as existing project manager");
	}

	@Test
	void process_user_update_someoneElsesProject_with_moderator_role() {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);


		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final User otherUser = new User()
				.uuid(UUID.fromString("343ed690-8092-4d29-b371-1bc24ad79747"))
				.login("john.connor");
		when(aclHelper.getUserByUUID(otherUser.getUuid())).thenReturn(otherUser);

		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.USER);
		existingProject.setOwnerUuid(otherUser.getUuid());

		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.USER);
		modifiedProject.setOwnerUuid(user.getUuid());
		// On agit en tant que MODERATOR
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(true);

		assertThatCode(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("Le peut modifier un projet d'un porteur de projet")
				.doesNotThrowAnyException();
	}

	// TODO changement de owner_type

	@Test
	void process_organization_create_member() throws GetOrganizationMembersException {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final UUID organizationUuid = UUID.fromString("1f76051c-465e-440f-8e3f-68dd847ef05e");
		when(organizationHelper.organizationContainsUser(organizationUuid, user.getUuid())).thenReturn(true);

		final ProjectEntity projectToCreate = new ProjectEntity();
		projectToCreate.setOwnerType(OwnerType.ORGANIZATION);
		projectToCreate.setOwnerUuid(organizationUuid);

		assertThatCode(() -> ownerProcessor.process(projectToCreate, null))
				.as("Le porteur de projet peut créer un projet au nom d'une organisation à laquelle il appartient")
				.doesNotThrowAnyException();
	}

	@Test
	void process_organization_create_notMember() throws GetOrganizationMembersException {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final UUID organizationUuid = UUID.fromString("1f76051c-465e-440f-8e3f-68dd847ef05e");
		when(organizationHelper.organizationContainsUser(organizationUuid, user.getUuid())).thenReturn(false);

		final ProjectEntity projectToCreate = new ProjectEntity();
		projectToCreate.setOwnerType(OwnerType.ORGANIZATION);
		projectToCreate.setOwnerUuid(organizationUuid);

		assertThatThrownBy(() -> ownerProcessor.process(projectToCreate, null))
				.as("Le porteur de projet peut créer un projet au nom d'une organisation à laquelle il appartient")
				.isInstanceOf(AppServiceForbiddenException.class)
				.hasMessage("Authenticated user cannot create project in the name of an organization they do not belong to, unless he is moderator");
	}

	@Test
	void process_organization_update_member() throws GetOrganizationMembersException {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final UUID previousOrganizationUuid = UUID.fromString("1f76051c-465e-440f-8e3f-68dd847ef05e");
		when(organizationHelper.organizationContainsUser(previousOrganizationUuid, user.getUuid())).thenReturn(true);
		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.ORGANIZATION);
		existingProject.setOwnerUuid(previousOrganizationUuid);

		final UUID nextOrganizationUuid = UUID.fromString("5d5f92b4-64f4-4ae9-bf1e-0da97b0f3e7c");
		when(organizationHelper.organizationContainsUser(nextOrganizationUuid, user.getUuid())).thenReturn(true);
		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.ORGANIZATION);
		modifiedProject.setOwnerUuid(nextOrganizationUuid);

		assertThatCode(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("Le porteur de projet peut modifier un projet au nom des organisations auxquelles il appartient")
				.doesNotThrowAnyException();
	}

	@Test
	void process_organization_update_notMember_notModerator() throws GetOrganizationMembersException {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(false);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final UUID previousOrganizationUuid = UUID.fromString("1f76051c-465e-440f-8e3f-68dd847ef05e");
		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.ORGANIZATION);
		existingProject.setOwnerUuid(previousOrganizationUuid);

		final UUID nextOrganizationUuid = UUID.fromString("5d5f92b4-64f4-4ae9-bf1e-0da97b0f3e7c");
		when(organizationHelper.organizationContainsUser(nextOrganizationUuid, user.getUuid())).thenReturn(true);
		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.ORGANIZATION);
		modifiedProject.setOwnerUuid(nextOrganizationUuid);

		assertThatThrownBy(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("Le porteur de projet ne peut pas modifier un projet au nom d'une organisation à laquelle il n'appartient pas")
				.isInstanceOf(AppServiceForbiddenException.class)
				.hasMessage("Authenticated user cannot update project in the name of an organization they do not belong to, unless he is moderator");
	}

	@Test
	void process_organization_update_notMember_buModerator() throws GetOrganizationMembersException {

		final User user = new User()
				.uuid(UUID.fromString("6a3b3a2d-7996-438e-a00e-0aec1cf6433b"))
				.login("sarah.connor");
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(true);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		final UUID previousOrganizationUuid = UUID.fromString("1f76051c-465e-440f-8e3f-68dd847ef05e");
		final ProjectEntity existingProject = new ProjectEntity();
		existingProject.setOwnerType(OwnerType.ORGANIZATION);
		existingProject.setOwnerUuid(previousOrganizationUuid);

		final UUID nextOrganizationUuid = UUID.fromString("5d5f92b4-64f4-4ae9-bf1e-0da97b0f3e7c");
		when(organizationHelper.organizationContainsUser(nextOrganizationUuid, user.getUuid())).thenReturn(true);
		final ProjectEntity modifiedProject = new ProjectEntity();
		modifiedProject.setOwnerType(OwnerType.ORGANIZATION);
		modifiedProject.setOwnerUuid(nextOrganizationUuid);

		assertThatCode(() -> ownerProcessor.process(modifiedProject, existingProject))
				.as("L'utilisateur connecté avec le rôle MODERATOR peut modifier un projet appartenant à une organisation")
				.doesNotThrowAnyException();
	}
}
