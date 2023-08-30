package org.rudi.microservice.strukture.service.helper.organization.comparator;

import java.util.Comparator;

import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;

public class OrganizationMemberLastConnexionComparator implements Comparator<OrganizationUserMember> {

	@Override
	public int compare(OrganizationUserMember one, OrganizationUserMember two) {
		return one.getLastConnexion().compareTo(two.getLastConnexion());
	}
}
