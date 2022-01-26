package org.rudi.microservice.acl.core.bean;

import lombok.Data;

/**
 * 
 * @author FNI18300
 *
 */
@Data
public class UserSearchCriteria {

	private String login;

	private String lastname;

	private String firstname;

	private String company;

	private UserType type;

}
