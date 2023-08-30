package org.rudi.microservice.strukture.service.helper.organization.comparator;

import java.util.Comparator;

import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;

public class OrganizationMemberRoleComparator implements Comparator<OrganizationUserMember> {

	@Override
	public int compare(OrganizationUserMember one, OrganizationUserMember two) {
		return one.getRole().compareTo(two.getRole());
	}
}
