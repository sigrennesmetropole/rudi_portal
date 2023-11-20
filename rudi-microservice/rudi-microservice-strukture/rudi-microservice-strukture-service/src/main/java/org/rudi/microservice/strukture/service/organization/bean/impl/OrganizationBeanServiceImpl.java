package org.rudi.microservice.strukture.service.organization.bean.impl;

import org.rudi.microservice.strukture.core.bean.OrganizationBean;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.mapper.OrganizationBeanMapper;
import org.rudi.microservice.strukture.service.organization.bean.OrganizationBeanService;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationCustomDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrganizationBeanServiceImpl implements OrganizationBeanService {


	private final OrganizationCustomDao organizationCustomDao;
	private final OrganizationBeanMapper organizationBeanMapper;

	@Override
	public Page<OrganizationBean> searchOrganizationBeans(OrganizationSearchCriteria criteria, Pageable pageable) {
		return organizationBeanMapper.entitiesToDto(organizationCustomDao.searchOrganizations(criteria, pageable), pageable);
	}


}
