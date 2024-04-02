/**
 * RUDI Portail
 */
package org.rudi.wso2.userstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.user.api.Properties;
import org.wso2.carbon.user.api.Property;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.common.RoleContext;
import org.wso2.carbon.user.core.constants.UserCoreErrorConstants;
import org.wso2.carbon.user.core.jdbc.JDBCRealmConstants;
import org.wso2.carbon.user.core.jdbc.JDBCRoleContext;
import org.wso2.carbon.user.core.jdbc.JDBCUserStoreConstants;
import org.wso2.carbon.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.carbon.user.core.util.DatabaseUtil;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.Secret;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import static org.wso2.carbon.user.core.util.DatabaseUtil.getLoggableSqlString;

/**
 * @author FNI18300
 *
 */
public class RudiUserStoreManager extends AbstractDefaultUserStoreManager {

	private static final String UNDERSCORE_EXPRESSION = "\\\\_";
	private static final String NULL_CONNECTION = "null connection";
	private static final String THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED = "The cause might be a time out. Hence ignored";

	public RudiUserStoreManager() {
		super();
	}

	public RudiUserStoreManager(DataSource ds, RealmConfiguration realmConfig, int tenantId, boolean addInitData)
			throws UserStoreException {
		super(ds, realmConfig, tenantId, addInitData);
	}

	public RudiUserStoreManager(DataSource ds, RealmConfiguration realmConfig) throws UserStoreException {
		super(ds, realmConfig);
	}

	public RudiUserStoreManager(RealmConfiguration realmConfig, int tenantId) throws UserStoreException {
		super(realmConfig, tenantId);
	}

