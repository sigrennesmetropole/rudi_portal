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

	private final String login;
	private final String password;
	private final List<UUID> roleUuids;
	private final List<UUID> userUuids;
	private final String loginAndDenomination;
	private final UserType userType;
}
