package org.rudi.microservice.projekt.service.ownerinfo.impl;

import org.rudi.microservice.projekt.core.bean.OwnerType;

class OwnerInfoHelperNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -4730853013641463901L;

	public OwnerInfoHelperNotImplementedException(OwnerType ownerType) {
		super(String.format("OwnerInfoHelper for ownerType %s not implemented", ownerType));
	}
}
