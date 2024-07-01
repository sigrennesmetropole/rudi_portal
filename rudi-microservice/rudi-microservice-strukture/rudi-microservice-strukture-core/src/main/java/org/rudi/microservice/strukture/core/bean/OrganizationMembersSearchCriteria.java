package org.rudi.microservice.strukture.core.bean;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrganizationMembersSearchCriteria {

	private UUID organizationUuid;

	private String searchText;

	private OrganizationMemberType type; // ROBOT ou PERSON

	private OrganizationRole role; // ADMINISTRATOR ou EDITOR

	private UUID userUuid;

	private Integer limit;

}
