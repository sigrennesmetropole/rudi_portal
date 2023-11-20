package org.rudi.microservice.strukture.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.core.bean.PagedOrganizationBeanList;
import org.rudi.microservice.strukture.facade.controller.api.OrganizationBeansApi;
import org.rudi.microservice.strukture.service.organization.bean.OrganizationBeanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequiredArgsConstructor
public class OrganizationBeanController implements OrganizationBeansApi {

	private final OrganizationBeanService organizationBeanService;
	private final UtilPageable utilPageable;

	@Override
	public ResponseEntity<PagedOrganizationBeanList> searchOrganizationsBeans(UUID userUuid, Integer offset, Integer limit, String order) throws Exception {

		val pageable = utilPageable.getPageable(offset, limit, order);
		val criteria = new OrganizationSearchCriteria();
		criteria.setUserUuid(userUuid);
		val organizationBeans = organizationBeanService.searchOrganizationBeans(criteria, pageable);

		val pagedOrganizationBeans = new PagedOrganizationBeanList();
		pagedOrganizationBeans.setElements(organizationBeans.getContent());
		pagedOrganizationBeans.setTotal(organizationBeans.getTotalElements());

		return ResponseEntity.ok(pagedOrganizationBeans);
	}
}
