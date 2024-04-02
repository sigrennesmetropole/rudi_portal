package org.rudi.wso2.userstore;

import static org.wso2.carbon.user.core.util.DatabaseUtil.getLoggableSqlString;

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
import java.util.Random;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rudi.wso2.userstore.internal.RudiSSLProtocolSockerFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.user.api.Properties;
import org.wso2.carbon.user.api.Property;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.PaginatedSearchResult;
import org.wso2.carbon.user.core.common.RoleContext;
import org.wso2.carbon.user.core.constants.UserCoreErrorConstants;
import org.wso2.carbon.user.core.dto.RoleDTO;
import org.wso2.carbon.user.core.jdbc.JDBCRealmConstants;
import org.wso2.carbon.user.core.jdbc.JDBCRoleContext;
import org.wso2.carbon.user.core.jdbc.JDBCUserStoreConstants;
import org.wso2.carbon.user.core.jdbc.caseinsensitive.JDBCCaseInsensitiveConstants;
import org.wso2.carbon.user.core.model.Condition;
import org.wso2.carbon.user.core.model.ExpressionAttribute;
import org.wso2.carbon.user.core.model.ExpressionCondition;
import org.wso2.carbon.user.core.model.ExpressionOperation;
import org.wso2.carbon.user.core.model.OperationalCondition;
import org.wso2.carbon.user.core.model.SqlBuilder;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.carbon.user.core.util.DatabaseUtil;
import org.wso2.carbon.user.core.util.JDBCRealmUtil;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.Secret;
import org.wso2.carbon.utils.dbcreator.DatabaseCreator;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * @author FNI18300
 *
 */
public class RudiUserStoreManagerOld extends AbstractUserStoreManager {

	private static final String UNDERSCORE = "_";

	private static final String EXCLAIM = "?";

	private static final String STAR = "*";

	private static final String PERCENT = "%";

	private static final String RUDI_DOMAIN_NAME_SLASH = "RUDI/";

	private static final String R_UM_ROLE_NAME_QUERY = "R.UM_ROLE_NAME = ?";

	private static final String R_UM_ROLE_NAME_LIKE_QUERY = "R.UM_ROLE_NAME LIKE ?";

	private static final String UA_UM_ATTR_NAME_QUERY = "UA.UM_ATTR_NAME = ?";

	private static final String UA_UM_ATTR_VALUE_QUERY = "UA.UM_ATTR_VALUE = ?";

	private static final String UA_UM_ATTR_VALUE_LIKE_QUERY = "UA.UM_ATTR_VALUE LIKE ?";

	private static final String RUDI_STORE_IS_READONLY_MESSAGE = "Rudi Store is readonly";

	private static final String PROFILE_NAME_MESSAGE = " & profile name : ";

	private static final String ERROR_OCCURRED_WHILE_RETRIEVING_USERS_FOR_FILTER_MESSAGE = "Error occurred while retrieving users for filter : ";

	private static final String THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED_MESSAGE = "The cause might be a time out. Hence ignored";

	private static final String NULL_CONNECTION_MESSAGE = "null connection";

	private static final Log LOGGER = LogFactory.getLog(RudiUserStoreManagerOld.class);

	private static final String QUERY_FILTER_STRING_ANY = STAR;
	private static final String SQL_FILTER_STRING_ANY = PERCENT;
	private static final String SQL_FILTER_CHAR_ESCAPE = "\\";
	private static final String QUERY_BINDING_SYMBOL = EXCLAIM;
	private static final String CASE_INSENSITIVE_USERNAME = "CaseInsensitiveUsername";

	private static final String DB2 = "db2";
	private static final String MSSQL = "mssql";
	private static final String ORACLE = "oracle";
	private static final String MYSQL = "mysql";

	private static final int MAX_ITEM_LIMIT_UNLIMITED = -1;
	private static final String DO_GET_PASSWORD_EXPIRATION_TIME = "doGetPasswordExpirationTime:";
	public static final String FILTER_VALUE_CANNOT_BE_NULL = "Filter value cannot be null";
	public static final String QUERY_FILTER_STRING_ANY_MATCH_REGEX = "(\\*)\\1+";
	private static final String QUERY_FILTER_STRING_ANY_REPLACE_REGEX = "(?<!\\\\)\\*";

	protected DataSource jdbcds = null;

	protected Random random = new Random();

	protected int maximumUserNameListLength = -1;

	protected int queryTimeout = -1;

	protected String authenticatorURL = null;

	protected HttpClient httpClient = null;

	/**
	 * 
	 */
	public RudiUserStoreManagerOld() {

	}

