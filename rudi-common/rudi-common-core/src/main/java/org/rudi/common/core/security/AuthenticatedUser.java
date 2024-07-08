/**
 * 
 */
package org.rudi.common.core.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<String, String> data;

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

	public String addData(String key, String value) {
		if (data == null) {
			data = new HashMap<>();
		}
		return data.put(key, value);
	}

	public boolean hasData(String key) {
		return (data != null) ? data.containsKey(key) : false;
	}

	public String getData(String key) {
		return (data != null) ? data.get(key) : null;
	}

	public String removeData(String key) {
		return (data != null) ? data.remove(key) : null;
	}

}
