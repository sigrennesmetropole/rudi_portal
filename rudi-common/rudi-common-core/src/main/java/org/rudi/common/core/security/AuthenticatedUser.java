/**
 * 
 */
package org.rudi.common.core.security;

import java.util.List;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class AuthenticatedUser {

	private String login;

	private UserType type;

	private String firstname;

	private String lastname;

	private String email;

	private String organization;

	private List<String> roles;

	public AuthenticatedUser() {
		super();
		// Default constructor
	}

	public AuthenticatedUser(String login, UserType type) {
		super();
		this.login = login;
		this.type = type;
	}

	public AuthenticatedUser(String login) {
		this(login, UserType.PERSON);
	}

}
