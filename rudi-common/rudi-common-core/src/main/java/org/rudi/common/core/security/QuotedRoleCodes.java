package org.rudi.common.core.security;

/**
 * {@link RoleCodes} avec des quotes, utilisables dans une expression EL (exemple : annotation @PreAuthorize("hasAnyRole(...)").
 */
public final class QuotedRoleCodes {
	private QuotedRoleCodes() {
	}

	public static final String ADMINISTRATOR = "'" + RoleCodes.ADMINISTRATOR + "'";
	public static final String ANONYMOUS = "'" + RoleCodes.ANONYMOUS + "'";
	public static final String MODERATOR = "'" + RoleCodes.MODERATOR + "'";
	public static final String MODULE = "'" + RoleCodes.MODULE + "'";
	public static final String MODULE_ACL_ADMINISTRATOR = "'" + RoleCodes.MODULE_ACL_ADMINISTRATOR + "'";
	public static final String MODULE_KALIM = "'" + RoleCodes.MODULE_KALIM + "'";
	public static final String MODULE_KALIM_ADMINISTRATOR = "'" + RoleCodes.MODULE_KALIM_ADMINISTRATOR + "'";
	public static final String MODULE_KONSULT = "'" + RoleCodes.MODULE_KONSULT + "'";
	public static final String MODULE_KONSULT_ADMINISTRATOR = "'" + RoleCodes.MODULE_KONSULT_ADMINISTRATOR + "'";
	public static final String MODULE_KOS_ADMINISTRATOR = "'" + RoleCodes.MODULE_KOS_ADMINISTRATOR + "'";
	public static final String MODULE_PROJEKT = "'" + RoleCodes.MODULE_PROJEKT + "'";
	public static final String MODULE_PROJEKT_ADMINISTRATOR = "'" + RoleCodes.MODULE_PROJEKT_ADMINISTRATOR + "'";
	public static final String MODULE_SELFDATA_ADMINISTRATOR = "'" + RoleCodes.MODULE_SELFDATA_ADMINISTRATOR + "'";
	public static final String MODULE_STRUKTURE = "'" + RoleCodes.MODULE_STRUKTURE + "'";
	public static final String MODULE_STRUKTURE_ADMINISTRATOR = "'" + RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR + "'";
	public static final String MODULE_KONSENT = "'" + RoleCodes.MODULE_KONSENT + "'";
	public static final String MODULE_KONSENT_ADMINISTRATOR = "'" + RoleCodes.MODULE_KONSENT_ADMINISTRATOR + "'";
	public static final String ORGANIZATION = "'" + RoleCodes.ORGANIZATION + "'";
	public static final String PROJECT_MANAGER = "'" + RoleCodes.PROJECT_MANAGER + "'";
	public static final String PROVIDER = "'" + RoleCodes.PROVIDER + "'";
	public static final String USER = "'" + RoleCodes.USER + "'";

}
