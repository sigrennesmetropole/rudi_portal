package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.Optional;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
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
	 * @return true or false
	 */
	public abstract boolean accept(Metadata metadata);

	/**
	 * Verifie si l'utilisateur connecté peut souscrire à ce JDD (une demande acceptée pour un JDD restreint ou une demande validée et passée en TraitéPrésent pour un selfdata)
	 *
	 * @param globalId    uuid du JDD
	 * @param requestUuid uuid de la requête permettant l'accès à ce JDD (surtout pour les restreints)
	 * @throws AppServiceUnauthorizedException
	 * @throws AppServiceForbiddenException
	 */
	public abstract void checkAuthenticatedUserHasAccessToDataset(UUID globalId, Optional<UUID> requestUuid) throws AppServiceException;

	protected User getAuthenticatedUser() throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		val authenticatedUser = aclHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("User anonymous not authorized");
		}

		final var user = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		if (user == null) {
			throw new AppServiceForbiddenException(
					String.format("No user found for login: %s", authenticatedUser.getLogin()));
		}
		return user;
	}

	/**
	 * @param requestUuid uuid de la demande qui nous donne l'autorisation de souscrire à ce JDD
	 * @return uuid à utiliser pour faire la souscription (utile si on agit au nom d'une de nos organisations)
	 * @throws AppServiceForbiddenException
	 * @throws AppServiceUnauthorizedException
	 */
	public abstract UUID getOwnerUuidToUse(UUID requestUuid) throws AppServiceForbiddenException, AppServiceUnauthorizedException;
}
