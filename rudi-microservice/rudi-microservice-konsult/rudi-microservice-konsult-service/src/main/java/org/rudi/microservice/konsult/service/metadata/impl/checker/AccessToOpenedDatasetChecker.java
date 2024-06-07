package org.rudi.microservice.konsult.service.metadata.impl.checker;

import java.util.Optional;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.springframework.stereotype.Component;

@Component
public class AccessToOpenedDatasetChecker extends AbstractAccessToDatasetChecker {
	private final ProjektHelper projektHelper;

	protected AccessToOpenedDatasetChecker(UtilContextHelper utilContextHelper, ACLHelper aclHelper,
			MetadataDetailsHelper metadataDetailsHelper, ProjektHelper projektHelper) {
		super(utilContextHelper, aclHelper, metadataDetailsHelper);
		this.projektHelper = projektHelper;
	}

	@Override
	public boolean accept(Metadata metadata) {
		return !metadataDetailsHelper.isRestricted(metadata) && !metadataDetailsHelper.isSelfdata(metadata);
	}

	@Override
	public void checkAuthenticatedUserHasAccessToDataset(UUID globalId, Optional<UUID> requestUuid)
			throws AppServiceException {
		// Opened dataset, nothing to do as control
	}

	@Override
	public UUID getOwnerUuidToUse(UUID requestUuid) {
		return projektHelper.getLinkedDatasetOwner(requestUuid);
	}
}