	/**
	 */
	public RudiUserStoreManagerOld(RealmConfiguration realmConfig, int tenantId) {
		this.realmConfig = realmConfig;
		this.tenantId = tenantId;

		LOGGER.warn("realConfig:" + realmConfig + " tenandId: " + tenantId);

		realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));

		LOGGER.info("Authorized:" + realmConfig.getAuthzProperties());
		LOGGER.info("Realm:" + realmConfig.getRealmProperties());
		LOGGER.info("Props:" + realmConfig.getUserStoreProperties());
		// new properties after carbon core 4.0.7 release.
		if (realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED) != null) {
			readGroupsEnabled = Boolean
					.parseBoolean(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED));
		}
		writeGroupsEnabled = false;

		authenticatorURL = realmConfig.getUserStoreProperty("PasswordJavaRegExViolationErrorMsg");

		maximumUserNameListLength = getMaxUserNameListLength();
		queryTimeout = getSQLQueryTimeoutLimit();

		if (LOGGER.isDebugEnabled()) {
			if (writeGroupsEnabled) {
				LOGGER.debug("WriteGroups is enabled for " + getMyDomainName());
			} else {
				LOGGER.debug("WriteGroups is disabled for " + getMyDomainName());
			}
			if (readGroupsEnabled) {
				LOGGER.debug("ReadGroups is enabled for " + getMyDomainName());
			} else {
				LOGGER.debug("ReadGroups is disabled for " + getMyDomainName());
			}
			LOGGER.debug("Rudi Authenticate url:" + authenticatorURL);
		}

		/*
		 * Initialize user roles cache as implemented in AbstractUserStoreManager
		 */
		initUserRolesCache();
	}

	/**
	 * This constructor is used by the support IS.
	 *
	 */
	public RudiUserStoreManagerOld(DataSource ds, RealmConfiguration realmConfig, int tenantId, boolean addInitData)
			throws UserStoreException {

		this(realmConfig, tenantId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Started " + System.currentTimeMillis());
		}
		realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));
		this.jdbcds = ds;
		this.dataSource = ds;

		if (dataSource == null) {
			dataSource = DatabaseUtil.getRealmDataSource(realmConfig);
		}
		if (dataSource == null) {
			throw new UserStoreException("User Management Data Source is null");
		}
		doInitialSetup();
		this.persistDomain();
		if (addInitData && realmConfig.isPrimary()) {
			addInitialAdminData(Boolean.parseBoolean(realmConfig.getAddAdmin()), !isInitSetupDone());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Ended " + System.currentTimeMillis());
		}
	}

	/**
	 * This constructor to accommodate PasswordUpdater called from chpasswd script.
	 *
	 */
	public RudiUserStoreManagerOld(DataSource ds, RealmConfiguration realmConfig) {

		this(realmConfig, MultitenantConstants.SUPER_TENANT_ID);
		realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));
		this.jdbcds = ds;
	}

	/**
	 */
	public RudiUserStoreManagerOld(RealmConfiguration realmConfig, Map<String, Object> properties,
			ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId)
			throws UserStoreException {
		this(realmConfig, properties, claimManager, profileManager, realm, tenantId, false);
	}

	/**
	 * 
	 * @param realmConfig
	 * @param properties
	 * @param claimManager
	 * @param profileManager le gestionnaire de profile
	 * @param realm
	 * @param tenantId
	 * @param skipInitData
	 * @throws UserStoreException
	 */
	public RudiUserStoreManagerOld(RealmConfiguration realmConfig, Map<String, Object> properties,
			ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId,
			boolean skipInitData) throws UserStoreException {
		this(realmConfig, tenantId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Started " + System.currentTimeMillis());
		}
		this.claimManager = claimManager;
		this.userRealm = realm;

		try {
			jdbcds = loadUserStoreSpacificDataSoruce();

			if (jdbcds == null) {
				jdbcds = (DataSource) properties.get(UserCoreConstants.DATA_SOURCE);
			}
			if (jdbcds == null) {
				jdbcds = DatabaseUtil.getRealmDataSource(realmConfig);
				properties.put(UserCoreConstants.DATA_SOURCE, jdbcds);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The jdbcDataSource being used by JDBCUserStoreManager :: " + jdbcds.hashCode());
			}
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Loading JDBC datasource failed", e);
			}
		}

		dataSource = (DataSource) properties.get(UserCoreConstants.DATA_SOURCE);
		if (dataSource == null) {
			dataSource = DatabaseUtil.getRealmDataSource(realmConfig);
		}
		if (dataSource == null) {
			throw new UserStoreException("User Management Data Source is null");
		}

		properties.put(UserCoreConstants.DATA_SOURCE, dataSource);

		realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));

		this.persistDomain();
		doInitialSetup();
		if (!skipInitData && realmConfig.isPrimary()) {
			addInitialAdminData(Boolean.parseBoolean(realmConfig.getAddAdmin()), !isInitSetupDone());
		}

		initUserRolesCache();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Ended " + System.currentTimeMillis());
		}

	}

	private DataSource getJDBCDataSource() {
		if (jdbcds == null) {
			jdbcds = loadUserStoreSpacificDataSoruce();
		}
		return jdbcds;
	}

	@Override
	public String[] doListUsers(String filter, int limit) throws UserStoreException {

		LOGGER.warn("doListUsers:" + filter);

		filter = removeDomainName(filter);

		String[] users = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		if (limit == 0) {
			return new String[0];
		}

		final int givenMax = getGivenMax();
		final int searchTime = getSearchTime(UserCoreConstants.MAX_SEARCH_TIME,
				UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME);

		if (limit < 0 || limit > givenMax) {
			limit = givenMax;
		}

		try {

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION_MESSAGE);
			}

			sqlStmt = getStatementDoListUsers(filter);

			filter = buildFilterDoListUsers(filter);

			LOGGER.debug("doListUsers SQL:" + sqlStmt);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			doListUsersPrepareStatement(prepStmt, sqlStmt, filter);
			prepStmt.setMaxRows(limit);
			assignTimeout(prepStmt, searchTime);

			rs = doListUsersExecute(prepStmt, filter, limit);

			if (rs != null) {
				List<String> lst = new LinkedList<>();
				while (rs.next()) {

					String name = rs.getString(1);
					if (CarbonConstants.REGISTRY_ANONNYMOUS_USERNAME.equals(name)) {
						continue;
					}
					// append the domain if exist
					String domain = realmConfig
							.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
					name = UserCoreUtil.addDomainToName(name, domain);
					lst.add(name);
				}
				rs.close();
				if (!lst.isEmpty()) {
					users = lst.toArray(new String[lst.size()]);
				}
			}

			Arrays.sort(users);

		} catch (SQLException e) {
			String msg = ERROR_OCCURRED_WHILE_RETRIEVING_USERS_FOR_FILTER_MESSAGE + filter + " & max Item limit : "
					+ limit;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return users;

	}

	private String getStatementDoListUsers(String filter) {
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

	private String buildFilterDoListUsers(String filter) {
		filter = trim(filter, filter.trim().length() != 0, filter.trim(), STAR, PERCENT);
		filter = filter.replace(EXCLAIM, UNDERSCORE);
		filter = filter.replace(UNDERSCORE, "\\\\_");
		return filter;
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

	private String trim(String filter, boolean b, String trim, String s, String s2) {
		if (filter != null && b) {
			filter = trim;
			filter = filter.replace(s, s2);
		} else {
			filter = PERCENT;
		}
		return filter;
	}

	private int getSearchTime(int maxSearchTime, String propertyMaxSearchTime) {
		int searchTime;
		try {
			searchTime = Integer.parseInt(realmConfig.getUserStoreProperty(propertyMaxSearchTime));
		} catch (Exception e) {
			searchTime = maxSearchTime;
		}
		return searchTime;
	}

	private int getGivenMax() {
		return getSearchTime(UserCoreConstants.MAX_USER_ROLE_LIST,
				UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST);
	}

	private String getUserFilterQuery(String caseSensitiveQueryPropertyName, String caseInsensitiveQueryPropertyName) {

		return getSqlStatement(isCaseSensitiveUsername(), caseSensitiveQueryPropertyName,
				caseInsensitiveQueryPropertyName);
	}

	@Override
	public boolean doCheckIsUserInRole(String userName, String roleName) throws UserStoreException {

		String[] roles = doGetExternalRoleListOfUser(userName, roleName);
		if (roles != null) {
			for (String role : roles) {
				if (role.equalsIgnoreCase(roleName)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected String[] doGetDisplayNamesForInternalRole(String[] userNames) {
		LOGGER.warn("doGetDisplayNamesForInternalRole:" + Arrays.toString(userNames));
		return userNames;
	}

	public String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException {
		LOGGER.warn("doGetRoleNames:" + filter);
		String[] roles = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		if (maxItemLimit == 0) {
			return roles;
		}

		try {
			filter = removeDomainName(filter);
			filter = normalizeAndCheckFilter(filter);

			List<String> lst = new LinkedList<>();

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION_MESSAGE);
			}

			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST);

			LOGGER.debug("doGetRoleNames SQL:" + sqlStmt);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			byte count = 0;
			prepStmt.setString(++count, filter);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(++count, tenantId);
			}
			setPSRestrictions(prepStmt, maxItemLimit);

			rs = doGetRoleNamesExecute(prepStmt, filter, maxItemLimit);

			// Expected columns UM_ROLE_NAME, UM_TENANT_ID, UM_SHARED_ROLE
			if (rs != null) {
				while (rs.next()) {
					String name = rs.getString(1);
					// append the domain if exist
					String domain = realmConfig
							.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
					name = UserCoreUtil.addDomainToName(name, domain);
					lst.add(name);
				}
			}

			if (!lst.isEmpty()) {
				roles = lst.toArray(new String[lst.size()]);
			}

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving role names for filter : " + filter + " & max item limit : "
					+ maxItemLimit;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return roles;

	}

	private ResultSet doGetRoleNamesExecute(PreparedStatement prepStmt, String filter, int maxItemLimit)
			throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED_MESSAGE, e);
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

	private String normalizeAndCheckFilter(String filter) {
		if (filter != null && filter.trim().length() != 0) {
			filter = normalizeFilter(filter);
		} else {
			filter = PERCENT;
		}
		return filter;
	}

	private String normalizeFilter(String filter) {
		filter = filter.trim();
		filter = filter.replace(STAR, PERCENT);
		filter = filter.replace(EXCLAIM, UNDERSCORE);
		return filter;
	}

	private void setPSRestrictions(PreparedStatement ps, int maxItemLimit) throws SQLException {

		int givenMax;
		int searchTime;

		try {
			givenMax = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_ROLE_LIST));
		} catch (Exception e) {
			givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		}

		searchTime = computeSearchTimeListUsers();

		if (maxItemLimit < 0 || maxItemLimit > givenMax) {
			maxItemLimit = givenMax;
		}

		ps.setMaxRows(maxItemLimit);
		assignTimeout(ps, searchTime);
	}

	@Override
	protected String[] doGetSharedRoleNames(String tenantDomain, String filter, int maxItemLimit)
			throws UserStoreException {
		LOGGER.warn("doGetSharedRoleNames:" + tenantDomain + " " + filter);
		String[] roles = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		if (maxItemLimit == 0) {
			return roles;
		}

		try {
			if (!isSharedGroupEnabled()) {
				return roles;
			}

			filter = removeDomainName(filter);
			filter = normalizeAndCheckFilter(filter);

			List<String> lst = new LinkedList<>();

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION_MESSAGE);
			}

			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_SHARED_ROLE_LIST);

			LOGGER.debug("doGetSharedRoleNames SQL:" + sqlStmt);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			byte count = 0;
			prepStmt.setString(++count, filter);
			setPSRestrictions(prepStmt, maxItemLimit);

			rs = doGetSharedRoleNamesExecute(prepStmt, tenantDomain, filter, maxItemLimit);

			// Expected columns UM_ROLE_NAME, UM_TENANT_ID, UM_SHARED_ROLE
			if (rs != null) {
				while (rs.next()) {
					String name = rs.getString(1);
					int roleTenantId = rs.getInt(2);
					// append the domain if exist
					String domain = realmConfig
							.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
					name = UserCoreUtil.addDomainToName(name, domain);
					name = UserCoreUtil.addTenantDomainToEntry(name, String.valueOf(roleTenantId));
					lst.add(name);
				}
			}

			if (!lst.isEmpty()) {
				roles = lst.toArray(new String[lst.size()]);
			}
		} catch (SQLException e) {
			String errorMessage = "Error while retrieving roles from JDBC user store for tenant domain : "
					+ tenantDomain + " & filter : " + filter + "& max item limit : " + maxItemLimit;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return roles;
	}

	private ResultSet doGetSharedRoleNamesExecute(PreparedStatement prepStmt, String tenantDomain, String filter,
			int maxItemLimit) throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED_MESSAGE, e);
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

	public String[] doGetUserListOfRole(String roleName, String filter) throws UserStoreException {
		LOGGER.warn("doGetUserListOfRole:" + roleName + " " + filter);
		RoleContext roleContext = createRoleContext(roleName);
		return getUserListOfJDBCRole(roleContext, filter);
	}

	public String[] getUserListOfJDBCRole(RoleContext ctx, String filter) throws UserStoreException {
		return getUserListOfJDBCRole(ctx, filter, QUERY_MAX_ITEM_LIMIT_ANY);
	}

	@Override
	public String[] doGetUserListOfRole(String roleName, String filter, int maxItemLimit) throws UserStoreException {
		RoleContext roleContext = createRoleContext(roleName);
		return getUserListOfJDBCRole(roleContext, filter, maxItemLimit);
	}

	/**
	 * Return the list of users belong to the given JDBC role for the given {@link RoleContext}, filter and max item limit.
	 *
	 * @param ctx          {@link RoleContext} corresponding to the JDBC role.
	 * @param filter       String filter for the users.
	 * @param maxItemLimit Maximum number of items returned.
	 * @return The list of users matching the provided constraints.
	 */
	public String[] getUserListOfJDBCRole(RoleContext ctx, String filter, int maxItemLimit) throws UserStoreException {
		String roleName = ctx.getRoleName();
		String[] names = null;
		String sqlStmt = null;

		LOGGER.warn("getUserListOfJDBCRole:" + ctx + " " + roleName + " " + filter);
		roleName = removeDomainName(roleName);
		filter = removeDomainName(filter);

		if (maxItemLimit == 0) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}

		if (maxItemLimit < 0 || maxItemLimit > maximumUserNameListLength) {
			maxItemLimit = maximumUserNameListLength;
		}

		if (StringUtils.isNotEmpty(filter)) {
			filter = normalizeFilter(filter);
		} else {
			filter = PERCENT;
		}

		LOGGER.debug("getUserListOfJDBCRole shared:" + ctx.isShared());

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

		return names;
	}

	public boolean doCheckExistingRole(String roleName) throws UserStoreException {
		LOGGER.debug("doCheckExistingRole:" + roleName);
		RoleContext roleContext = createRoleContext(roleName);
		return isExistingJDBCRole(roleContext);
	}

	protected boolean isExistingJDBCRole(RoleContext context) throws UserStoreException {

		boolean isExisting;
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

		return isExisting;
	}

	public String[] getAllProfileNames() throws UserStoreException {
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

		return names;
	}

	public String[] getProfileNames(String userName) throws UserStoreException {
		userName = UserCoreUtil.removeDomainFromName(userName);

		final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_PROFILE_NAMES_FOR_USER,
				JDBCCaseInsensitiveConstants.GET_PROFILE_NAMES_FOR_USER_CASE_INSENSITIVE);
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
			if (Arrays.binarySearch(names, UserCoreConstants.DEFAULT_PROFILE) < 0) {
				// we have to add the default profile
				String[] newNames = new String[names.length + 1];
				System.arraycopy(names, 0, newNames, 0, names.length);
				newNames[names.length] = UserCoreConstants.DEFAULT_PROFILE;
				names = newNames;
			}
		}

		return names;
	}

	public int getUserId(String username) throws UserStoreException {
		username = removeDomainName(username);

		final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_USERID_FROM_USERNAME,
				JDBCCaseInsensitiveConstants.GET_USERID_FROM_USERNAME_CASE_INSENSITIVE);
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
		return id;
	}

	/**
	 * @param tenantId tenant id
	 * @return array of users of the tenant.
	 * @throws UserStoreException throws user store exception
	 */
	public String[] getUserNames(int tenantId) throws UserStoreException {
		String sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERNAME_FROM_TENANT_ID);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving user names is null");
		}
		String[] userNames;
		Connection dbConnection = null;
		try {
			dbConnection = getDBConnection();
			userNames = DatabaseUtil.getStringValuesFromDatabase(dbConnection, sqlStmt, tenantId);
		} catch (SQLException e) {
			String errorMessage = "Error occurred while getting username from tenant ID : " + tenantId;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection);
		}
		return userNames;
	}

	/**
	 * @return the admin user.
	 * @throws org.wso2.carbon.user.core.UserStoreException from the getUserNames()
	 * @deprecated Returns the admin users for the given tenant.
	 */
	public String getAdminUser() throws UserStoreException {
		String[] users = getUserListOfRole(this.realmConfig.getAdminRoleName());
		if (users != null && users.length > 0) {
			return users[0];
		}
		return null;
	}

	public int getTenantId() throws UserStoreException {
		return this.tenantId;
	}

	public Map<String, String> getProperties(org.wso2.carbon.user.api.Tenant tenant)
			throws org.wso2.carbon.user.api.UserStoreException {
		return getProperties((Tenant) tenant);
	}

	public int getTenantId(String username) throws UserStoreException {
		if (this.tenantId != MultitenantConstants.SUPER_TENANT_ID) {
			throw new UserStoreException("Not allowed to perform this operation");
		}
		final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(),
				JDBCRealmConstants.GET_TENANT_ID_FROM_USERNAME,
				JDBCCaseInsensitiveConstants.GET_TENANT_ID_FROM_USERNAME_CASE_INSENSITIVE);
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
		return id;
	}

	public Map<String, String> getUserPropertyValues(String userName, String[] propertyNames, String profileName)
			throws UserStoreException {
		LOGGER.warn("getUserPropertyValues:" + userName + " " + Arrays.toString(propertyNames) + " " + profileName + " "
				+ tenantId);

		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}
		userName = removeDomainName(userName);

		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[] propertyNamesSorted = propertyNames.clone();
		Arrays.sort(propertyNamesSorted);
		Map<String, String> map = new HashMap<>();
		try {
			dbConnection = getDBConnection();
			final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_PROPS_FOR_PROFILE,
					JDBCCaseInsensitiveConstants.GET_PROPS_FOR_PROFILE_CASE_INSENSITIVE);

			LOGGER.debug("getUserPropertyValues SQL:" + sqlStmt);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			getUserPropertyValuesPrepareStatement(userName, profileName, prepStmt, sqlStmt);
			rs = prepStmt.executeQuery();
			buildUserPropertyValues(rs, propertyNamesSorted, map);

			return map;
		} catch (SQLException e) {
			String errorMessage = "Error Occurred while getting property values for user : " + userName
					+ PROFILE_NAME_MESSAGE + profileName;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMessage, e);
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
	}

	private void buildUserPropertyValues(ResultSet rs, String[] propertyNamesSorted, Map<String, String> map)
			throws SQLException {
		while (rs.next()) {
			String name = rs.getString(1);
			String value = rs.getString(2);
			if (Arrays.binarySearch(propertyNamesSorted, name) < 0) {
				continue;
			}
			map.put(name, value);
		}
	}

	private void getUserPropertyValuesPrepareStatement(String userName, String profileName, PreparedStatement prepStmt,
			final String sqlStmt) throws SQLException {
		prepStmt.setString(1, userName);
		prepStmt.setString(2, profileName);
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			prepStmt.setInt(3, tenantId);
			prepStmt.setInt(4, tenantId);
		}
	}

	/**
	 */
	private String[] getStringValuesFromDatabase(String sqlStmt, Object... params) throws UserStoreException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query: " + sqlStmt);
			for (int i = 0; i < params.length; i++) {
				Object param = params[i];
				LOGGER.debug("Input value: " + param);
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
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return values;
	}

	/**
	 * Get {@link String}[] of values from the database for the given SQL query and the constraints.
	 *
	 * @param sqlStmt      {@link String} SQL query.
	 * @param maxRows      Upper limit to the number of rows returned from the database.
	 * @param queryTimeout SQL query timeout limit in seconds. Zero means there is no limit.
	 * @param params       Values passed for the SQL query placeholders.
	 * @return {@link String}[] of results.
	 */
	private String[] getStringValuesFromDatabaseWithConstraints(String sqlStmt, int maxRows, int queryTimeout,
			Object... params) throws UserStoreException {

		if (LOGGER.isDebugEnabled()) {
			String loggableSqlString = getLoggableSqlString(sqlStmt, params);
			String msg = "getStringValuesFromDatabaseWithConstraints sql: " + loggableSqlString + ", and maxRows: "
					+ maxRows + ", and queryTimeout: " + queryTimeout;
			LOGGER.debug(msg);
		}

		String[] values;
		try (Connection dbConnection = getDBConnection()) {
			values = DatabaseUtil.getStringValuesFromDatabaseWithConstraints(dbConnection, sqlStmt, maxRows,
					queryTimeout, params);
		} catch (Exception e) {
			String msg = "Error occurred while accessing the database connection." + e.getMessage();
			LOGGER.error(msg, e);
			throw new UserStoreException(msg, e);
		}
		return values;
	}

	private String[] getRoleNamesWithDomain(String sqlStmt, String userName, int tenantId, boolean appendDn)
			throws UserStoreException {
		LOGGER.warn("getRoleNamesWithDomain:" + sqlStmt + " " + userName + " " + userName);
		userName = removeDomainName(userName);

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

			LOGGER.debug("getRoleNamesWithDomain:" + domain);

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
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return roles.toArray(new String[roles.size()]);
	}

	/**
	 */
	protected Connection getDBConnection() throws SQLException, UserStoreException {
		@SuppressWarnings({ "unused", "java:S2095" }) // La connexion est fermée par les méthodes appelantes via DatabaseUtil.closeAllConnections
		Connection dbConnection = getJDBCDataSource().getConnection();
		dbConnection.setAutoCommit(false);
		return dbConnection;
	}

	/**
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
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			if (doClose) {
				DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
			}
		}
	}

	public boolean doCheckExistingUser(String userName) throws UserStoreException {

		final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_IS_USER_EXISTING,
				JDBCCaseInsensitiveConstants.GET_IS_USER_EXISTING_CASE_INSENSITIVE);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for is user existing null");
		}
		boolean isExisting = false;

		String isUnique = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_USERNAME_UNIQUE);
		if (Boolean.parseBoolean(isUnique) && !CarbonConstants.REGISTRY_ANONNYMOUS_USERNAME.equals(userName)) {
			final String uniquenesSql = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.USER_NAME_UNIQUE,
					JDBCCaseInsensitiveConstants.USER_NAME_UNIQUE_CASE_INSENSITIVE);
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

		return isExisting;
	}

	public boolean doAuthenticate(String userName, Object credential) throws UserStoreException {
		LOGGER.debug("doAuthenticate:" + userName);
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

			prepStmt = doAuthenticateCreateStatement(dbConnection, userName);

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

		return isAuthed;
	}

	private boolean doAuthenticateHandleResult(ResultSet rs, String username, Object credential) throws SQLException {
		boolean isAuthed = false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doAuthenticate: result (" + rs.getLong(1) + ")");
		}
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
		return isAuthed;
	}

	@SuppressWarnings({ "unused", "java:S2095" }) // La connexion est fermée par les méthodes appelantes via DatabaseUtil.closeAllConnections
	private PreparedStatement doAuthenticateCreateStatement(Connection dbConnection, String userName)
			throws SQLException {
		PreparedStatement prepStmt;
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
		return prepStmt;
	}

	public boolean isReadOnly() throws UserStoreException {
		return true;
	}

	public void doAddUser(String userName, Object credential, String[] roleList, Map<String, String> claims,
			String profileName, boolean requirePasswordChange) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doAddRole(String roleName, String[] userList, boolean shared) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doUpdateRoleName(String roleName, String newRoleName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	public boolean isSharedRole(String roleName, String roleNameBase) {
		return roleNameBase != null && roleNameBase.indexOf(TRUE_VALUE) > -1;
	}

	/**
	 * JDBC User store supports bulk import.
	 *
	 * @return Always true
	 */
	public boolean isBulkImportSupported() {
		return false;
	}

	public RealmConfiguration getRealmConfiguration() {
		return this.realmConfig;
	}

	/**
	 * User of this
	 *
	 */
	public RoleDTO[] getRoleNamesWithDomain(boolean noHybridRoles) throws UserStoreException {

		String[] names = null;
		String domain = realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);

		String sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_ROLE_LIST);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving role name is null");
		}
		names = getStringValuesFromDatabase(sqlStmt, tenantId);
		if (isReadOnly() && !noHybridRoles) {
			String[] hybrids = hybridRoleManager.getHybridRoles(STAR);
			names = UserCoreUtil.combineArrays(names, hybrids);
		}

		List<RoleDTO> roleDTOs = new ArrayList<>();
		if (names != null && names.length != 0) {
			roleDTOs.addAll(Arrays.asList(UserCoreUtil.convertRoleNamesToRoleDTO(names, domain)));
		}

		RoleDTO[] secondaryRoleDTOs = getAllSecondaryRoleDTOs();
		if (secondaryRoleDTOs != null && secondaryRoleDTOs.length != 0) {
			roleDTOs.addAll(Arrays.asList(secondaryRoleDTOs));
		}

		return roleDTOs.toArray(new RoleDTO[roleDTOs.size()]);
	}

	/**
	 * This method is to check whether multiple profiles are allowed with a particular user-store. For an example, currently, JDBC user store supports
	 * multiple profiles and where as ApacheDS does not allow. Currently, JDBC user store allows multiple profiles. Hence return true.
	 *
	 * @return boolean
	 */
	public boolean isMultipleProfilesAllowed() {
		return true;
	}

	public void doDeleteRole(String roleName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doDeleteUser(String userName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doUpdateUserListOfRole(String roleName, String[] deletedUsers, String[] newUsers)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doUpdateRoleListOfUser(String userName, String[] deletedRoles, String[] newRoles)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	protected void doSetUserAttribute(String userName, String attributeName, String value, String profileName)
			throws UserStoreException {

		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	public void doSetUserClaimValues(String userName, Map<String, String> claims, String profileName)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doDeleteUserClaimValue(String userName, String claimURI, String profileName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doDeleteUserClaimValues(String userName, String[] claims, String profileName)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doUpdateCredential(String userName, Object newCredential, Object oldCredential)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	public void doUpdateCredentialByAdmin(String userName, Object newCredential) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	/**
	 * Get the password exiparation time of the user.
	 * 
	 * @param userName username.
	 * @return date.
	 */
	@Override
	public Date doGetPasswordExpirationTime(String userName) throws UserStoreException {
		LOGGER.debug(DO_GET_PASSWORD_EXPIRATION_TIME + userName);
		if (userName != null && userName.contains(CarbonConstants.DOMAIN_SEPARATOR)) {
			return super.getPasswordExpirationTime(userName);
		}

		Connection dbConnection = null;
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		Date date = null;

		try {
			dbConnection = getDBConnection();
			dbConnection.setAutoCommit(false);

			prepStmt = doGetPasswordExpirationTimeCreateStatement(dbConnection, userName);

			rs = prepStmt.executeQuery();

			if (rs.next()) {
				date = doGetPasswordExpirationTimeHandleResult(rs, date);
			}
		} catch (SQLException e) {
			String msg = "Error occurred while retrieving password expiration time for user : " + userName;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		LOGGER.debug(DO_GET_PASSWORD_EXPIRATION_TIME + date);
		return date;
	}

	private Date doGetPasswordExpirationTimeHandleResult(ResultSet rs, Date date) throws SQLException {
		boolean requireChange = rs.getBoolean(5);
		Timestamp changedTime = rs.getTimestamp(6);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(DO_GET_PASSWORD_EXPIRATION_TIME + requireChange + "/" + changedTime);
		}
		if (requireChange) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(changedTime);
			gc.add(Calendar.HOUR, 24);
			date = gc.getTime();
		}
		return date;
	}

	@SuppressWarnings({ "unused", "java:S2095" }) // La connexion est fermée par les méthodes appelantes via DatabaseUtil.closeAllConnections
	private PreparedStatement doGetPasswordExpirationTimeCreateStatement(Connection dbConnection, String userName)
			throws SQLException {
		PreparedStatement prepStmt;
		final String sqlstmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.SELECT_USER,
				JDBCCaseInsensitiveConstants.SELECT_USER_CASE_INSENSITIVE);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doGetPasswordExpirationTime SQL:" + sqlstmt);
		}
		userName = removeDomainName(userName);

		prepStmt = dbConnection.prepareStatement(sqlstmt);
		prepStmt.setString(1, userName);
		if (sqlstmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			prepStmt.setInt(2, tenantId);
		}
		return prepStmt;
	}

	/**
	 */
	protected String getProperty(Connection dbConnection, String userName, String propertyName, String profileName)
			throws UserStoreException {

		final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_PROP_FOR_PROFILE,
				JDBCCaseInsensitiveConstants.GET_PROP_FOR_PROFILE_CASE_INSENSITIVE);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for add user property sql is null");
		}

		userName = removeDomainName(userName);

		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String value = null;
		try {
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			getUserListFromPropertiesPrepareStatement(prepStmt, sqlStmt, userName, propertyName, profileName);

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				value = rs.getString(1);
			}
			return value;
		} catch (SQLException e) {
			String msg = "Error occurred while retrieving user profile property for user : " + userName
					+ " & property name : " + propertyName + PROFILE_NAME_MESSAGE + profileName;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(null, rs, prepStmt);
		}
	}

	/**
	 */
	private DataSource loadUserStoreSpacificDataSoruce() {
		return DatabaseUtil.createUserStoreDataSource(realmConfig);
	}

	public Map<String, String> getProperties(Tenant tenant) throws UserStoreException {
		return this.realmConfig.getUserStoreProperties();
	}

	public void addRememberMe(String userName, String token) throws org.wso2.carbon.user.api.UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	/**
	 * Checks whether the token is existing or not.
	 *
	 * @param userName le userName
	 * @param token    le token
	 * @return
	 * @throws org.wso2.carbon.user.api.UserStoreException
	 */
	public boolean isExistingRememberMeToken(String userName, String token)
			throws org.wso2.carbon.user.api.UserStoreException {
		return false;
	}

	public boolean isValidRememberMeToken(String userName, String token)
			throws org.wso2.carbon.user.api.UserStoreException {
		try {
			if (isExistingUser(userName)) {
				return isExistingRememberMeToken(userName, token);
			}
		} catch (Exception e) {
			LOGGER.error("Validating remember me token failed for" + userName);
			// not throwing exception.
			// because we need to seamlessly direct them to login uis
		}

		return false;
	}

	@Override
	public String[] getUserListFromProperties(String property, String value, String profileName)
			throws UserStoreException {

		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}

		if (value == null) {
			throw new IllegalArgumentException(FILTER_VALUE_CANNOT_BE_NULL);
		}
		if (value.contains(QUERY_FILTER_STRING_ANY)) {
			// This is to support LDAP like queries. Value having only * is
			// restricted
			// except one *.
			if (!value.matches(QUERY_FILTER_STRING_ANY_MATCH_REGEX)) {
				// Convert all the * to % except \*.
				value = value.replaceAll(QUERY_FILTER_STRING_ANY_REPLACE_REGEX, SQL_FILTER_STRING_ANY);
			}
		}

		String[] users = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		List<String> list = new ArrayList<>();
		try {
			dbConnection = getDBConnection();
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_FOR_PROP);
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			getUserListFromPropertiesPrepareStatement(prepStmt, sqlStmt, property, value, profileName);

			int givenMax;
			int searchTime;
			int maxItemLimit = MAX_ITEM_LIMIT_UNLIMITED;
			givenMax = computeGivenMaxListUsers();
			searchTime = computeSearchTimeListUsers();
			if (maxItemLimit < 0 || maxItemLimit > givenMax) {
				maxItemLimit = givenMax;
			}
			prepStmt.setMaxRows(maxItemLimit);
			assignTimeout(prepStmt, searchTime);
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
					+ value + PROFILE_NAME_MESSAGE + profileName;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}

		return users;
	}

	private void getUserListFromPropertiesPrepareStatement(PreparedStatement prepStmt, String sqlStmt, String property,
			String value, String profileName) throws SQLException {
		prepStmt.setString(1, property);
		prepStmt.setString(2, value);
		prepStmt.setString(3, profileName);
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			prepStmt.setInt(4, tenantId);
			prepStmt.setInt(5, tenantId);
		}
	}

	@Override
	public String[] doGetExternalRoleListOfUser(String userName, String filter) throws UserStoreException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting roles of user: " + userName + " with filter: " + filter);
		}
		userName = removeDomainName(userName);
		filter = removeDomainName(filter);

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
			filter = normalizeFilter(filter);
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
		List<String> roles = new ArrayList<>();
		logRoles(userName, names);

		Collections.addAll(roles, names);
		return roles.toArray(new String[roles.size()]);
	}

	private void logRoles(String userName, String[] names) {
		if (LOGGER.isDebugEnabled()) {
			if (names != null) {
				for (String name : names) {
					LOGGER.debug("Found role: " + name);
				}
			} else {
				LOGGER.debug("No external role found for the user: " + userName);
			}
		}
	}

	@Override
	public org.wso2.carbon.user.api.Properties getDefaultUserStoreProperties() {
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
	protected Map<String, Map<String, String>> getUsersPropertyValues(List<String> users, String[] propertyNames,
			String profileName) throws UserStoreException {

		Connection dbConnection = null;
		String sqlStmt;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		String[] propertyNamesSorted = propertyNames.clone();
		Arrays.sort(propertyNamesSorted);

		Map<String, Map<String, String>> usersPropertyValuesMap = new HashMap<>();
		try {
			dbConnection = getDBConnection();
			StringBuilder usernameParameter = new StringBuilder();
			if (isCaseSensitiveUsername()) {
				sqlStmt = getUsersPropertyValuesCaseUnsensitive(usernameParameter, users);
			} else {
				sqlStmt = getUsersPropertyValuesCaseSensitive(usernameParameter, users);
			}

			sqlStmt = sqlStmt.replaceFirst("\\?", Matcher.quoteReplacement(usernameParameter.toString()));
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			prepStmt.setString(1, profileName);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(2, tenantId);
				prepStmt.setInt(3, tenantId);
			}

			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(2);
				if (Arrays.binarySearch(propertyNamesSorted, name) < 0) {
					continue;
				}
				String username = rs.getString(1);
				String value = rs.getString(3);

				if (usersPropertyValuesMap.get(username) != null) {
					usersPropertyValuesMap.get(username).put(name, value);
				} else {
					Map<String, String> attributes = new HashMap<>();
					attributes.put(name, value);
					usersPropertyValuesMap.put(username, attributes);
				}
			}
			return usersPropertyValuesMap;
		} catch (SQLException e) {
			String errorMessage = "Error Occurred while getting property values";
			if (LOGGER.isDebugEnabled()) {
				errorMessage = errorMessage + ": " + users;
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
	}

	private String getUsersPropertyValuesCaseSensitive(StringBuilder usernameParameter, List<String> users) {
		String sqlStmt;
		sqlStmt = realmConfig
				.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USERS_PROPS_FOR_PROFILE_CASE_INSENSITIVE);
		for (int i = 0; i < users.size(); i++) {

			users.set(i, removeDomainName(users.get(i).replace("'", "''")));
			usernameParameter.append("LOWER('").append(users.get(i)).append("')");

			if (i != users.size() - 1) {
				usernameParameter.append(",");
			}
		}
		return sqlStmt;
	}

	private String getUsersPropertyValuesCaseUnsensitive(StringBuilder usernameParameter, List<String> users) {
		String sqlStmt;
		sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_PROPS_FOR_PROFILE);
		for (int i = 0; i < users.size(); i++) {

			users.set(i, removeDomainName(users.get(i).replace("'", "''")));
			usernameParameter.append("'").append(users.get(i)).append("'");

			if (i != users.size() - 1) {
				usernameParameter.append(",");
			}
		}
		return sqlStmt;
	}

	@Override
	protected Map<String, List<String>> doGetExternalRoleListOfUsers(List<String> userNames) throws UserStoreException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting roles of users: " + userNames);
		}

		String sqlStmt;
		Map<String, List<String>> rolesListOfUsersMap = new HashMap<>();
		Connection dbConnection = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		try {
			dbConnection = getDBConnection();
			StringBuilder usernameParameter = new StringBuilder();
			if (isCaseSensitiveUsername()) {
				sqlStmt = doGetExternalRoleListOfUsersCaseUnsensitive(usernameParameter, userNames);
			} else {
				sqlStmt = doGetExternalRoleListOfUsersCaseSensitive(usernameParameter, userNames);
			}

			sqlStmt = sqlStmt.replaceFirst("\\?", Matcher.quoteReplacement(usernameParameter.toString()));
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(1, tenantId);
				prepStmt.setInt(2, tenantId);
				prepStmt.setInt(3, tenantId);
			}
			rs = prepStmt.executeQuery();
			String domainName = getMyDomainName();
			while (rs.next()) {
				String username = UserCoreUtil.addDomainToName(rs.getString(1), domainName);
				String roleName = UserCoreUtil.addDomainToName(rs.getString(2), domainName);
				if (rolesListOfUsersMap.get(username) != null) {
					rolesListOfUsersMap.get(username).add(roleName);
				} else {
					List<String> roleNames = new ArrayList<>();
					roleNames.add(roleName);
					rolesListOfUsersMap.put(username, roleNames);
				}
			}
			return rolesListOfUsersMap;
		} catch (SQLException e) {
			String errorMessage = "Error Occurred while getting role lists of users";
			if (LOGGER.isDebugEnabled()) {
				errorMessage = errorMessage + ": " + userNames;
			}
			throw new UserStoreException(errorMessage, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
	}

	private String doGetExternalRoleListOfUsersCaseSensitive(StringBuilder usernameParameter, List<String> userNames)
			throws UserStoreException {
		String sqlStmt;
		sqlStmt = realmConfig.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USERS_ROLE_CASE_INSENSITIVE);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving users roles is null");
		}
		for (int i = 0; i < userNames.size(); i++) {

			userNames.set(i, removeDomainName(userNames.get(i).replace("'", "''")));
			usernameParameter.append("LOWER('").append(userNames.get(i)).append("')");

			if (i != userNames.size() - 1) {
				usernameParameter.append(",");
			}
		}
		return sqlStmt;
	}

	private String doGetExternalRoleListOfUsersCaseUnsensitive(StringBuilder usernameParameter, List<String> userNames)
			throws UserStoreException {
		String sqlStmt;
		sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USERS_ROLE);
		if (sqlStmt == null) {
			throw new UserStoreException("The sql statement for retrieving users roles is null");
		}
		for (int i = 0; i < userNames.size(); i++) {

			userNames.set(i, removeDomainName(userNames.get(i).replace("'", "''")));
			usernameParameter.append("'").append(userNames.get(i)).append("'");

			if (i != userNames.size() - 1) {
				usernameParameter.append(",");
			}
		}
		return sqlStmt;
	}

	protected void doAddSharedRole(String roleName, String[] userList) throws UserStoreException {

		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	protected String[] doGetSharedRoleListOfUser(String userName, String tenantDomain, String filter)
			throws UserStoreException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Looking for shared roles for user: " + userName + " for tenant: " + tenantDomain);
		}

		if (isSharedGroupEnabled()) {
			// shared roles
			final String sqlStmt = getSqlStatement(isCaseSensitiveUsername(),
					JDBCRealmConstants.GET_SHARED_ROLES_FOR_USER,
					JDBCCaseInsensitiveConstants.GET_SHARED_ROLES_FOR_USER_CASE_INSENSITIVE);
			return getRoleNamesWithDomain(sqlStmt, userName, tenantId, true);
		}
		return new String[0];
	}

	@Override
	protected RoleContext createRoleContext(String roleName) {

		JDBCRoleContext searchCtx = new JDBCRoleContext();
		String[] roleNameParts;

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
		return searchCtx;
	}

	@Override
	protected PaginatedSearchResult doListUsers(String filter, int limit, int offset) throws UserStoreException {

		String[] users = new String[0];
		Connection dbConnection = null;
		String sqlStmt;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		int givenMax;
		int searchTime;

		PaginatedSearchResult result = new PaginatedSearchResult();

		if (limit == 0) {
			return result;
		}

		givenMax = computeGivenMaxListUsers();
		searchTime = computeSearchTimeListUsers();

		if (limit < 0 || limit > givenMax) {
			limit = givenMax;
		}

		try {
			filter = removeDomainName(filter);
			filter = normalizeAndCheckFilter(filter);

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION_MESSAGE);
			}

			String type = DatabaseCreator.getDatabaseType(dbConnection);

			if (offset <= 0) {
				offset = 0;
			} else {
				offset = offset - 1;
			}

			if (DB2.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = offset + limit;
				limit = initialOffset + 1;
			} else if (MSSQL.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = limit + offset;
				limit = initialOffset + 1;
			} else if (ORACLE.equalsIgnoreCase(type)) {
				limit = offset + limit;
			}

			sqlStmt = getListUsersSqlStatement(type);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			doListUsersPrepareStatement(prepStmt, sqlStmt, filter, limit, offset);

			assignTimeout(prepStmt, searchTime);

			rs = doListUsersExecute(prepStmt, filter, limit);
			if (rs != null) {
				List<String> list = new LinkedList<>();

				while (rs.next()) {
					String name = rs.getString(1);
					if (CarbonConstants.REGISTRY_ANONNYMOUS_USERNAME.equals(name)) {
						continue;
					}
					// append the domain if exist
					String domain = realmConfig
							.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_DOMAIN_NAME);
					name = UserCoreUtil.addDomainToName(name, domain);
					list.add(name);
				}
				rs.close();

				if (!list.isEmpty()) {
					users = list.toArray(new String[list.size()]);
				}

				Arrays.sort(users);
			}
		} catch (Exception e) {
			String msg = ERROR_OCCURRED_WHILE_RETRIEVING_USERS_FOR_FILTER_MESSAGE + filter + " & limit : " + limit;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		result.setUsers(users);

		if (users.length == 0) {
			result.setSkippedUserCount(doGetListUsersCount(filter));
		}
		return result;
	}

	private ResultSet doListUsersExecute(PreparedStatement prepStmt, String filter, int limit)
			throws UserStoreException {
		ResultSet rs = null;
		try {
			rs = prepStmt.executeQuery();
		} catch (SQLTimeoutException e) {
			LOGGER.error(THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED_MESSAGE, e);
		} catch (SQLException e) {
			String errorMessage = "Error while fetching users according to filter : " + filter + " & limit " + ": "
					+ limit;
			LOGGER.debug(errorMessage, e);
			throw new UserStoreException(errorMessage, e);
		}
		return rs;
	}

	private void doListUsersPrepareStatement(PreparedStatement prepStmt, String sqlStmt, String filter, int limit,
			int offset) throws SQLException {
		prepStmt.setString(1, filter);
		if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
			prepStmt.setInt(2, tenantId);
			prepStmt.setLong(3, limit);
			prepStmt.setLong(4, offset);
		} else {
			prepStmt.setLong(2, limit);
			prepStmt.setLong(3, offset);
		}
	}

	private String getListUsersSqlStatement(String type) {
		String sqlStmt;
		if (isCaseSensitiveUsername()) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USER_FILTER_PAGINATED + "-" + type);
			if (sqlStmt == null) {
				sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_USER_FILTER_PAGINATED);
			}
		} else {
			sqlStmt = realmConfig.getUserStoreProperty(
					JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE_PAGINATED + "-" + type);
			if (sqlStmt == null) {
				sqlStmt = realmConfig
						.getUserStoreProperty(JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE_PAGINATED);
			}
		}
		return sqlStmt;
	}

	private int computeSearchTimeListUsers() {
		int searchTime;
		try {
			searchTime = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME));
		} catch (Exception e) {
			searchTime = UserCoreConstants.MAX_SEARCH_TIME;
		}
		return searchTime;
	}

	private int computeGivenMaxListUsers() {
		int givenMax;
		try {
			givenMax = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST));
		} catch (Exception e) {
			givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		}
		return givenMax;
	}

	private void assignTimeout(PreparedStatement prepStmt, int searchTime) {
		try {
			prepStmt.setQueryTimeout(searchTime);
		} catch (Exception e) {
			// this can be ignored since timeout method is not implemented
			LOGGER.debug(e);
		}
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
		LOGGER.warn("doCountUsersWithClaims:" + claimUri + " " + value);
		if (claimUri == null) {
			throw new IllegalArgumentException("Error while getting the claim uri");
		}

		String valueFilter = value;
		if (valueFilter == null) {
			throw new IllegalArgumentException("Error while getting the claim filter");
		}

		String sqlStmt = doCountUsersWithClaimsGetStatement(claimUri);

		if (valueFilter.equals(STAR)) {
			valueFilter = PERCENT;
		} else {
			valueFilter = normalizeFilter(valueFilter);
		}

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

			try (final ResultSet resultSet = prepStmt.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("RESULT");
				} else {
					LOGGER.warn("No result for the filter" + value);
					return 0;
				}
			}

		} catch (SQLException e) {
			String msg = "Error while executing the SQL " + sqlStmt;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg + sqlStmt, e);
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
			String errorMsg = "Error while getting attribute name from " + claimUri;
			throw new UserStoreException(errorMsg, e);
		}
	}

	private String doCountUsersWithClaimsGetStatement(String claimUri) {
		String sqlStmt;
		if (isUserNameClaim(claimUri)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_USERS);

		} else {
			sqlStmt = JDBCRealmConstants.COUNT_USERS_WITH_CLAIM_SQL;
		}
		return sqlStmt;
	}

	/**
	 * Count roles in user stores.
	 *
	 * @param filter the filter for the user name. Use '*' to have all.
	 * @return user count
	 * @throws UserStoreException UserStoreException
	 */
	public long doCountRoles(String filter) throws UserStoreException {
		LOGGER.warn("doCountRoles:" + filter);
		long usersCount = 0;
		String sqlStmt;
		if (filter.startsWith(UserCoreConstants.INTERNAL_DOMAIN)) {
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.COUNT_INTERNAL_ROLES);
			String names[] = filter.split(UserCoreConstants.DOMAIN_SEPARATOR);
			filter = names[1].trim();
		} else {
			sqlStmt = getSqlStatement(filter.startsWith(UserCoreConstants.APPLICATION_DOMAIN),
					JDBCRealmConstants.COUNT_APPLICATION_ROLES, JDBCRealmConstants.COUNT_ROLES);
		}

		if (StringUtils.isNotEmpty(filter)) {
			filter = normalizeFilter(filter);
		} else {
			filter = PERCENT;
		}

		LOGGER.debug("doCountRoles SQL:" + sqlStmt);

		try (Connection dbConnection = getDBConnection();
				PreparedStatement prepStmt = dbConnection.prepareStatement(sqlStmt)) {

			doListUsersPrepareStatement(prepStmt, sqlStmt, filter);

			try (final ResultSet resultSets = prepStmt.executeQuery()) {
				while (resultSets.next()) {
					return resultSets.getLong(1);
				}
			}

		} catch (SQLException e) {
			String msg = ERROR_OCCURRED_WHILE_RETRIEVING_USERS_FOR_FILTER_MESSAGE + filter;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);

		} catch (UserStoreException ex) {
			handleGetUserCountFailure(
					UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_ROLES_COUNT.getCode(),
					String.format(UserCoreErrorConstants.ErrorMessages.ERROR_CODE_ERROR_WHILE_GETTING_ROLES_COUNT
							.getMessage(), ex.getMessage()),
					null, null);
			throw ex;
		}
		return usersCount;
	}

	protected int doGetListUsersCount(String filter) throws UserStoreException {
		LOGGER.warn("doCountRoles:" + filter);
		Connection dbConnection = null;
		String sqlStmt;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		int count = 0;

		try {

			filter = trim(filter, StringUtils.isNotEmpty(filter.trim()), filter.trim().replace(STAR, PERCENT), EXCLAIM,
					UNDERSCORE);

			dbConnection = getDBConnection();

			if (dbConnection == null) {
				throw new UserStoreException(NULL_CONNECTION_MESSAGE);
			}

			sqlStmt = getSqlStatement(isCaseSensitiveUsername(), JDBCRealmConstants.GET_USER_FILTER_PAGINATED_COUNT,
					JDBCCaseInsensitiveConstants.GET_USER_FILTER_CASE_INSENSITIVE_PAGINATED_COUNT);

			prepStmt = dbConnection.prepareStatement(sqlStmt);
			prepStmt.setString(1, filter);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(2, tenantId);
			}

			rs = prepStmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			String msg = "Error occurred while retrieving users count for filter : " + filter;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}
		return count;

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
	@SuppressWarnings("java:S3776") // TODO découper cette méthode trop complexe
	public PaginatedSearchResult getUserListFromProperties(String property, String value, String profileName, int limit,
			int offset) throws UserStoreException {

		PaginatedSearchResult result = new PaginatedSearchResult();

		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}

		if (limit == 0) {
			return result;
		}

		if (value == null) {
			throw new IllegalArgumentException(FILTER_VALUE_CANNOT_BE_NULL);
		}
		if (value.contains(QUERY_FILTER_STRING_ANY)) {
			// This is to support LDAP like queries. Value having only * is
			// restricted
			// except one *.
			if (!value.matches(QUERY_FILTER_STRING_ANY_MATCH_REGEX)) {
				// Convert all the * to % except \*.
				value = value.replaceAll(QUERY_FILTER_STRING_ANY_REPLACE_REGEX, SQL_FILTER_STRING_ANY);
			}
		}

		String[] users = new String[0];
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		List<String> list = new ArrayList<>();
		try {
			dbConnection = getDBConnection();
			String type = DatabaseCreator.getDatabaseType(dbConnection);

			if (offset <= 0) {
				offset = 0;
			} else {
				offset = offset - 1;
			}

			if (ORACLE.equalsIgnoreCase(type)) {
				limit = offset + limit;
			} else if (MSSQL.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = limit + offset;
				limit = initialOffset + 1;
			} else if (DB2.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = offset + limit;
				limit = initialOffset + 1;
			}

			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PAGINATED_USERS_FOR_PROP + "-" + type);
			if (sqlStmt == null) {
				sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PAGINATED_USERS_FOR_PROP);
			}
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			prepStmt.setString(1, property);
			prepStmt.setString(2, value);
			prepStmt.setString(3, profileName);
			if (sqlStmt.contains(UserCoreConstants.UM_TENANT_COLUMN)) {
				prepStmt.setInt(4, tenantId);
				prepStmt.setInt(5, tenantId);
				prepStmt.setInt(6, limit);
				prepStmt.setInt(7, offset);
			} else {
				prepStmt.setInt(4, limit);
				prepStmt.setInt(5, offset);
			}
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				list.add(name);
			}

			if (!list.isEmpty()) {
				users = list.toArray(new String[list.size()]);
			}
			result.setUsers(users);
		} catch (Exception e) {
			String msg = "Database error occurred while paginating users for a property : " + property + " & value : "
					+ value + "& profile name : " + profileName;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}

		if (users.length == 0) {
			result.setSkippedUserCount(getUserListFromPropertiesCount(property, value, profileName));
		}
		return result;
	}

	@SuppressWarnings("java:S3776")
	protected PaginatedSearchResult doGetUserList(Condition condition, String profileName, int limit, int offset,
			String sortBy, String sortOrder) throws UserStoreException {

		boolean isGroupFiltering = false;
		boolean isUsernameFiltering = false;
		boolean isClaimFiltering = false;
		// To identify Mysql multi group filter and multi claim filter.
		int totalMultiGroupFilters = 0;
		int totalMulitClaimFitlers = 0;

		PaginatedSearchResult result = new PaginatedSearchResult();

		if (limit == 0) {
			return result;
		}

		// Since we support only AND operation get expressions as a list.
		List<ExpressionCondition> expressionConditions = new ArrayList<>();
		getExpressionConditions(condition, expressionConditions);

		for (ExpressionCondition expressionCondition : expressionConditions) {
			if (ExpressionAttribute.ROLE.toString().equals(expressionCondition.getAttributeName())) {
				isGroupFiltering = true;
				totalMultiGroupFilters++;
			} else if (ExpressionAttribute.USERNAME.toString().equals(expressionCondition.getAttributeName())) {
				isUsernameFiltering = true;
			} else {
				isClaimFiltering = true;
				totalMulitClaimFitlers++;
			}
		}

		String[] users = new String[0];
		Connection dbConnection = null;
		PreparedStatement prepStmt = null;

		List<String> list = new ArrayList<>();
		try {
			dbConnection = getDBConnection();
			String type = DatabaseCreator.getDatabaseType(dbConnection);

			if (offset <= 0) {
				offset = 0;
			} else {
				offset = offset - 1;
			}

			if (DB2.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = offset + limit;
				limit = initialOffset + 1;
			} else if (ORACLE.equalsIgnoreCase(type)) {
				limit = offset + limit;
			} else if (MSSQL.equalsIgnoreCase(type)) {
				int initialOffset = offset;
				offset = limit + offset;
				limit = initialOffset + 1;
			}

			SqlBuilder sqlBuilder = getQueryString(isGroupFiltering, isUsernameFiltering, isClaimFiltering,
					expressionConditions, limit, offset, sortBy, sortOrder, profileName, type, totalMultiGroupFilters,
					totalMulitClaimFitlers);

			if (MYSQL.equals(type) && totalMultiGroupFilters > 1 && totalMulitClaimFitlers > 1) {
				String fullQuery = sqlBuilder.getQuery();
				String[] splits = fullQuery.split("INTERSECT ");
				int startIndex = 0;
				int endIndex = 0;
				for (String query : splits) {
					List<String> tempUserList = new ArrayList<>();
					int occurance = StringUtils.countMatches(query, QUERY_BINDING_SYMBOL);
					endIndex = endIndex + occurance;
					prepStmt = dbConnection.prepareStatement(query);
					doGetUserListPopulatePrepareStatement(sqlBuilder, prepStmt, startIndex, endIndex);
					try (final ResultSet rs = prepStmt.executeQuery()) {
						while (rs.next()) {
							String name = rs.getString(1);
							tempUserList.add(UserCoreUtil.addDomainToName(name, getMyDomainName()));
						}
					}

					if (startIndex == 0) {
						list = tempUserList;
					} else {
						list.retainAll(tempUserList);
					}
					startIndex += occurance;
				}
			} else {
				prepStmt = dbConnection.prepareStatement(sqlBuilder.getQuery());
				int occurance = StringUtils.countMatches(sqlBuilder.getQuery(), EXCLAIM);
				doGetUserListPopulatePrepareStatement(sqlBuilder, prepStmt, 0, occurance);
				try (final ResultSet rs = prepStmt.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString(1);
						list.add(UserCoreUtil.addDomainToName(name, getMyDomainName()));
					}
				}
			}

			if (!list.isEmpty()) {
				users = list.toArray(new String[list.size()]);
			}
			result.setUsers(users);

		} catch (Exception e) {
			String msg = "Error occur while doGetUserList for multi attribute searching";
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, prepStmt);
		}

		return result;
	}

	private void doGetUserListPopulatePrepareStatement(SqlBuilder sqlBuilder, PreparedStatement prepStmt,
			int startIndex, int endIndex) throws SQLException {

		Map<Integer, Integer> integerParameters = sqlBuilder.getIntegerParameters();
		Map<Integer, String> stringParameters = sqlBuilder.getStringParameters();
		Map<Integer, Long> longParameters = sqlBuilder.getLongParameters();

		for (Map.Entry<Integer, Integer> entry : integerParameters.entrySet()) {
			if (entry.getKey() > startIndex && entry.getKey() <= endIndex) {
				prepStmt.setInt(entry.getKey() - startIndex, entry.getValue());
			}
		}

		for (Map.Entry<Integer, String> entry : stringParameters.entrySet()) {
			if (entry.getKey() > startIndex && entry.getKey() <= endIndex) {
				prepStmt.setString(entry.getKey() - startIndex, entry.getValue());
			}
		}

		for (Map.Entry<Integer, Long> entry : longParameters.entrySet()) {
			if (entry.getKey() > startIndex && entry.getKey() <= endIndex) {
				prepStmt.setLong(entry.getKey() - startIndex, entry.getValue());
			}
		}
	}

	@SuppressWarnings({ "java:S3776", "java:S1192" })
	protected SqlBuilder getQueryString(boolean isGroupFiltering, boolean isUsernameFiltering, boolean isClaimFiltering,
			List<ExpressionCondition> expressionConditions, int limit, int offset, String sortBy, String sortOrder,
			String profileName, String dbType, int totalMultiGroupFilters, int totalMulitClaimFitlers)
			throws UserStoreException {

		StringBuilder sqlStatement;
		SqlBuilder sqlBuilder;
		boolean hitGroupFilter = false;
		boolean hitClaimFilter = false;
		int groupFilterCount = 0;
		int claimFilterCount = 0;

		if (isGroupFiltering && isUsernameFiltering && isClaimFiltering || isGroupFiltering && isClaimFiltering) {

			if (DB2.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT ROW_NUMBER() OVER (ORDER BY "
						+ "UM_USER_NAME) AS rn, p.*  FROM (SELECT DISTINCT UM_USER_NAME "
						+ " FROM UM_ROLE R INNER JOIN " + "UM_USER_ROLE UR ON R.UM_ID = UR.UM_ROLE_ID INNER JOIN "
						+ "UM_USER U ON UR.UM_USER_ID =U.UM_ID "
						+ "INNER JOIN UM_USER_ATTRIBUTE UA ON U.UM_ID = UA.UM_USER_ID");
			} else if (MSSQL.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, ROW_NUMBER() OVER "
						+ "(ORDER BY UM_USER_NAME) AS RowNum FROM (SELECT DISTINCT "
						+ "UM_USER_NAME FROM UM_ROLE R INNER " + "JOIN UM_USER_ROLE UR ON R"
						+ ".UM_ID = UR.UM_ROLE_ID INNER JOIN UM_USER U ON UR.UM_USER_ID =U.UM_ID INNER JOIN "
						+ "UM_USER_ATTRIBUTE UA ON U.UM_ID = UA.UM_USER_ID");
			} else if (ORACLE.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, rownum AS rnum FROM "
						+ "(SELECT  UM_USER_NAME FROM UM_ROLE R INNER JOIN "
						+ "UM_USER_ROLE UR ON R.UM_ID = UR.UM_ROLE_ID "
						+ "INNER JOIN UM_USER U ON UR.UM_USER_ID =U.UM_ID "
						+ "INNER JOIN UM_USER_ATTRIBUTE UA ON U.UM_ID = " + "UA.UM_USER_ID");
			} else {
				sqlStatement = new StringBuilder(
						"SELECT DISTINCT UM_USER_NAME FROM UM_ROLE R INNER JOIN UM_USER_ROLE UR"
								+ " INNER JOIN UM_USER U INNER JOIN UM_USER_ATTRIBUTE "
								+ "UA ON R.UM_ID = UR.UM_ROLE_ID AND UR.UM_USER_ID ="
								+ " U.UM_ID AND U.UM_ID = UA.UM_USER_ID");
			}
			sqlBuilder = new SqlBuilder(sqlStatement).where("R.UM_TENANT_ID = ?", tenantId)
					.where("U.UM_TENANT_ID = ?", tenantId).where("UR.UM_TENANT_ID = ?", tenantId)
					.where("UA.UM_TENANT_ID = ?", tenantId).where("UA.UM_PROFILE_ID = ?", profileName);
		} else if (isGroupFiltering && isUsernameFiltering || isGroupFiltering) {
			if (DB2.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT ROW_NUMBER() OVER (ORDER BY "
						+ "UM_USER_NAME) AS rn, p.*  FROM (SELECT DISTINCT UM_USER_NAME" + " FROM UM_ROLE R INNER JOIN "
						+ "UM_USER_ROLE UR ON R.UM_ID = UR.UM_ROLE_ID INNER JOIN "
						+ "UM_USER U ON UR.UM_USER_ID =U.UM_ID ");
			} else if (MSSQL.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, ROW_NUMBER() OVER "
						+ "(ORDER BY UM_USER_NAME) AS RowNum FROM (SELECT DISTINCT "
						+ "UM_USER_NAME FROM UM_ROLE R INNER "
						+ "JOIN UM_USER_ROLE UR ON R.UM_ID = UR.UM_ROLE_ID INNER JOIN "
						+ "UM_USER U ON UR.UM_USER_ID =U" + ".UM_ID");
			} else if (ORACLE.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, rownum AS rnum FROM "
						+ "(SELECT  UM_USER_NAME FROM UM_ROLE R INNER JOIN "
						+ "UM_USER_ROLE UR ON R.UM_ID = UR.UM_ROLE_ID "
						+ "INNER JOIN UM_USER U ON UR.UM_USER_ID =U.UM_ID");
			} else {
				sqlStatement = new StringBuilder(
						"SELECT DISTINCT UM_USER_NAME FROM UM_ROLE R INNER JOIN UM_USER_ROLE UR"
								+ " INNER JOIN UM_USER U ON R.UM_ID = UR.UM_ROLE_ID AND UR.UM_USER_ID =U.UM_ID");
			}

			sqlBuilder = new SqlBuilder(sqlStatement).where("R.UM_TENANT_ID = ?", tenantId)
					.where("U.UM_TENANT_ID = ?", tenantId).where("UR.UM_TENANT_ID = ?", tenantId);
		} else if (isUsernameFiltering && isClaimFiltering || isClaimFiltering) {
			if (DB2.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT ROW_NUMBER() OVER (ORDER BY "
						+ "UM_USER_NAME) AS rn, p.*  FROM (SELECT DISTINCT UM_USER_NAME"
						+ " FROM  UM_USER U INNER JOIN " + "UM_USER_ATTRIBUTE UA ON U.UM_ID = UA.UM_USER_ID");
			} else if (MSSQL.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, ROW_NUMBER() OVER "
						+ "(ORDER BY UM_USER_NAME) AS RowNum FROM (SELECT DISTINCT UM_USER_NAME "
						+ "FROM UM_USER U INNER JOIN " + "UM_USER_ATTRIBUTE UA ON U.UM_ID = UA.UM_USER_ID");
			} else if (ORACLE.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, rownum AS rnum FROM "
						+ "(SELECT UM_USER_NAME FROM UM_USER U INNER JOIN UM_USER_ATTRIBUTE UA ON U.UM_ID = "
						+ "UA.UM_USER_ID");
			} else {
				sqlStatement = new StringBuilder("SELECT DISTINCT UM_USER_NAME FROM UM_USER U INNER JOIN "
						+ "UM_USER_ATTRIBUTE UA ON U.UM_ID = UA.UM_USER_ID");
			}
			sqlBuilder = new SqlBuilder(sqlStatement).where("U.UM_TENANT_ID = ?", tenantId)
					.where("UA.UM_TENANT_ID = ?", tenantId).where("UA.UM_PROFILE_ID = ?", profileName);
		} else if (isUsernameFiltering) {
			if (DB2.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT ROW_NUMBER() OVER (ORDER BY "
						+ "UM_USER_NAME) AS rn, p.*  FROM (SELECT DISTINCT UM_USER_NAME  FROM UM_USER U");
			} else if (MSSQL.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, "
						+ "ROW_NUMBER() OVER " + "(ORDER BY UM_USER_NAME) AS RowNum "
						+ "FROM (SELECT DISTINCT UM_USER_NAME FROM UM_USER U");
			} else if (ORACLE.equals(dbType)) {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM (SELECT UM_USER_NAME, rownum AS rnum FROM "
						+ "(SELECT UM_USER_NAME FROM UM_USER U");
			} else {
				sqlStatement = new StringBuilder("SELECT UM_USER_NAME FROM UM_USER U");
			}

			sqlBuilder = new SqlBuilder(sqlStatement).where("U.UM_TENANT_ID = ?", tenantId);
		} else {
			throw new UserStoreException("Condition is not valid.");
		}

		SqlBuilder header = new SqlBuilder(new StringBuilder(sqlBuilder.getSql()));
		addingWheres(sqlBuilder, header);

		for (ExpressionCondition expressionCondition : expressionConditions) {
			if (ExpressionAttribute.ROLE.toString().equals(expressionCondition.getAttributeName())) {
				if (!MYSQL.equals(dbType)
						|| (MYSQL.equals(dbType) && totalMultiGroupFilters > 1 && totalMulitClaimFitlers > 1)) {
					multiGroupQueryBuilder(sqlBuilder, header, hitGroupFilter, expressionCondition);
					hitGroupFilter = true;
				} else {
					multiGroupMySqlQueryBuilder(sqlBuilder, groupFilterCount, expressionCondition);
					groupFilterCount++;
				}
			} else if (ExpressionOperation.EQ.toString().equals(expressionCondition.getOperation())
					&& ExpressionAttribute.USERNAME.toString().equals(expressionCondition.getAttributeName())) {
				if (isCaseSensitiveUsername()) {
					sqlBuilder.where("U.UM_USER_NAME = ?", expressionCondition.getAttributeValue());
				} else {
					sqlBuilder.where("U.UM_USER_NAME = LOWER(?)", expressionCondition.getAttributeValue());
				}
			} else if (ExpressionOperation.CO.toString().equals(expressionCondition.getOperation())
					&& ExpressionAttribute.USERNAME.toString().equals(expressionCondition.getAttributeName())) {
				if (isCaseSensitiveUsername()) {
					sqlBuilder.where("U.UM_USER_NAME LIKE ?",
							PERCENT + expressionCondition.getAttributeValue() + PERCENT);
				} else {
					sqlBuilder.where("U.UM_USER_NAME LIKE LOWER(?)",
							PERCENT + expressionCondition.getAttributeValue() + PERCENT);
				}
			} else if (ExpressionOperation.EW.toString().equals(expressionCondition.getOperation())
					&& ExpressionAttribute.USERNAME.toString().equals(expressionCondition.getAttributeName())) {
				if (isCaseSensitiveUsername()) {
					sqlBuilder.where("U.UM_USER_NAME LIKE ?", PERCENT + expressionCondition.getAttributeValue());
				} else {
					sqlBuilder.where("U.UM_USER_NAME LIKE LOWER(?)", PERCENT + expressionCondition.getAttributeValue());
				}
			} else if (ExpressionOperation.SW.toString().equals(expressionCondition.getOperation())
					&& ExpressionAttribute.USERNAME.toString().equals(expressionCondition.getAttributeName())) {
				if (isCaseSensitiveUsername()) {
					sqlBuilder.where("U.UM_USER_NAME LIKE ?", expressionCondition.getAttributeValue() + PERCENT);
				} else {
					sqlBuilder.where("U.UM_USER_NAME LIKE LOWER(?)", expressionCondition.getAttributeValue() + PERCENT);
				}
			} else {
				// Claim filtering
				if (!MYSQL.equals(dbType)
						|| (MYSQL.equals(dbType) && totalMultiGroupFilters > 1 && totalMulitClaimFitlers > 1)) {
					multiClaimQueryBuilder(sqlBuilder, header, hitClaimFilter, expressionCondition);
					hitClaimFilter = true;
				} else {
					multiClaimMySqlQueryBuilder(sqlBuilder, claimFilterCount, expressionCondition);
					claimFilterCount++;
				}
			}
		}

		if (MYSQL.equals(dbType)) {
			sqlBuilder.updateSql(" GROUP BY U.UM_USER_NAME ");
			if (groupFilterCount > 0) {
				sqlBuilder.updateSql(" HAVING COUNT(DISTINCT R.UM_ROLE_NAME) = " + groupFilterCount);
			}
			if (claimFilterCount > 0) {
				sqlBuilder.updateSql(" HAVING COUNT(DISTINCT UA.UM_ATTR_VALUE) = " + claimFilterCount);
			}
		}

		if (!(MYSQL.equals(dbType) && totalMultiGroupFilters > 1 && totalMulitClaimFitlers > 1)) {
			if (DB2.equals(dbType)) {
				sqlBuilder.setTail(") AS p) WHERE rn BETWEEN ? AND ?", limit, offset);
			} else if (MSSQL.equals(dbType)) {
				sqlBuilder.setTail(") AS R) AS P WHERE P.RowNum BETWEEN ? AND ?", limit, offset);
			} else if (ORACLE.equals(dbType)) {
				sqlBuilder.setTail(" ORDER BY UM_USER_NAME) where rownum <= ?) WHERE  rnum > ?", limit, offset);
			} else {
				sqlBuilder.setTail(" ORDER BY UM_USER_NAME ASC LIMIT ? OFFSET ?", limit, offset);
			}
		}
		return sqlBuilder;
	}

	private void multiGroupQueryBuilder(SqlBuilder sqlBuilder, SqlBuilder header, boolean hitFirstRound,
			ExpressionCondition expressionCondition) {

		if (hitFirstRound) {
			sqlBuilder.updateSql(" INTERSECT " + header.getSql());
			addingWheres(header, sqlBuilder);
			buildGroupWhereConditions(sqlBuilder, expressionCondition.getOperation(),
					expressionCondition.getAttributeValue());
		} else {
			buildGroupWhereConditions(sqlBuilder, expressionCondition.getOperation(),
					expressionCondition.getAttributeValue());
		}
	}

	private void buildGroupWhereConditions(SqlBuilder sqlBuilder, String operation, String value) {

		if (ExpressionOperation.EQ.toString().equals(operation)) {
			sqlBuilder.where(R_UM_ROLE_NAME_QUERY, value);
		} else if (ExpressionOperation.EW.toString().equals(operation)) {
			sqlBuilder.where(R_UM_ROLE_NAME_LIKE_QUERY, PERCENT + value);
		} else if (ExpressionOperation.CO.toString().equals(operation)) {
			sqlBuilder.where(R_UM_ROLE_NAME_LIKE_QUERY, PERCENT + value + PERCENT);
		} else if (ExpressionOperation.SW.toString().equals(operation)) {
			sqlBuilder.where(R_UM_ROLE_NAME_LIKE_QUERY, value + PERCENT);
		}
	}

	private void multiGroupMySqlQueryBuilder(SqlBuilder sqlBuilder, int groupFilterCount,
			ExpressionCondition expressionCondition) {

		if (groupFilterCount == 0) {
			buildGroupWhereConditions(sqlBuilder, expressionCondition.getOperation(),
					expressionCondition.getAttributeValue());
		} else {
			buildGroupConditionWithOROperator(sqlBuilder, expressionCondition.getOperation(),
					expressionCondition.getAttributeValue());
		}
	}

	private void buildGroupConditionWithOROperator(SqlBuilder sqlBuilder, String operation, String value) {

		if (ExpressionOperation.EQ.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(R_UM_ROLE_NAME_QUERY, value);
		} else if (ExpressionOperation.EW.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(R_UM_ROLE_NAME_LIKE_QUERY, PERCENT + value);
		} else if (ExpressionOperation.CO.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(R_UM_ROLE_NAME_LIKE_QUERY, PERCENT + value + PERCENT);
		} else if (ExpressionOperation.SW.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(R_UM_ROLE_NAME_LIKE_QUERY, value + PERCENT);
		}
	}

	private void multiClaimQueryBuilder(SqlBuilder sqlBuilder, SqlBuilder header, boolean hitFirstRound,
			ExpressionCondition expressionCondition) {

		if (hitFirstRound) {
			sqlBuilder.updateSql(" INTERSECT " + header.getSql());
			addingWheres(header, sqlBuilder);
			buildClaimWhereConditions(sqlBuilder, expressionCondition.getAttributeName(),
					expressionCondition.getOperation(), expressionCondition.getAttributeValue());
		} else {
			buildClaimWhereConditions(sqlBuilder, expressionCondition.getAttributeName(),
					expressionCondition.getOperation(), expressionCondition.getAttributeValue());
		}
	}

	private void buildClaimWhereConditions(SqlBuilder sqlBuilder, String attributeName, String operation,
			String attributeValue) {

		sqlBuilder.where(UA_UM_ATTR_NAME_QUERY, attributeName);

		if (ExpressionOperation.EQ.toString().equals(operation)) {
			sqlBuilder.where(UA_UM_ATTR_VALUE_QUERY, attributeValue);
		} else if (ExpressionOperation.EW.toString().equals(operation)) {
			sqlBuilder.where(UA_UM_ATTR_VALUE_LIKE_QUERY, PERCENT + attributeValue);
		} else if (ExpressionOperation.CO.toString().equals(operation)) {
			sqlBuilder.where(UA_UM_ATTR_VALUE_LIKE_QUERY, PERCENT + attributeValue + PERCENT);
		} else if (ExpressionOperation.SW.toString().equals(operation)) {
			sqlBuilder.where(UA_UM_ATTR_VALUE_LIKE_QUERY, attributeValue + PERCENT);
		}
	}

	private void multiClaimMySqlQueryBuilder(SqlBuilder sqlBuilder, int claimFilterCount,
			ExpressionCondition expressionCondition) {

		if (claimFilterCount == 0) {
			buildClaimWhereConditions(sqlBuilder, expressionCondition.getAttributeName(),
					expressionCondition.getOperation(), expressionCondition.getAttributeValue());
		} else {
			buildClaimConditionWithOROperator(sqlBuilder, expressionCondition.getAttributeName(),
					expressionCondition.getOperation(), expressionCondition.getAttributeValue());
		}
	}

	private void buildClaimConditionWithOROperator(SqlBuilder sqlBuilder, String attributeName, String operation,
			String attributeValue) {

		sqlBuilder.updateSqlWithOROperation(UA_UM_ATTR_NAME_QUERY, attributeName);

		if (ExpressionOperation.EQ.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(UA_UM_ATTR_VALUE_QUERY, attributeValue);
		} else if (ExpressionOperation.EW.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(UA_UM_ATTR_VALUE_LIKE_QUERY, PERCENT + attributeValue);
		} else if (ExpressionOperation.CO.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(UA_UM_ATTR_VALUE_LIKE_QUERY, PERCENT + attributeValue + PERCENT);
		} else if (ExpressionOperation.SW.toString().equals(operation)) {
			sqlBuilder.updateSqlWithOROperation(UA_UM_ATTR_VALUE_LIKE_QUERY, attributeValue + PERCENT);
		}
	}

	private void addingWheres(SqlBuilder baseSqlBuilder, SqlBuilder newSqlBuilder) {

		for (int i = 0; i < baseSqlBuilder.getWheres().size(); i++) {

			if (baseSqlBuilder.getIntegerParameters().containsKey(i + 1)) {
				newSqlBuilder.where(baseSqlBuilder.getWheres().get(i),
						baseSqlBuilder.getIntegerParameters().get(i + 1));
			} else if (baseSqlBuilder.getStringParameters().containsKey(i + 1)) {
				newSqlBuilder.where(baseSqlBuilder.getWheres().get(i), baseSqlBuilder.getStringParameters().get(i + 1));

			} else if (baseSqlBuilder.getLongParameters().containsKey(i + 1)) {
				newSqlBuilder.where(baseSqlBuilder.getWheres().get(i), baseSqlBuilder.getLongParameters().get(i + 1));
			}
		}
	}

	private void getExpressionConditions(Condition condition, List<ExpressionCondition> expressionConditions) {

		if (condition instanceof ExpressionCondition) {
			expressionConditions.add((ExpressionCondition) condition);
		} else if (condition instanceof OperationalCondition) {
			Condition leftCondition = ((OperationalCondition) condition).getLeftCondition();
			getExpressionConditions(leftCondition, expressionConditions);
			Condition rightCondition = ((OperationalCondition) condition).getRightCondition();
			getExpressionConditions(rightCondition, expressionConditions);
		}
	}

	protected int getUserListFromPropertiesCount(String property, String value, String profileName)
			throws UserStoreException {

		if (profileName == null) {
			profileName = UserCoreConstants.DEFAULT_PROFILE;
		}

		if (value == null) {
			throw new IllegalArgumentException(FILTER_VALUE_CANNOT_BE_NULL);
		}
		// This is to support LDAP like queries. Value having only * is restricted except one *.
		if (value.contains(QUERY_FILTER_STRING_ANY) && !value.matches(QUERY_FILTER_STRING_ANY_MATCH_REGEX)) {
			// Convert all the * to % except \*.
			value = value.replaceAll(QUERY_FILTER_STRING_ANY_REPLACE_REGEX, SQL_FILTER_STRING_ANY);
		}

		int count = 0;
		Connection dbConnection = null;
		String sqlStmt = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		try {
			dbConnection = getDBConnection();
			sqlStmt = realmConfig.getUserStoreProperty(JDBCRealmConstants.GET_PAGINATED_USERS_COUNT_FOR_PROP);
			prepStmt = dbConnection.prepareStatement(sqlStmt);
			getUserListFromPropertiesPrepareStatement(prepStmt, sqlStmt, property, value, profileName);
			rs = prepStmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			String msg = "Database error occurred while paginating users count for a property : " + property + " & "
					+ "value :" + " " + value + "& profile name : " + profileName;
			LOGGER.debug(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
		}

		return count;
	}

	private boolean isCaseSensitiveUsername() {
		String isUsernameCaseInsensitiveString = realmConfig.getUserStoreProperty(CASE_INSENSITIVE_USERNAME);
		return !Boolean.parseBoolean(isUsernameCaseInsensitiveString);
	}

	/**
	 * Get the SQL statement for ExternalRoles.
	 *
	 * @param caseSensitiveUsernameQuery    query for getting role with case sensitive username.
	 * @param nonCaseSensitiveUsernameQuery query for getting role with non-case sensitive username.
	 * @return sql statement.
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

	private int getMaxUserNameListLength() {

		int maxUserList;
		try {
			maxUserList = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST));
		} catch (Exception e) {
			// The user store property might not be configured. Therefore
			// logging as debug.
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Unable to get the " + UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST
						+ " from the realm configuration. The default value: " + UserCoreConstants.MAX_USER_ROLE_LIST
						+ " is used instead.", e);
			}
			maxUserList = UserCoreConstants.MAX_USER_ROLE_LIST;
		}
		return maxUserList;
	}

	private int getSQLQueryTimeoutLimit() {

		int searchTime;
		try {
			searchTime = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME));
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Unable to get the " + UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME
						+ " from the realm configuration. The default value: " + UserCoreConstants.MAX_SEARCH_TIME
						+ " is used instead.", e);
			}
			searchTime = -1;
		}
		LOGGER.debug("SQLQueryTimeout:" + searchTime);

		return searchTime;
	}

	private boolean isUserNameClaim(String claim) {
		return AbstractUserStoreManager.USERNAME_CLAIM_URI.equals(claim);
	}

	@Override
	public boolean isUniqueUserIdEnabled() {
		return false;
	}

	protected HttpClient getHttpClient() {
		if (httpClient == null) {
			Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory) new RudiSSLProtocolSockerFactory(), 443);
			Protocol.registerProtocol("https", easyhttps);

			httpClient = createHttpClient();
		}
		return httpClient;
	}

	protected HttpConnectionManager createConnectionManager() {
		return new MultiThreadedHttpConnectionManager();
	}

	protected HttpClient createHttpClient() {
		HttpClient client = new HttpClient();
		client.setHttpConnectionManager(createConnectionManager());
		return client;
	}

	protected String removeDomainName(String filter) {
		if (filter != null && filter.contains(RUDI_DOMAIN_NAME_SLASH)) {
			filter = filter.replace(RUDI_DOMAIN_NAME_SLASH, "");
		}
		return filter;
	}

}
