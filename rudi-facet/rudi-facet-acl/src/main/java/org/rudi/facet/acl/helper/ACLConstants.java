/**
 * RUDI Portail
 */
package org.rudi.facet.acl.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ACLConstants {

	public static final String USER_LOGIN_PARAMETER = "login";
	public static final String USER_LOGIN_AND_DENOMINATION_PARAMETER = "login-and-denomination";
	public static final String USER_PASSWORD_PARAMETER = "password";
	public static final String USER_TYPE_PARAMETER = "type";
	public static final String USER_UUIDS_PARAMETER = "user-uuids";

	public static final String ROLE_UUIDS_PARAMETER = "role-uuids";

	public static final String PROJECT_KEY_STORE_UUID_PARAMETER = "project-key-store-uuid";
	public static final String PROJECT_KEY_UUID_PARAMETER = "project-key-uuid";

	public static final String LIMIT_PARAMETER = "limit";
	public static final String OFFSET_PARAMETER = "offset";

}
