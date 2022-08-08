package org.rudi.microservice.projekt.service.ownerinfo.impl;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;

import java.util.UUID;

interface OwnerInfoHelper {
	boolean isHelperFor(OwnerType ownerType);

	OwnerInfo getOwnerInfo(UUID ownerUuid) throws AppServiceException;
}