	public RudiUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties,
			ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId,
			boolean skipInitData) throws UserStoreException {
		super(realmConfig, properties, claimManager, profileManager, realm, tenantId, skipInitData);
	}

	public RudiUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties,
			ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId)
			throws UserStoreException {
		super(realmConfig, properties, claimManager, profileManager, realm, tenantId);
	}

	@Override
	public String[] getProfileNames(String userName) throws UserStoreException {
		LOGGER.warn("getProfileNames " + userName);
		userName = UserCoreUtil.removeDomainFromName(userName);
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PROFILE_NAMES_FOR_USER);
		} else {
			sqlStmt = realmConfig
					.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_PROFILE_NAMES_FOR_USER_CASE_INSENSITIVE);
		}
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving  is null");
		}
		String[] names;
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			names = getStringValuesFromDatabase(sqlStmt, userName, tenantId, tenantId);
		} else {
			names = getStringValuesFromDatabase(sqlStmt, userName);
		}
		if (names.length == 0) {
			names = new String[] { UserCoreConstants.DEFAULT_PROFILE };
		} else {
			Arrays.sort(names);
			if (Arrays.binarySearch(names, UserCoreConstants.DEFAULT_PROFILE) < 0){
				// Créer un nouveau tableau de taille augmentée pour ajouter le profile par défaut
				names = Arrays.copyOf(names, names.length + 1);

				// Ajouter la valeur par défaut à la fin du tableau
				names[names.length - 1] = UserCoreConstants.DEFAULT_PROFILE;
			}
		}
		LOGGER.warn("getProfileNames=" + names);
		return names;
	}

	@Override
	public String[] getAllProfileNames() throws UserStoreException {
		LOGGER.warn("getAllProfileNames");
		String sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PROFILE_NAMES);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving profile names is null");
		}
		String[] names;
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			names = getStringValuesFromDatabase(sqlStmt, tenantId);
		} else {
			names = getStringValuesFromDatabase(sqlStmt);
		}
		LOGGER.warn("getAllProfileNames=" + names);
		return names;
	}

	/**
	 * @param sqlStmt
	 * @param params
	 * @return
	 * @throws UserStoreException
	 */
	private String[] getStringValuesFromDatabase(String sqlStmt, Object... params) throws UserStoreException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query:" + sqlStmt);
			for (int i = 0; i < params.length; i++) {
				Object param = params[i];
				LOGGER.debug("Input value:" + param);
			}
		}

		String[] values = new String[0];
		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			dbConnection = getDBConnection();
			values = DatabaseUtil.getStringValuesFromDatabase(dbConnection, sqlStmt, params);
		} catch (SQLException e) {
			String msg = "Error occurred while retrieving string values.";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return values;
	}

	@Override
	public int getUserId(String username) throws UserStoreException {
		LOGGER.warn("getUserId:" + username);
		username = removeDomainName(username);

		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERID_FROM_USERNAME);
		} else {
			sqlStmt = realmConfig
					.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USERID_FROM_USERNAME_CASE_INSENSITIVE);
		}
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving ID is null");
		}
		int id = -1;
		Connection dbConnection = null;
		try {
			dbConnection = getDBConnection();
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				id = DatabaseUtil.getIntegerValueFromDatabase(dbConnection, sqlStmt, username, tenantId);
			} else {
				id = DatabaseUtil.getIntegerValueFromDatabase(dbConnection, sqlStmt, username);
			}
		} catch (SQLException e) {
			String errorMessage = "Error occurred while getting user id from username : " + username;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection);
		}
		LOGGER.warn("getUserId=" + id);
		return id;
	}

	@Override
	public int getTenantId(String username) throws UserStoreException {
		LOGGER.info("getTenantId:" + username);
		if (this.tenantId != MultitenantConstants.SUPER_TENANT_ID) {
			throw new UserStoreException("Not allowed to perform this operation");
		}
		username = removeDomainName(username);
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_TENANT_ID_FROM_USERNAME);
		} else {
			sqlStmt = realmConfig
					.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_TENANT_ID_FROM_USERNAME_CASE_INSENSITIVE);
		}
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving ID is null");
		}
		int id = -1;
		Connection dbConnection = null;
		try {
			dbConnection = getDBConnection();
			id = DatabaseUtil.getIntegerValueFromDatabase(dbConnection, sqlStmt, username);
		} catch (SQLException e) {
			String errorMessage = "Error occurred while getting tenant ID from username : " + username;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection);
		}
		LOGGER.info("getTenantId=" + id);
		return id;
	}

	@Override
	public int getTenantId() throws UserStoreException {
		LOGGER.info("getTenantId=" + this.tenantId);
		return this.tenantId;
	}

	@Override
	public Map<String, String> getProperties(Tenant tenant) throws UserStoreException {
		LOGGER.info("getProperties:" + tenant.getId());
		return this.realmConfig.getUserStoreProperties();
	}

	@Override
	public Map<String, String> getProperties(org.wso2.carbon.user.api.Tenant tenant)
			throws org.wso2.carbon.user.api.UserStoreException {
		LOGGER.info("getProperties:" + tenant.getId());
		return this.realmConfig.getUserStoreProperties();
	}

	@Override
	public boolean isMultipleProfilesAllowed() {
		return true;
	}

	@Override
	public Properties getDefaultUserStoreProperties() {
		Properties properties = new Properties();
		properties.setMandatoryProperties(JDBCUserStoreConstants.JDBC_UM_MANDATORY_PROPERTIES
				.toArray(new Property[JDBCUserStoreConstants.JDBC_UM_MANDATORY_PROPERTIES.size()]));
		properties.setOptionalProperties(JDBCUserStoreConstants.JDBC_UM_OPTIONAL_PROPERTIES
				.toArray(new Property[JDBCUserStoreConstants.JDBC_UM_OPTIONAL_PROPERTIES.size()]));
		properties.setAdvancedProperties(JDBCUserStoreConstants.JDBC_UM_ADVANCED_PROPERTIES
				.toArray(new Property[JDBCUserStoreConstants.JDBC_UM_ADVANCED_PROPERTIES.size()]));
		return properties;
	}

	@Override
	protected Map<String, String> getUserPropertyValues(String userName, String[] propertyNames, String profileName)
			throws UserStoreException {
		LOGGER.warn("getUserPropertyValues:" + userName + " " + Arrays.toString(propertyNames) + " " + profileName + " "
				+ tenantId);
		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}

		userName = removeDomainName(userName);

		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[] propertyNamesSorted = propertyNames.clone();
		Arrays.sort(propertyNamesSorted);
		Map<String, String> map = new HashMap<>();
		try {
			dbConnection = getDBConnection();
			if (isCaseSensitiveUsername()) {
				sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PROPS_FOR_PROFILE);
			} else {
				sqlStmt = realmConfig
						.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_PROPS_FOR_PROFILE_CASE_INSENSITIVE);
			}
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			prepStmt.setString(1, userName);
			prepStmt.setString(2, profileName);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(3, tenantId);
				prepStmt.setInt(4, tenantId);
			}
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				String value = rs.getString(2);
				if (Arrays.binarySearch(propertyNamesSorted, name) < 0) {
					continue;
				}
				map.put(name, value);
			}
			LOGGER.warn("getUserPropertyValues:" + map);
			return map;
		} catch (SQLException e) {
			String errorMessage = "Error Occurred while getting property values for user : " + userName
					+ " & profile name : " + profileName;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
	}

	@Override
	protected boolean doCheckExistingRole(String roleName) throws UserStoreException {
		LOGGER.warn("doCheckExistingRole:" + roleName);
		RoleContext roleContext = createRoleContext(roleName);
		return isExistingJDBCRole(roleContext);
	}

	protected boolean isExistingJDBCRole(RoleContext context) throws UserStoreException {
		boolean isExisting;
		LOGGER.warn("isExistingJDBCRole:" + context.getRoleName());
		String roleName = context.getRoleName();
		roleName = removeDomainName(roleName);
		String sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_IS_ROLE_EXISTING);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for is role existing role null");
		}

		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			isExisting = isValueExisting(sqlStmt, null, roleName, ((JDBCRoleContext) context).getTenantId());
		} else {
			isExisting = isValueExisting(sqlStmt, null, roleName);
		}
		LOGGER.warn("isExistingJDBCRole=" + isExisting);
		return isExisting;
	}

	/**
	 * @param sqlStmt
	 * @param dbConnection
	 * @param params
	 * @return
	 * @throws UserStoreException
	 */
	protected boolean isValueExisting(String sqlStmt, Connection dbConnection, Object... params)
			throws UserStoreException {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		boolean isExisting = false;
		boolean doClose = false;
		try {
			if (dbConnection == null) {
				dbConnection = getDBConnection();
				doClose = true; // because we created it
			}
			if (DatabaseUtil.getIntegerValueFromDatabase(dbConnection, sqlStmt, params) > -1) {
				isExisting = true;
			}
			return isExisting;
		} catch (SQLException e) {
			String msg = "Error occurred while checking existence of values.";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} finally {
			if (doClose) {
				DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
			}
		}
	}

	@Override
	protected RoleContext createRoleContext(String roleName) throws UserStoreException {
		LOGGER.warn("createRoleContext:" + roleName);
		JDBCRoleContext searchCtx = new JDBCRoleContext();
		String[] roleNameParts;

		roleName = removeDomainName(roleName);

		if (isSharedGroupEnabled()) {
			roleNameParts = roleName.split(UserCoreConstants.TENANT_DOMAIN_COMBINER);
			if (roleNameParts.length > 1 && (roleNameParts[1] == null || roleNameParts[1].equals("null"))) {
				roleNameParts = new String[] { roleNameParts[0] };
			}
		} else {
			roleNameParts = new String[] { roleName };
		}

		int tenantId = -1;
		if (roleNameParts.length > 1) {
			tenantId = Integer.parseInt(roleNameParts[1]);
			searchCtx.setTenantId(tenantId);
		} else {
			tenantId = this.tenantId;
			searchCtx.setTenantId(tenantId);
		}

		if (tenantId != this.tenantId) {
			searchCtx.setShared(true);
		}

		searchCtx.setRoleName(roleNameParts[0]);
		LOGGER.warn("createRoleContext=" + searchCtx.getRoleName());
		return searchCtx;
	}

	@Override
	protected boolean doCheckExistingUser(String userName) throws UserStoreException {
		LOGGER.warn("doCheckExistingUser:" + userName);
		userName = removeDomainName(userName);
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_IS_USER_EXISTING);
		} else {
			sqlStmt = realmConfig
					.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_IS_USER_EXISTING_CASE_INSENSITIVE);
		}
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for is user existing null");
		}
		boolean isExisting = false;

		String isUnique = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_USERNAME_UNIQUE);
		if (Boolean.parseBoolean(isUnique) && !CarbonConstants.REGISTRY_ANONNYMOUS_USERNAME.equals(userName)) {
			String uniquenesSql;
			if (isCaseSensitiveUsername()) {
				uniquenesSql = realmConfig.getUserStoreProperty(JDBCRealmConstants.USER_NAME_UNIQUE);
			} else {
				uniquenesSql = realmConfig
						.getUserStoreProperty(JDBCCaseInsensitiveConstants.USER_NAME_UNIQUE_CASE_INSENSITIVE);
			}
			isExisting = isValueExisting(uniquenesSql, null, userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The username should be unique across tenants.");
			}
		} else {
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				isExisting = isValueExisting(sqlStmt, null, userName, tenantId);
			} else {
				isExisting = isValueExisting(sqlStmt, null, userName);
			}
		}
		LOGGER.warn("doCheckExistingUser=" + isExisting);
		return isExisting;
	}

	@Override
	protected String[] getUserListFromProperties(String property, String value, String profileName)
			throws UserStoreException {
		LOGGER.warn("getUserListFromProperties:" + property + " " + value + " " + profileName);
		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}

		if (value == null) {
			throw new IllegalArgumentException("Filter value cannot be null");
		}

		String sqlStmt = getUserListFromPropertiesGetStatement(value);

		String[] users = new String[0];
		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		// This is to support LDAP like queries. Value having only * is restricted except one *.
		// Convert all the * to % except \*.
		if (value.contains(QUERY_FILTER_STRING_ANY) && !value.matches("(\\*)\\1+")) {
			value = value.replaceAll("(?<!\\\\)\\*", SQL_FILTER_STRING_ANY);
		}

		if (value.contains(UNDERSCORE)) {
			value = value.replace(UNDERSCORE, UNDERSCORE_EXPRESSION);
		}

		List<String> list = new ArrayList<>();
		try {
			dbConnection = getDBConnection();

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			getUserListFromPropertiesPrepareStatement(prepStmt, sqlStmt, property, value, profileName);

			int givenMax;
			int searchTime;
			int maxItemLimit = MAX_ITEM_LIMIT_UNLIMITED;
			givenMax = getGivenMaxUser();
			searchTime = getSearchTime();
			if (maxItemLimit < 0 || maxItemLimit > givenMax) {
				maxItemLimit = givenMax;
			}
			prepStmt.setMaxRows(maxItemLimit);
			setQueryTimeout(prepStmt, searchTime);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				list.add(name);
			}

			if (!list.isEmpty()) {
				users = list.toArray(new String[list.size()]);
			}

		} catch (SQLException e) {
			String msg = "Database error occurred while listing users for a property : " + property + " & value : "
					+ value + " & profile name : " + profileName;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		LOGGER.warn("getUserListFromProperties=" + Arrays.toString(users));
		return users;
	}

	private void getUserListFromPropertiesPrepareStatement(PreparedStatement prepStmt, String sqlStmt, String property,
			String value, String profileName) throws SQLException {
		int count = 0;
		prepStmt.setString(++count, property);
		prepStmt.setString(++count, value);
		if (sqlStmt.toUpperCase().contains(UserCoreConstants.SQL_ESCAPE_KEYWORD)) {
			prepStmt.setString(++count, SQL_FILTER_CHAR_ESCAPE);
		}
		prepStmt.setString(++count, profileName);
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			prepStmt.setInt(++count, tenantId);
			prepStmt.setInt(++count, tenantId);
		}
	}

	private String getUserListFromPropertiesGetStatement(String value) {
		String sqlStmt = null;
		if (value.contains(UNDERSCORE)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_FOR_PROP_WITH_ESCAPE);
		} else if (value.contains(QUERY_FILTER_STRING_ANY)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_FOR_PROP);
		} else {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_FOR_CLAIM_VALUE);
		}
		return sqlStmt;
	}

	@Override
	protected boolean doAuthenticate(String userName, Object credential) throws UserStoreException {
		LOGGER.warn("doAuthenticate:" + userName);
		if (!isValidUserName(userName)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Username validation failed");
			}
			return false;
		}

		if (!isValidCredentials(credential)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Password validation failed");
			}
			return false;
		}

		if (UserCoreUtil.isRegistryAnnonymousUser(userName)) {
			LOGGER.error("Anonnymous user trying to login");
			return false;
		}

		Connection dbConnection = null;
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		boolean isAuthed = false;

		try {
			dbConnection = getDBConnection();
			dbConnection.setAutoCommit(false);

			final String sqlstmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.SELECT_USER,
					JDBCCaseInsensitiveConstants.SELECT_USER_CASE_INSENSITIVE);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("doAuthenticate SQL:" + sqlstmt + " " + userName + " from " + tenantId);
			}

			userName = removeDomainName(userName);

			prepStmt = dbConnection.prepareStatement(sqlstmt);
			prepStmt.setString(1, userName);
			if (sqlstmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(2, tenantId);
			}

			rs = prepStmt.executeQuery();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("doAuthenticate: executed");
			}

			if (rs.next()) {
				isAuthed = doAuthenticateHandleResult(rs, userName, credential);
			}
		} catch (SQLException e) {
			String msg = "Error occurred while retrieving user authentication info for user : " + userName;
			LOGGER.debug(msg, e);
			throw new UserStoreException("Authentication Failure", e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("User " + userName + " login attempt. Login success :: " + isAuthed);
		}
		LOGGER.warn("doAuthenticate=" + isAuthed);
		return isAuthed;
	}

	private boolean doAuthenticateHandleResult(ResultSet rs, String username, Object credential) throws SQLException {
		boolean isAuthed = false;
		LOGGER.warn("doAuthenticate: result (" + rs.getLong(1) + ")");
		username = removeDomainName(username);

		boolean requireChange = rs.getBoolean(5);
		Timestamp changedTime = rs.getTimestamp(6);

		GregorianCalendar gc = new GregorianCalendar();
		gc.add(Calendar.HOUR, -24);
		Date date = gc.getTime();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doAuthenticate: " + requireChange + " " + changedTime);
		}

		if (requireChange && changedTime.before(date)) {
			LOGGER.debug("doAuthenticate: requireChange");
		} else if (username.equalsIgnoreCase("anonymous")) {
			LOGGER.debug("doAuthenticate: check anonymous ok");
			isAuthed = true;
		} else {
			LOGGER.debug("doAuthenticate: check password");
			PostMethod request = null;
			try {
				Secret credentialObj = Secret.getSecret(credential);
				String passwordString = new String(credentialObj.getChars());

				request = new PostMethod(authenticatorURL);
				request.addRequestHeader(new Header("content-type", "application/x-www-form-urlencoded"));
				request.addRequestHeader(new Header("accept", "application/json"));
				request.addParameter("login", username);
				request.addParameter("password", passwordString);
				int code = getHttpClient().executeMethod(request);

				LOGGER.debug("doAuthenticate call result:" + code);
				if (code == 200) {
					LOGGER.debug("doAuthenticate body:" + request.getResponseBodyAsString());
					isAuthed = true;
				}
			} catch (Exception e) {
				LOGGER.error("doAuthenticate: call failed", e);
			} finally {
				if (request != null) {
					request.releaseConnection();
				}
			}
		}
		LOGGER.warn("doAuthenticate=" + isAuthed);
		return isAuthed;
	}

	private String getSqlStatement(boolean caseSensitiveUsername, String getUserFilterPaginatedCount,
			String getUserFilterCaseInsensitivePaginatedCount) {
		String sqlStmt;
		if (caseSensitiveUsername) {
			sqlStmt = realmConfig.getUserStoreProperty(getUserFilterPaginatedCount);
		} else {
			sqlStmt = realmConfig.getUserStoreProperty(getUserFilterCaseInsensitivePaginatedCount);
		}
		return sqlStmt;
	}

	@Override
	protected String[] doGetExternalRoleListOfUser(String userName, String filter) throws UserStoreException {
		LOGGER.warn("doGetExternalRoleListOfUser:" + userName + " " + filter);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting roles of user: " + userName + " with filter: " + filter);
		}

		String[] names = getNameOfUsers(userName, filter);

		List<String> roles = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			if (names != null) {
				for (String name : names) {
					LOGGER.debug("Found role: " + name);
				}
			} else {
				LOGGER.debug("No external role found for the user: " + userName);
			}
		}

		Collections.addAll(roles, names);
		LOGGER.warn("doGetExternalRoleListOfUser=" + roles);
		return roles.toArray(new String[roles.size()]);
	}

	/**
	 * Get the SQL statement for ExternalRoles.
	 *
	 * @param caseSensitiveUsernameQuery    query for getting role with case sensitive username.
	 * @param nonCaseSensitiveUsernameQuery query for getting role with non-case sensitive username.
	 * @return sql statement.
	 * @throws UserStoreException
	 */
	private String getExternalRoleListSqlStatement(String caseSensitiveUsernameQuery,
			String nonCaseSensitiveUsernameQuery) throws UserStoreException {
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = caseSensitiveUsernameQuery;
		} else {
			sqlStmt = nonCaseSensitiveUsernameQuery;
		}
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving user roles is null");
		}
		return sqlStmt;
	}

	@Override
	protected String[] doGetSharedRoleListOfUser(String userName, String tenantDomain, String filter)
			throws UserStoreException {
		LOGGER.warn("doGetRoleNames:" + userName + " " + tenantDomain + " " + filter);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Looking for shared roles for user: " + userName + " for tenant: " + tenantDomain);
		}

		userName = removeDomainName(userName);

		String[] sharedNames = new String[0];
		if (isSharedGroupEnabled()) {
			// shared roles
			String sqlStmt;
			if (isCaseSensitiveUsername()) {
				sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_SHARED_ROLES_FOR_USER);
			} else {
				sqlStmt = realmConfig
						.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_SHARED_ROLES_FOR_USER_CASE_INSENSITIVE);
			}
			sharedNames = getRoleNamesWithDomain(sqlStmt, userName, tenantId, true);
		}
		return sharedNames;
	}

	private String[] getRoleNamesWithDomain(String sqlStmt, String userName, int tenantId, boolean appendDn)
			throws UserStoreException {

		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		List<String> roles = new ArrayList<>();
		try {
			dbConnection = getDBConnection();
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			byte count = 0;
			prepStmt.setString(++count, userName);
			prepStmt.setInt(++count, tenantId);

			rs = prepStmt.executeQuery();
			// append the domain if exist
			String domain = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
			LOGGER.debug("getRoleNamesWithDomain Domain:" + domain);

			while (rs.next()) {
				String name = rs.getString(1);
				int tenant = rs.getInt(2);

				String role = name;
				if (appendDn) {
					name = UserCoreUtil.addTenantDomainToEntry(name, String.valueOf(tenant));
				}
				roles.add(role);
			}

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving role name with tenant id : " + tenantId + " & user : "
					+ userName;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return roles.toArray(new String[roles.size()]);
	}

	@Override
	protected String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException {
		LOGGER.warn("doGetRoleNames:" + filter + " " + maxItemLimit);

		String[] roles = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		filter = removeDomainName(filter);

		int givenMax = getGivenMaxRole();
		int searchTime = getSearchTime();

		if (maxItemLimit == 0) {
			return roles;
		}

		if (maxItemLimit < 0 || maxItemLimit > givenMax) {
			maxItemLimit = givenMax;
		}

		try {

			filter = normalizeFilter(filter);

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION);
			}

			sqlStmt = doGetRoleNamesGetStatement(dbConnection, filter);

			if (filter.contains(UNDERSCORE)) {
				filter = filter.replace(UNDERSCORE, UNDERSCORE_EXPRESSION);
			}

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			byte count = 0;
			prepStmt.setString(++count, filter);
			if (sqlStmt.toUpperCase().contains(UserCoreConstants.SQL_ESCAPE_KEYWORD)) {
				prepStmt.setString(++count, SQL_FILTER_CHAR_ESCAPE);
			}
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(++count, tenantId);
			}

			prepStmt.setMaxRows(maxItemLimit);
			setQueryTimeout(prepStmt, searchTime);
			rs = doGetRoleNamesExecute(prepStmt, filter, maxItemLimit);

			roles = doGetRoleNamesGetResult(rs);

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving role names for filter : " + filter + " & max item limit : "
					+ maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} catch (Exception e) {
			throw new UserStoreException("Error while retrieving the DB type. ", e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		LOGGER.warn("doGetRoleNames=" + roles);
		return roles;
	}

	private String[] doGetRoleNamesGetResult(ResultSet rs) throws SQLException {
		String[] roles = new String[0];
		List<String> lst = new LinkedList<>();
		// Expected columns UM_ROLE_NAME, UM_TENANT_ID, UM_SHARED_ROLE
		if (rs != null) {
			while (rs.next()) {
				String name = rs.getString(1);
				// append the domain if exist
				String domain = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
				name = UserCoreUtil.addDomainToName(name, domain);
				lst.add(name);
			}
		}

		if (!lst.isEmpty()) {
			roles = lst.toArray(new String[lst.size()]);
		}
		return roles;
	}

	private ResultSet doGetRoleNamesExecute(PreparedStatement prepStmt, String filter, int maxItemLimit)
			throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED, e);
		} catch (SQLException e) {
			String errorMessage = "Error while fetching roles from JDBC user store according to filter : " + filter
					+ " & max item limit : " + maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		}
		return rs;
	}

	private String doGetRoleNamesGetStatement(Connection dbConnection, String filter) throws Exception {
		String sqlStmt;
		if (filter.contains(UNDERSCORE)) {
			sqlStmt = isH2DB(dbConnection)
					? realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST_WITH_ESCAPE_H2)
					: realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST_WITH_ESCAPE);
		} else {
			sqlStmt = isH2DB(dbConnection) ? realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST_H2)
					: realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST);
		}
		return sqlStmt;
	}

	@Override
	protected String[] doListUsers(String filter, int maxItemLimit) throws UserStoreException {
		LOGGER.warn("doListUsers:" + filter + " " + maxItemLimit);
		String[] users = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		filter = removeDomainName(filter);

		if (maxItemLimit == 0) {
			return new String[0];
		}

		int givenMax = getGivenMaxUser();
		int searchTime = getSearchTime();

		if (maxItemLimit < 0 || maxItemLimit > givenMax) {
			maxItemLimit = givenMax;
		}

		String displayNameAttribute = realmConfig.getUserStoreProperty(JDBCUserStoreConstants.DISPLAY_NAME_ATTRIBUTE);

		try {

			if (filter != null && filter.trim().length() != 0) {
				filter = filter.trim();
				filter = filter.replace(STAR, PERCENT);
			} else {
				filter = PERCENT;
			}

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION);
			}

			sqlStmt = doListUsersGetStatement(filter);

			if (filter.contains(UNDERSCORE)) {
				filter = filter.replace(UNDERSCORE, UNDERSCORE_EXPRESSION);
			}
			filter = filter.replace(EXCLAIM, UNDERSCORE);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			doListUsersPrepareStatement(prepStmt, sqlStmt, filter);
			prepStmt.setMaxRows(maxItemLimit);
			setQueryTimeout(prepStmt, searchTime);

			rs = doListUsersExecute(prepStmt, filter, maxItemLimit);

			users = doListUsersGetResult(rs, displayNameAttribute);

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving users for filter : " + filter + " & max Item limit : "
					+ maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		LOGGER.warn("doListUsers=" + Arrays.toString(users));
		return users;
	}

	private void doListUsersPrepareStatement(PreparedStatement prepStmt, String sqlStmt, String filter)
			throws SQLException {
		prepStmt.setString(1, filter);
		if (sqlStmt.toUpperCase().contains(UserCoreConstants.SQL_ESCAPE_KEYWORD)) {
			prepStmt.setString(2, SQL_FILTER_CHAR_ESCAPE);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(3, tenantId);
			}
		} else {
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(2, tenantId);
			}
		}
	}

	private String doListUsersGetStatement(String filter) {
		String sqlStmt;
		if (filter.contains(UNDERSCORE)) {
			sqlStmt = getUserFilterQuery(JDBCRealmConstants.GET_USER_FILTER_WITH_ESCAPE,
					JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE_WITH_ESCAPE);
		} else {
			sqlStmt = getUserFilterQuery(JDBCRealmConstants.GET_USER_FILTER,
					JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE);
		}
		return sqlStmt;
	}

	private String[] doListUsersGetResult(ResultSet rs, String displayNameAttribute)
			throws SQLException, UserStoreException {
		String[] users = new String[0];
		List<String> lst = new LinkedList<>();

		while (rs.next()) {
			String displayName = null;
			String name = rs.getString(1);
			if (CarbonConstants.REGISTRY_ANONNYMOUS_USERNAME.equals(name)) {
				continue;
			}
			// append the domain if exist
			String domain = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);

			if (StringUtils.isNotEmpty(displayNameAttribute)) {
				String[] propertyNames = { displayNameAttribute };

				// There is no capability to select profile in UI, So select the Default profile.
				Map<String, String> profileDetails = getUserPropertyValues(name, propertyNames,
						UserCoreConstants.DEFAULT_PROFILE);
				displayName = profileDetails.get(displayNameAttribute);

				// If user created without the display name attribute applied.
				if (StringUtils.isNotEmpty(displayName)) {
					name = UserCoreUtil.getCombinedName(domain, name, displayName);
				} else {
					name = UserCoreUtil.addDomainToName(name, domain);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(displayNameAttribute + " : " + displayName);
				}
			} else {
				name = UserCoreUtil.addDomainToName(name, domain);
			}
			lst.add(name);
		}
		rs.close();

		if (!lst.isEmpty()) {
			users = lst.toArray(new String[lst.size()]);
		}

		Arrays.sort(users);
		return users;
	}

	private ResultSet doListUsersExecute(PreparedStatement prepStmt, String filter, int maxItemLimit)
			throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED, e);
		} catch (SQLException e) {
			String errorMessage = "Error while fetching users according to filter : " + filter + " & max Item limit "
					+ ": " + maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		}
		return rs;
	}

	private String getUserFilterQuery(String caseSensitiveQueryPropertyName, String caseInsensitiveQueryPropertyName) {
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(caseSensitiveQueryPropertyName);
		} else {
			sqlStmt = realmConfig.getUserStoreProperty(caseInsensitiveQueryPropertyName);
		}
		return sqlStmt;
	}

	@Override
	protected String[] doGetDisplayNamesForInternalRole(String[] userNames) throws UserStoreException {
		return userNames;
	}

	@Override
	public boolean doCheckIsUserInRole(String userName, String roleName) throws UserStoreException {
		LOGGER.warn("doCheckIsUserInRole:" + userName + " " + roleName);
		boolean result = false;
		String[] roles = doGetExternalRoleListOfUser(userName, roleName);
		if (roles != null) {
			for (String role : roles) {
				if (role.equalsIgnoreCase(roleName)) {
					result = true;
				}
			}
		}
		LOGGER.warn("doCheckIsUserInRole=" + result);
		return result;
	}

	@Override
	protected String[] doGetSharedRoleNames(String tenantDomain, String filter, int maxItemLimit)
			throws UserStoreException {
		LOGGER.warn("doGetSharedRoleNames:" + tenantDomain + " " + filter + " " + maxItemLimit);

		filter = removeDomainName(filter);

		String[] roles = new String[0];
		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		if (maxItemLimit == 0) {
			return roles;
		}

		try {

			if (!isSharedGroupEnabled()) {
				return roles;
			}

			if (filter != null && filter.trim().length() != 0) {
				filter = filter.trim();
				filter = filter.replace(STAR, PERCENT);
				filter = filter.replace(EXCLAIM, UNDERSCORE);
			} else {
				filter = PERCENT;
			}

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION);
			}

			String sqlStmt = isH2DB(dbConnection)
					? realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_SHARED_ROLE_LIST_H2)
					: realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_SHARED_ROLE_LIST);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			byte count = 0;
			prepStmt.setString(++count, filter);

			int givenMax = getGivenMaxRole();

			if (maxItemLimit < 0 || maxItemLimit > givenMax) {
				maxItemLimit = givenMax;
			}

			int searchTime = getSearchTime();
			prepStmt.setMaxRows(maxItemLimit);

			setQueryTimeout(prepStmt, searchTime);

			rs = doGetSharedRoleNamesExecute(prepStmt, tenantDomain, filter, maxItemLimit);

			// Expected columns UM_ROLE_NAME, UM_TENANT_ID, UM_SHARED_ROLE
			if (rs != null) {
				roles = doGetSharedRoleNamesGetResult(rs);
			}

		} catch (SQLException e) {
			String errorMessage = "Error while retrieving roles from JDBC user store for tenant domain : "
					+ tenantDomain + " & filter : " + filter + "& max item limit : " + maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} catch (Exception e) {
			throw new UserStoreException("Error while retrieving the DB type for tenant domain: " + tenantDomain, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}

		LOGGER.warn("doGetSharedRoleNames=" + roles);
		return roles;
	}

	private String[] doGetSharedRoleNamesGetResult(ResultSet rs) throws SQLException {
		List<String> lst = new ArrayList<>();
		while (rs.next()) {
			String name = rs.getString(1);
			int roleTenantId = rs.getInt(2);
			// append the domain if exist
			String domain = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
			name = UserCoreUtil.addDomainToName(name, domain);
			name = UserCoreUtil.addTenantDomainToEntry(name, String.valueOf(roleTenantId));
			lst.add(name);
		}

		if (!lst.isEmpty()) {
			return lst.toArray(new String[lst.size()]);
		}
		return new String[0];
	}

	private ResultSet doGetSharedRoleNamesExecute(PreparedStatement prepStmt, String tenantDomain, String filter,
			int maxItemLimit) throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED, e);
		} catch (SQLException e) {
			String errorMessage = "Error while fetching roles from JDBC user store for tenant domain : " + tenantDomain
					+ " & filter : " + filter + "& max item limit : " + maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		}
		return rs;
	}

	@Override
	protected String[] doGetUserListOfRole(String roleName, String filter) throws UserStoreException {
		LOGGER.warn("doGetUserListOfRole:" + roleName + " " + filter);
		roleName = removeDomainName(roleName);
		filter = removeDomainName(filter);
		RoleContext roleContext = createRoleContext(roleName);
		return getUserListOfJDBCRole(roleContext, filter);
	}

	private String[] getUserListOfJDBCRole(RoleContext ctx, String filter) throws UserStoreException {
		LOGGER.warn("getUserListOfJDBCRole:" + ctx.getRoleName() + " " + filter);
		return getUserListOfJDBCRole(ctx, filter, QUERY_MAX_ITEM_LIMIT_ANY);
	}

	private String[] getUserListOfJDBCRole(RoleContext ctx, String filter, int maxItemLimit) throws UserStoreException {
		LOGGER.warn("getUserListOfJDBCRole:" + ctx.getRoleName() + " " + filter + " " + maxItemLimit);
		String roleName = ctx.getRoleName();
		String[] names = null;
		String sqlStmt = null;

		if (maxItemLimit == 0) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}

		if (maxItemLimit < 0 || maxItemLimit > maximumUserNameListLength) {
			maxItemLimit = maximumUserNameListLength;
		}

		if (StringUtils.isNotEmpty(filter)) {
			filter = filter.trim();
			filter = filter.replace("*", "%");
			filter = filter.replace("?", "_");
		} else {
			filter = "%";
		}

		if (!ctx.isShared()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_IN_ROLE_FILTER);
			if (sqlStmt == null) {
				throw new UserStoreException("The sql statement for retrieving user roles is null");
			}

			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				names = getStringValuesFromDatabaseWithConstraints(sqlStmt, maxItemLimit, queryTimeout, filter,
						roleName, tenantId, tenantId, tenantId);
			} else {
				names = getStringValuesFromDatabaseWithConstraints(sqlStmt, maxItemLimit, queryTimeout, filter,
						roleName);
			}
		} else if (ctx.isShared()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_IN_SHARED_ROLE_FILTER);
			names = getStringValuesFromDatabaseWithConstraints(sqlStmt, maxItemLimit, queryTimeout, filter, roleName);
		}

		List<String> userList = new ArrayList<>();

		String domainName = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);

		if (names != null) {
			for (String user : names) {
				user = UserCoreUtil.addDomainToName(user, domainName);
				userList.add(user);
			}

			names = userList.toArray(new String[userList.size()]);
		}
		LOGGER.debug("Roles are not defined for the role name " + roleName);
		LOGGER.warn("getUserListOfJDBCRole=" + names);
		return names;
	}

	/**
	 * Get {@link String}[] of values from the database for the given SQL query and the constraints.
	 *
	 * @param sqlStmt      {@link String} SQL query.
	 * @param maxRows      Upper limit to the number of rows returned from the database.
	 * @param queryTimeout SQL query timeout limit in seconds. Zero means there is no limit.
	 * @param params       Values passed for the SQL query placeholders.
	 * @return {@link String}[] of results.
	 * @throws UserStoreException
	 */
	private String[] getStringValuesFromDatabaseWithConstraints(String sqlStmt, int maxRows, int queryTimeout,
			Object... params) throws UserStoreException {
		if (LOGGER.isDebugEnabled()) {
			String loggableSqlString = getLoggableSqlString(sqlStmt, params);
			String msg = "Using SQL : " + loggableSqlString + ", and maxRows: " + maxRows + ", and queryTimeout: "
					+ queryTimeout;
			LOGGER.debug(msg);
		}

		String[] values;
		try (Connection dbConnection = getDBConnection()) {
			values = DatabaseUtil.getStringValuesFromDatabaseWithConstraints(dbConnection, sqlStmt, maxRows,
					queryTimeout, params);
		} catch (SQLException e) {
			String msg = "Error occurred while accessing the database connection.";
			throw new UserStoreException(msg, e);
		}
		return values;
	}

	/**
	 * Count roles in user stores.
	 *
	 * @param filter the filter for the user name. Use '*' to have all.
	 * @return user count
	 * @throws UserStoreException UserStoreException
	 */
	@Override
	public long doCountRoles(String filter) throws UserStoreException {

		long usersCount = 0;
		String sqlStmt;
		if (filter.startsWith(UserCoreConstants.INTERNAL_DOMAIN)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_INTERNAL_ROLES);
			String[] names = filter.split(UserCoreConstants.DOMAIN_SEPARATOR);
			filter = names[1].trim();
		} else if (filter.startsWith(UserCoreConstants.APPLICATION_DOMAIN)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_APPLICATION_ROLES);
		} else {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_ROLES);
		}

		filter = normalizeFilter(filter);

		try (Connection dbConnection = getDBConnection();
				PreparedStatement prepStmt = dbConnection.prepareStatement(sqlStmt)) {

			prepStmt.setString(1, filter);
			if (sqlStmt.toUpperCase().contains(UserCoreConstants.SQL_ESCAPE_KEYWORD)) {
				prepStmt.setString(2, SQL_FILTER_CHAR_ESCAPE);
				if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
					prepStmt.setInt(3, tenantId);
				}
			} else {
				if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
					prepStmt.setInt(2, tenantId);
				}
			}

			ResultSet resultSets = prepStmt.executeQuery();
			while (resultSets.next()) {
				return resultSets.getLong(1);
			}

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving roles for filter : " + filter;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, e);
			}
			throw new UserStoreException(msg, e);

		} catch (Exception ex) {
			handleGetUserCountFailure(
					UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_ROLES_COUNT.getCode(),
					String.format(UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_ROLES_COUNT
							.getMessage(), ex.getMessage()),
					null, null);
			throw new UserStoreException("Error occurred while retrieving roles for filter : " + filter, ex);
		}
		return usersCount;
	}

	private String normalizeFilter(String filter) {
		if (StringUtils.isNotEmpty(filter)) {
			filter = filter.trim();
			filter = filter.replace(STAR, PERCENT);
			filter = filter.replace(EXCLAIM, UNDERSCORE);
		} else {
			filter = PERCENT;
		}
		return filter;
	}

	/**
	 * Count users with claim.
	 *
	 * @param claimUri claim uri.
	 * @param value    The filter for the user name. Use '*' to have all.
	 * @return Count of the users.
	 * @throws UserStoreException UserStoreException
	 */
	@Override
	public long doCountUsersWithClaims(String claimUri, String value) throws UserStoreException {

		if (claimUri == null) {
			throw new IllegalArgumentException("Error while getting the claim uri");
		}

		String valueFilter = value;
		if (valueFilter == null) {
			throw new IllegalArgumentException("Error while getting the claim filter");
		}

		String sqlStmt;
		if (isUserNameClaim(claimUri)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_USERS);

		} else {
			sqlStmt = JDBCRealmConstants.COUNT_USERS_WITH_CLAIM_SQL;
		}

		valueFilter = normalizeFilter(valueFilter);

		try (Connection dbConnection = getDBConnection();
				PreparedStatement prepStmt = dbConnection.prepareStatement(sqlStmt)) {
			String domainName = getMyDomainName();
			if (StringUtils.isEmpty(domainName)) {
				domainName = UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME;
			}

			if (isUserNameClaim(claimUri)) {
				prepStmt.setString(1, valueFilter);
				prepStmt.setInt(2, tenantId);
			} else {
				prepStmt.setString(1, userRealm.getClaimManager().getAttributeName(domainName, claimUri));
				prepStmt.setInt(2, tenantId);
				prepStmt.setString(3, valueFilter);
				prepStmt.setString(4, UserCoreConstants.DEFAULT_PROFILE);
			}

			ResultSet resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong("RESULT");
			} else {
				LOGGER.warn("No result for the filter" + value);
				return 0;
			}

		} catch (SQLException e) {
			String msg = "Error while executing the SQL " + sqlStmt;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg + sqlStmt);
			}
			throw new UserStoreException(msg, e);
		} catch (UserStoreException ex) {
			handleGetUserCountFailure(
					UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_COUNT_USERS.getCode(),
					String.format(UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_COUNT_USERS
							.getMessage(), ex.getMessage()),
					claimUri, value);
			throw ex;
		} catch (org.wso2.carbon.user.api.UserStoreException e) {
			throw new UserStoreException("Error while getting attribute name from " + claimUri, e);
		}
	}

	private String[] getNameOfUsers(String userName, String filter) throws UserStoreException {
		String sqlStmt;
		String[] names;
		if (filter.equals(STAR) || StringUtils.isEmpty(filter)) {

			sqlStmt = getExternalRoleListSqlStatement(
					realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USER_ROLE),
					realmConfig.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USER_ROLE_CASE_INSENSITIVE));
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				names = getStringValuesFromDatabase(sqlStmt, userName, tenantId, tenantId, tenantId);
			} else {
				names = getStringValuesFromDatabase(sqlStmt, userName);
			}
		} else {
			filter = filter.trim();
			filter = filter.replace(STAR, PERCENT);
			filter = filter.replace(EXCLAIM, UNDERSCORE);
			sqlStmt = getExternalRoleListSqlStatement(
					realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_IS_USER_ROLE_EXIST),
					realmConfig.getUserStoreProperty(
							JDBCCaseInsensitiveConstants.GET_IS_USER_ROLE_EXIST_CASE_INSENSITIVE));

			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				names = getStringValuesFromDatabase(sqlStmt, userName, tenantId, tenantId, tenantId, filter);
			} else {
				names = getStringValuesFromDatabase(sqlStmt, userName, filter);
			}
		}
		return names;
	}

}

