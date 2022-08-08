package org.rudi.microservice.projekt.service.ownerinfo.impl;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserInfoHelper implements OwnerInfoHelper {
	private final ACLHelper aclHelper;

	@Override
	public boolean isHelperFor(OwnerType ownerType) {
		return ownerType == OwnerType.USER;
	}

	@Override
	public OwnerInfo getOwnerInfo(UUID ownerUuid) throws AppServiceNotFoundException {
		final var userByUUID = aclHelper.getUserByUUID(ownerUuid);
		if (userByUUID == null) {
			throw new AppServiceNotFoundException(User.class, ownerUuid);
		}
		return new OwnerInfo().name(userByUUID.getFirstname() + " " + userByUUID.getLastname());
	}
}
