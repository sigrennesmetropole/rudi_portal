package org.rudi.microservice.konsent.service.consent.impl;

import java.util.List;

import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.service.consent.MyConsentsService;
import org.rudi.microservice.konsent.service.mapper.consent.ConsentsMapper;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentCustomDao;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author KOU21310
 */
@Service
@RequiredArgsConstructor
public class MyConsentsServiceImpl implements MyConsentsService {
	private final ACLHelper aclHelper;
	private final ConsentCustomDao consentCustomDao;
	private final ConsentsMapper consentsMapper;

	@Override
	public PagedConsentList searchMyConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) throws Exception {
		val userUuid = aclHelper.getAuthenticatedUserUuid();
		if (userUuid == null) {
			throw new AppServiceUnauthorizedException("Aucun utilisateur connecté");
		}
		searchCriteria.setUserUuids(List.of(userUuid));
		val myConsentsPage = consentCustomDao.searchMyConsents(searchCriteria, pageable);
		return new PagedConsentList().total(myConsentsPage.getTotalElements())
				.elements(consentsMapper.entitiesToDto(myConsentsPage.getContent()));
	}
}
