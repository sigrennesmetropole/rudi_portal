package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.selfdata.helper.SelfdataHelper;
import org.springframework.stereotype.Component;

@Component
class AccessToSelfdataDatasetChecker extends AbstractAccessToDatasetChecker {

	private final SelfdataHelper selfdataHelper;

	protected AccessToSelfdataDatasetChecker(UtilContextHelper utilContextHelper, ACLHelper aclHelper, MetadataDetailsHelper metadataDetailsHelper, SelfdataHelper selfdataHelper1) {
		super(utilContextHelper, aclHelper, metadataDetailsHelper);
		this.selfdataHelper = selfdataHelper1;
	}


	/**
	 * Doit être utilisé si notre métadata est un selfdata (Règle fonctionnelle: un JDD ne peut pas être selfdata et restreint à la fois)
	 *
	 * @param metadata JDD dont on teste on veut savoir si on doit utiliser le checker ou non
	 * @return
	 */
	@Override
	public boolean hasToBeUse(Metadata metadata) {
		return metadataDetailsHelper.isSelfdata(metadata);
	}

	@Override
	public void checkAuthenticatedUserHasAccessToDataset(UUID datasetUuid) throws AppServiceForbiddenException, AppServiceUnauthorizedException {
		final var user = this.getAuthenticatedUser(datasetUuid);
		final var userHasAccessToDataset = selfdataHelper.hasMatchingToDataset(user.getUuid(), datasetUuid);
		if (!userHasAccessToDataset) {
			throw new AppServiceForbiddenException(
					String.format("Cannot subscribe because user has not been granted access to selfdata dataset %s",
							datasetUuid));
		}
	}
}
