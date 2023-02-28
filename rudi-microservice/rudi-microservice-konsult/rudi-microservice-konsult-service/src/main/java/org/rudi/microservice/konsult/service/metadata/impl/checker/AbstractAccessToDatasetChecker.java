package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAccessToDatasetChecker {

	protected final UtilContextHelper utilContextHelper;
	protected final ACLHelper aclHelper;
	protected final MetadataDetailsHelper metadataDetailsHelper;


	/**
	 * Permet de dire si un checker donné doit être utilisé ou pas
	 *
	 * @param metadata JDD dont on teste on veut savoir si on doit utiliser le checker ou non
	 * @return
	 */
	public abstract boolean hasToBeUse(Metadata metadata);

	/**
	 * Verifie si l'utilisateur connecté peut souscrire à ce JDD (une demande acceptée pour un JDD restreint ou une demande validée et passée en TraitéPrésent pour un selfdata)
	 *
	 * @param globalId
	 * @throws AppServiceUnauthorizedException
	 * @throws AppServiceForbiddenException
	 */
	public abstract void checkAuthenticatedUserHasAccessToDataset(UUID globalId) throws AppServiceUnauthorizedException, AppServiceForbiddenException;

	protected User getAuthenticatedUser(UUID globalId) throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		val authenticatedUser = aclHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException(
					String.format("User anonymous not authorized", globalId));
		}

		final var user = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		if (user == null) {
			throw new AppServiceForbiddenException(
					String.format("No user found for login: %s", authenticatedUser.getLogin()));
		}
		return user;
	}
}
