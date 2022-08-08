package org.rudi.microservice.projekt.service.ownerinfo;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;

import java.util.UUID;

public interface OwnerInfoService {

	OwnerInfo getOwnerInfo(OwnerType ownerType, UUID ownerUuid) throws AppServiceException;
}
