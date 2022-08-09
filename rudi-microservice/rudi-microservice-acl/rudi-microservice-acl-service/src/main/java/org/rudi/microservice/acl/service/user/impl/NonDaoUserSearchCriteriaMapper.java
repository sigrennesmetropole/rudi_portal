package org.rudi.microservice.acl.service.user.impl;

import org.rudi.common.service.util.SearchCriteriaMapper;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.service.helper.PasswordHelper;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Critères de recherche ne pouvant pas être appliqués côté DAO
 */
@Component
class NonDaoUserSearchCriteriaMapper extends SearchCriteriaMapper<UserSearchCriteria, UserEntity> {

	NonDaoUserSearchCriteriaMapper(PasswordHelper passwordHelper) {
		// On ne peut pas appliquer ce critère dans la DAO (cf javadoc UserSearchCriteria.password)
		mapStringCriterion(UserSearchCriteria::getPassword, passwordHelper::buildUserHasPasswordPredicate);
	}

}
