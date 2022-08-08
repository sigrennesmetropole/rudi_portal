package org.rudi.microservice.projekt.service.project.impl.fields;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class OwnerProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor, DeleteProjectFieldProcessor {
	private final UtilContextHelper utilContextHelper;
	private final ACLHelper aclHelper;
	private final OrganizationHelper organizationHelper;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		final var authenticatedUserUuid = lookupUserUuid(getAuthenticatedUser());

		if (project != null) {
			val ownerUuid = project.getOwnerUuid();
			if (ownerUuid == null) {
				throw new MissingParameterException("owner_uuid manquant");
			}

			validateProject(project, authenticatedUserUuid, ownerUuid,
					"Authenticated user and project manager must be the same user",
					"Authenticated user cannot create project in the name of an organization they do not belong to");
		}

		if (existingProject != null) {
			val ownerUuid = existingProject.getOwnerUuid();

			if (project != null && project.getOwnerType() != existingProject.getOwnerType()) {
				// Pour le moment on ne sait pas ce qu'il faut faire lorsqu'on change le ownerType
				throw new AppServiceForbiddenException("Cannot change project ownerType");
			}

			validateProject(existingProject, authenticatedUserUuid, ownerUuid,
					"Authenticated user and existing project manager must be the same user",
					"Authenticated user cannot update project in the name of an organization they do not belong to");
		}
	}

	private void validateProject(@Nonnull ProjectEntity project, UUID authenticatedUserUuid, UUID ownerUuid, String userErrorMessage, String organizationErrorMessage) throws AppServiceForbiddenException, GetOrganizationMembersException {
		if (project.getOwnerType() == OwnerType.USER) {
			validateOwnerAsManager(ownerUuid, authenticatedUserUuid, userErrorMessage);
		}
		if (project.getOwnerType() == OwnerType.ORGANIZATION) {
			validateOwnerAsOrganization(ownerUuid, authenticatedUserUuid, organizationErrorMessage);
		}
	}

	private void validateOwnerAsManager(UUID managerUuid, UUID authenticatedUserUuid, String errorMessage) throws AppServiceForbiddenException {
		val existingManager = getManager(managerUuid);
		val previousManagerUserUuid = existingManager.getUuid();
		if (!previousManagerUserUuid.equals(authenticatedUserUuid)) {
			throw new AppServiceForbiddenException(errorMessage);
		}
	}

	private void validateOwnerAsOrganization(UUID organizationUuid, UUID authenticatedUserUuid, String errorMessage) throws AppServiceForbiddenException, GetOrganizationMembersException {
		val authenticatedUserIsMember = organizationHelper.organizationContainsUser(organizationUuid, authenticatedUserUuid);
		if (!authenticatedUserIsMember) {
			throw new AppServiceForbiddenException(errorMessage);
		}
	}

	@Nonnull
	private User getManager(UUID managerUserUuid) throws AppServiceForbiddenException {
		final var manager = aclHelper.getUserByUUID(managerUserUuid);
		if (manager == null) {
			throw new AppServiceForbiddenException("Unknown user with UUID " + managerUserUuid);
		}
		return manager;
	}

	@Nonnull
	protected AuthenticatedUser getAuthenticatedUser() throws AppServiceUnauthorizedException {
		val authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("Cannot modify project list without authentication");
		}
		return authenticatedUser;
	}

	private UUID lookupUserUuid(AuthenticatedUser authenticatedUser) {
		val user = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		return user.getUuid();
	}

}
