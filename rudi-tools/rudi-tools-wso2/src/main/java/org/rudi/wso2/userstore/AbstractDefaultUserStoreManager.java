/**
 * RUDI Portail
 */
package org.rudi.wso2.userstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rudi.wso2.userstore.internal.RudiSSLProtocolSockerFactory;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.user.core.tenant.Tenant;
import org.wso2.carbon.user.core.util.DatabaseUtil;
import org.wso2.carbon.user.core.util.JDBCRealmUtil;
import org.wso2.carbon.utils.dbcreator.DatabaseCreator;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractDefaultUserStoreManager extends AbstractUserStoreManager {

	protected static final String UNDERSCORE = "_";

	protected static final String EXCLAIM = "?";

	protected static final String STAR = "*";

	protected static final String PERCENT = "%";

	protected static final String RUDI_DOMAIN_NAME_SLASH = "RUDI/";

	protected static final String R_UM_ROLE_NAME_QUERY = "R.UM_ROLE_NAME = ?";

	protected static final String R_UM_ROLE_NAME_LIKE_QUERY = "R.UM_ROLE_NAME LIKE ?";

	protected static final String UA_UM_ATTR_NAME_QUERY = "UA.UM_ATTR_NAME = ?";

	protected static final String UA_UM_ATTR_VALUE_QUERY = "UA.UM_ATTR_VALUE = ?";

	protected static final String UA_UM_ATTR_VALUE_LIKE_QUERY = "UA.UM_ATTR_VALUE LIKE ?";

	protected static final String RUDI_STORE_IS_READONLY_MESSAGE = "Rudi Store is readonly";

	protected static final String PROFILE_NAME_MESSAGE = " & profile name : ";

	protected static final String ERROR_OCCURRED_WHILE_RETRIEVING_USERS_FOR_FILTER_MESSAGE = "Error occurred while retrieving users for filter : ";

	protected static final String THE_CAUSE_MIGHT_BE_A_TIME_OUT_HENCE_IGNORED_MESSAGE = "The cause might be a time out. Hence ignored";

	protected static final String NULL_CONNECTION_MESSAGE = "null connection";

	protected static final Log LOGGER = LogFactory.getLog(AbstractDefaultUserStoreManager.class);

	protected static final String QUERY_FILTER_STRING_ANY = STAR;
	protected static final String SQL_FILTER_STRING_ANY = PERCENT;
	protected static final String SQL_FILTER_CHAR_ESCAPE = "\\";
	protected static final String QUERY_BINDING_SYMBOL = EXCLAIM;
	protected static final String CASE_INSENSITIVE_USERNAME = "CaseInsensitiveUsername";

	protected static final String H2 = "h2";
	protected static final String DB2 = "db2";
	protected static final String MSSQL = "mssql";
	protected static final String ORACLE = "oracle";
	protected static final String MYSQL = "mysql";

	protected static final int MAX_ITEM_LIMIT_UNLIMITED = -1;
	protected static final String DO_GET_PASSWORD_EXPIRATION_TIME = "doGetPasswordExpirationTime:";
	protected static final String QUERY_FILTER_STRING_ANY_REPLACE_REGEX = "(?<!\\\\)\\*";

	public static final String FILTER_VALUE_CANNOT_BE_NULL = "Filter value cannot be null";

	public static final String QUERY_FILTER_STRING_ANY_MATCH_REGEX = "(\\*)\\1+";
	protected DataSource jdbcds = null;

	protected Random random = new Random();

	protected int maximumUserNameListLength = -1;

	protected int queryTimeout = -1;

	protected String authenticatorURL = null;

	protected HttpClient httpClient = null;

	/**
	 * 
	 */
	protected AbstractDefaultUserStoreManager() {

	}

	/**
	 */
	protected AbstractDefaultUserStoreManager(RealmConfiguration realmConfig, int tenantId) {
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
	protected AbstractDefaultUserStoreManager(DataSource ds, RealmConfiguration realmConfig, int tenantId,
			boolean addInitData) throws UserStoreException {

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
	protected AbstractDefaultUserStoreManager(DataSource ds, RealmConfiguration realmConfig) {

		this(realmConfig, MultitenantConstants.SUPER_TENANT_ID);
		realmConfig.setUserStoreProperties(JDBCRealmUtil.getSQL(realmConfig.getUserStoreProperties()));
		this.jdbcds = ds;
	}

	/**
	 */
	protected AbstractDefaultUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties,
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
	protected AbstractDefaultUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties,
			ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId,
			boolean skipInitData) throws UserStoreException {
		this(realmConfig, tenantId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Started " + System.currentTimeMillis());
		}
		this.claimManager = claimManager;
		this.userRealm = realm;

		try {
			jdbcds = loadUserStoreSpecificDataSource();

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

	protected DataSource getJDBCDataSource() {
		if (jdbcds == null) {
			jdbcds = loadUserStoreSpecificDataSource();
		}
		return jdbcds;
	}

	protected DataSource loadUserStoreSpecificDataSource() {
		return DatabaseUtil.createUserStoreDataSource(realmConfig);
	}

	@Override
	public boolean isReadOnly() throws UserStoreException {
		return true;
	}

	public Map<String, String> getProperties(Tenant tenant) throws UserStoreException {
		return this.realmConfig.getUserStoreProperties();
	}

	protected int getMaxUserNameListLength() {

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

	protected int getSQLQueryTimeoutLimit() {

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

	@Override
	public RealmConfiguration getRealmConfiguration() {
		return this.realmConfig;
	}

	@Override
	public boolean isBulkImportSupported() throws UserStoreException {
		return false;
	}

	@Override
	public void addRememberMe(String userName, String token) throws org.wso2.carbon.user.api.UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	public boolean isValidRememberMeToken(String userName, String token)
			throws org.wso2.carbon.user.api.UserStoreException {
		return false;
	}

	@Override
	protected void doUpdateCredential(String userName, Object newCredential, Object oldCredential)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doUpdateCredentialByAdmin(String userName, Object newCredential) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doDeleteUser(String userName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doDeleteUserClaimValue(String userName, String claimURI, String profileName)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doAddUser(String userName, Object credential, String[] roleList, Map<String, String> claims,
			String profileName, boolean requirePasswordChange) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	protected void doDeleteUserClaimValues(String userName, String[] claims, String profileName)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);
	}

	@Override
	protected void doUpdateUserListOfRole(String roleName, String[] deletedUsers, String[] newUsers)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doUpdateRoleListOfUser(String userName, String[] deletedRoles, String[] newRoles)
			throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doAddRole(String roleName, String[] userList, boolean shared) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doDeleteRole(String roleName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

	}

	@Override
	protected void doUpdateRoleName(String roleName, String newRoleName) throws UserStoreException {
		throw new UnsupportedOperationException(RUDI_STORE_IS_READONLY_MESSAGE);

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

	protected Connection getDBConnection() throws SQLException {
		@SuppressWarnings({ "unused", "java:S2095" }) // La connexion est fermée par les méthodes appelantes via DatabaseUtil.closeAllConnections
		Connection dbConnection = getJDBCDataSource().getConnection();
		dbConnection.setAutoCommit(false);
		return dbConnection;
	}

	protected boolean isCaseSensitiveUsername() {
		String isUsernameCaseInsensitiveString = realmConfig.getUserStoreProperty(CASE_INSENSITIVE_USERNAME);
		return !Boolean.parseBoolean(isUsernameCaseInsensitiveString);
	}

	protected void setQueryTimeout(PreparedStatement prepStmt, int searchTime) {
		try {
			prepStmt.setQueryTimeout(searchTime);
		} catch (Exception e) {
			// this can be ignored since timeout method is not implemented
			LOGGER.debug(e);
		}
	}

	protected int getGivenMaxUser() {
		int givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		try {
			givenMax = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_USER_LIST));
		} catch (Exception e) {
			givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		}
		return givenMax;
	}

	protected int getGivenMaxRole() {
		int givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		try {
			givenMax = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_ROLE_LIST));
		} catch (Exception e) {
			givenMax = UserCoreConstants.MAX_USER_ROLE_LIST;
		}
		return givenMax;
	}

	protected int getSearchTime() {
		int searchTime;
		try {
			searchTime = Integer
					.parseInt(realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_MAX_SEARCH_TIME));
		} catch (Exception e) {
			searchTime = UserCoreConstants.MAX_SEARCH_TIME;
		}
		return searchTime;
	}

	/**
	 * Check if the DB is H2.
	 *
	 * @return true if H2, false otherwise.
	 * @throws Exception if error occurred while getting database type.
	 */
	protected boolean isH2DB(Connection dbConnection) throws Exception {
		return H2.equalsIgnoreCase(DatabaseCreator.getDatabaseType(dbConnection));
	}

	protected boolean isUserNameClaim(String claim) {
		return AbstractUserStoreManager.USERNAME_CLAIM_URI.equals(claim);
	}
}
