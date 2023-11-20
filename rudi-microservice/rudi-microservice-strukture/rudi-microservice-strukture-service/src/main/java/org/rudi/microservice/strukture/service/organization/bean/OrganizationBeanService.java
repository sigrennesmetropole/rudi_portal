package org.rudi.microservice.strukture.service.organization.bean;

import org.rudi.microservice.strukture.core.bean.OrganizationBean;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationBeanService {

	Page<OrganizationBean> searchOrganizationBeans(OrganizationSearchCriteria criteria, Pageable pageable);
}
