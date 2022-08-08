package org.rudi.microservice.projekt.service.ownerinfo.impl;

import org.rudi.microservice.projekt.core.bean.OwnerType;

class OwnerInfoHelperNotImplementedException extends RuntimeException {
	public OwnerInfoHelperNotImplementedException(OwnerType ownerType) {
		super(String.format("OwnerInfoHelper for ownerType %s not implemented", ownerType));
	}
}
