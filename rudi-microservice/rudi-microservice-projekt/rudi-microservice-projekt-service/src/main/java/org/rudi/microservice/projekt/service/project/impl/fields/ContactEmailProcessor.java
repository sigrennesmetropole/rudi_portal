package org.rudi.microservice.projekt.service.project.impl.fields;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
@RequiredArgsConstructor
class ContactEmailProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final ACLHelper aclHelper;
	private final UtilContextHelper utilContextHelper;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project != null && project.getContactEmail() == null) {
			val authenticatedUser = utilContextHelper.getAuthenticatedUser();
			if (authenticatedUser != null) {
				project.setContactEmail(getContactEmailFromUserLogin(authenticatedUser.getLogin()));
			}
		}
	}

	private String getContactEmailFromUserLogin(String login) {
		if (login != null) {
			final var user = aclHelper.getUserByLogin(login);
			if (user != null) {
				return aclHelper.lookupEMailAddress(user);
			}
		}
		return null;
	}

}
