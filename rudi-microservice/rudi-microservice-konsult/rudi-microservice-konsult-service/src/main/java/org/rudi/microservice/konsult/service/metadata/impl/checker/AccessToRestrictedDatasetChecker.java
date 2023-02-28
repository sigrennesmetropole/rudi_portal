package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.springframework.stereotype.Component;

@Component
class AccessToRestrictedDatasetChecker extends AbstractAccessToDatasetChecker {

	private final ProjektHelper projektHelper;

	protected AccessToRestrictedDatasetChecker(UtilContextHelper utilContextHelper, ACLHelper aclHelper, MetadataDetailsHelper metadataDetailsHelper, ProjektHelper projektHelper1) {
		super(utilContextHelper, aclHelper, metadataDetailsHelper);
		this.projektHelper = projektHelper1;
	}


	/**
	 * Doit être utilisé si notre métadata est un JDD restreint (Règle fonctionnelle: un JDD ne peut pas être selfdata et restreint à la fois)
	 *
	 * @param metadata JDD dont on teste on veut savoir si on doit utiliser le checker ou non
	 * @return
	 */
	@Override
	public boolean hasToBeUse(Metadata metadata) {
		return metadataDetailsHelper.isRestricted(metadata) && !metadataDetailsHelper.isSelfdata(metadata);
	}

	@Override
	public void checkAuthenticatedUserHasAccessToDataset(UUID globalId) throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		final var user = this.getAuthenticatedUser(globalId);
		final var ownerHasAccessToDataset = projektHelper.checkOwnerHasAccessToDataset(user.getUuid(), globalId);
		if (!ownerHasAccessToDataset) {
			throw new AppServiceForbiddenException(
					String.format("Cannot subscribe because user %s has not been granted access to restricted dataset %s",
							user.getLogin(),
							globalId));
		}
	}
}
