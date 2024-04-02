package org.rudi.microservice.konsent.service.consent.impl;

import java.util.List;
import java.util.UUID;

import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.service.consent.MyConsentsService;
import org.rudi.microservice.konsent.service.mapper.consent.ConsentsMapper;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentCustomDao;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
		UUID userUuid = aclHelper.getAuthenticatedUserUuid(); // ne peut pas être null

		searchCriteria.setUserUuids(List.of(userUuid));
		Page<ConsentEntity> myConsentsPage = consentCustomDao.searchMyConsents(searchCriteria, pageable);
		return new PagedConsentList().total(myConsentsPage.getTotalElements())
				.elements(consentsMapper.entitiesToDto(myConsentsPage.getContent()));
	}
}
