package org.rudi.microservice.projekt.service.ownerinfo;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;

public interface OwnerInfoService {

	OwnerInfo getOwnerInfo(OwnerType ownerType, UUID ownerUuid) throws AppServiceException;

	boolean hasAccessToDataset(UUID uuidToCheck, UUID datasetUuid);

	UUID getLinkedDatasetOwner(UUID linkedDatasetUuid) throws AppServiceNotFoundException;

}
