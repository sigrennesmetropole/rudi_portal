package org.rudi.facet.acl.helper;

import java.util.List;
import java.util.UUID;

import org.rudi.facet.acl.bean.UserType;

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
	public static final String USER_LOGIN_AND_DENOMINATION_PARAMETER = "login-and-denomination";
	public static final String USER_PASSWORD_PARAMETER = "password";
	public static final String ROLE_UUIDS_PARAMETER = "role-uuids";
	public static final String USER_UUIDS_PARAMETER = "user-uuids";
	public static final String USER_TYPE_PARAMETER = "type";
	public static final String USER_LIMIT_PARAMETER = "limit";

	private final String login;
	private final String password;
	private final List<UUID> roleUuids;
	private final List<UUID> userUuids;
	private final String loginAndDenomination;
	private final UserType userType;
}
