package org.rudi.microservice.strukture.service.helper.organization;

public enum OrganizationMemberSort {

	LOGIN("login"),
	FIRSTNAME("firstname"),
	LASTNAME("lastname"),
	ROLE("role"),
	LAST_CONNEXION("last_connexion"),
	ADDED_DATE("added_date");

	private final String value;

	OrganizationMemberSort(String value) {
		this.value = value;
	}

	static OrganizationMemberSort from(String value) {
		for (OrganizationMemberSort b : OrganizationMemberSort.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}
