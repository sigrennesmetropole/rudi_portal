package org.rudi.microservice.acl.service.account.impl;

import org.rudi.microservice.acl.storage.dao.accountupdate.ResetPasswordRequestDao;
import org.rudi.microservice.acl.storage.entity.accountupdate.ResetPasswordRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
class UpdatePasswordTokenHelper extends AbstractTokenHelper<ResetPasswordRequestEntity, ResetPasswordRequestDao> {

	/**
	 * Validity duration for an Update Password request (in minutes)
	 */
	@Value("${rudi.acl.resetPasswordRequest.validity:60}")
	@Getter
	private int tokenValidity;

	UpdatePasswordTokenHelper(ResetPasswordRequestDao dao) {
		super(dao);
	}

	@Override
	Class<ResetPasswordRequestEntity> getEntityClass() {
		return ResetPasswordRequestEntity.class;
	}

}
