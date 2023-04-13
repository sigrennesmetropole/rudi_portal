package org.rudi.common.core.security;

/**
 * Valeurs possibles pour le rôle d'un utilisateur.
 * Cf. org.rudi.microservice.acl.storage.entity.role.RoleEntity.
 * On ne peut pas utiliser un Enum car les annotations Java n'acceptent que des constantes.
 */
public final class RoleCodes {
	private RoleCodes() {
	}

	public static final String ADMINISTRATOR = "ADMINISTRATOR";
	public static final String ANONYMOUS = "ANONYMOUS";
	public static final String MODERATOR = "MODERATOR";
	public static final String MODULE = "MODULE";
	public static final String MODULE_ACL_ADMINISTRATOR = "MODULE_ACL_ADMINISTRATOR";
	public static final String MODULE_KALIM = "MODULE_KALIM";
	public static final String MODULE_KALIM_ADMINISTRATOR = "MODULE_KALIM_ADMINISTRATOR";
	public static final String MODULE_KONSULT = "MODULE_KONSULT";
	public static final String MODULE_KONSULT_ADMINISTRATOR = "MODULE_KONSULT_ADMINISTRATOR";
	public static final String MODULE_KOS_ADMINISTRATOR = "MODULE_KOS_ADMINISTRATOR";
	public static final String MODULE_PROJEKT = "MODULE_PROJEKT";
	public static final String MODULE_PROJEKT_ADMINISTRATOR = "MODULE_PROJEKT_ADMINISTRATOR";
	public static final String MODULE_SELFDATA_ADMINISTRATOR = "MODULE_SELFDATA_ADMINISTRATOR";
	public static final String MODULE_STRUKTURE = "MODULE_STRUKTURE";
	public static final String MODULE_KONSENT = "MODULE_KONSENT";
	public static final String MODULE_KONSENT_ADMINISTRATOR = "MODULE_KONSENT_ADMINISTRATOR";
	public static final String MODULE_STRUKTURE_ADMINISTRATOR = "MODULE_STRUKTURE_ADMINISTRATOR";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String PROJECT_MANAGER = "PROJECT_MANAGER";
	public static final String PROVIDER = "PROVIDER";
	public static final String USER = "USER";
}
