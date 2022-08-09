package org.rudi.facet.acl.helper;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Builder
public class UserSearchCriteria {

	public static final String USER_LOGIN_PARAMETER = "login";
	public static final String USER_PASSWORD_PARAMETER = "password";
	public static final String ROLE_UUIDS_PARAMETER = "role-uuids";

	private final String login;
	private final String password;
	private final List<UUID> roleUuids;

}
