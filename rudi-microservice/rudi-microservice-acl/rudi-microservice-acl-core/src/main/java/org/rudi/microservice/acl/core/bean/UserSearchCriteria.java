package org.rudi.microservice.acl.core.bean;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author FNI18300
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserSearchCriteria {

	private String login;

	private String lastname;

	private String firstname;

	private String company;

	private UserType type;

	private List<UUID> roleUuids;

	private String userEmail;

}
