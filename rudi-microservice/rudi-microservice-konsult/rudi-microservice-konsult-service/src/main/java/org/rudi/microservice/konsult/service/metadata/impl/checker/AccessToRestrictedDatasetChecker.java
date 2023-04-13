package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.konsult.service.helper.APIManagerHelper;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
class AccessToRestrictedDatasetChecker extends AbstractAccessToDatasetChecker {

	private final ProjektHelper projektHelper;
	private final APIManagerHelper apiManagerHelper;

	protected AccessToRestrictedDatasetChecker(UtilContextHelper utilContextHelper,
			ACLHelper aclHelper, MetadataDetailsHelper metadataDetailsHelper, ProjektHelper projektHelper1,
			APIManagerHelper apiManagerHelper) {
		super(utilContextHelper, aclHelper, metadataDetailsHelper);
		this.projektHelper = projektHelper1;
		this.apiManagerHelper = apiManagerHelper;
	}


	/**
	 * Doit être utilisé si notre métadata est un JDD restreint (Règle fonctionnelle: un JDD ne peut pas être selfdata et restreint à la fois)
	 *
	 * @param metadata JDD dont on teste on veut savoir si on doit utiliser le checker ou non
	 * @return true si le checker doit être utilisé sur le JDD
	 */
	@Override
	public boolean accept(Metadata metadata) {
		return metadataDetailsHelper.isRestricted(metadata) && !metadataDetailsHelper.isSelfdata(metadata);
	}

	@Override
	public void checkAuthenticatedUserHasAccessToDataset(UUID globalId, Optional<UUID> requestUuid) throws AppServiceException {
		if (requestUuid.isEmpty()) {
			throw new AppServiceForbiddenException("Cannot subscribe to restricted dataset without previous request to this dataset.");
		}
		val user = this.getAuthenticatedUser();
		final List<User> potentialOwners = new ArrayList<>();
		if (projektHelper.hasAccessToDataset(user.getUuid(), globalId)) {
			potentialOwners.add(user);
		}
		potentialOwners.addAll(apiManagerHelper.collectOrganizationsWithAccess(user.getUuid(), globalId));
		// Si ni l'user ni une de ses organisations n'a de demande validée sur ce JDD
		if (CollectionUtils.isEmpty(potentialOwners)) {
			throw new AppServiceForbiddenException(
					String.format("Cannot subscribe because user %s nor organizations has not been granted access to restricted dataset %s",
							user.getLogin(),
							globalId));
		}
		val projectOwnerUuid = projektHelper.getLinkedDatasetOwner(requestUuid.get());
		if (!potentialOwners.stream().map(User::getUuid).collect(Collectors.toList()).contains(projectOwnerUuid)) {
			throw new AppServiceForbiddenException(
					String.format("Cannot subscribe. Some incoherence exists between user (%s) and request data (%s)",
							user.getLogin(),
							requestUuid.get()));
		}
	}

	@Override
	public UUID getOwnerUuidToUse(UUID requestUuid) {
		return projektHelper.getLinkedDatasetOwner(requestUuid);
	}
}
