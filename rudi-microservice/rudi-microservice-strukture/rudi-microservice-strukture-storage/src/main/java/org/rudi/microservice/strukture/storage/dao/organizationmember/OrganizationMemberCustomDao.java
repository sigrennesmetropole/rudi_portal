package org.rudi.microservice.strukture.storage.dao.organizationmember;

import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationMemberCustomDao {
	Page<OrganizationMemberEntity> searchOrganizationMembers(OrganizationMembersSearchCriteria searchCriteria, Pageable pageable);
}
