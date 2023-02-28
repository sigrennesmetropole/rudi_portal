package org.rudi.common.core.security;

import java.util.Arrays;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

/**
 * Valeurs possibles pour le rôle d'un utilisateur.
 * Cf. org.rudi.microservice.acl.storage.entity.role.RoleEntity.
 */
@RequiredArgsConstructor
public enum Role {
	ADMINISTRATOR(RoleCodes.ADMINISTRATOR),
	ANONYMOUS(RoleCodes.ANONYMOUS),
	MODERATOR(RoleCodes.MODERATOR),
	MODULE(RoleCodes.MODULE),
	MODULE_ACL_ADMINISTRATOR(RoleCodes.MODULE_ACL_ADMINISTRATOR),
	MODULE_KALIM(RoleCodes.MODULE_KALIM),
	MODULE_KALIM_ADMINISTRATOR(RoleCodes.MODULE_KALIM_ADMINISTRATOR),
	MODULE_KONSULT(RoleCodes.MODULE_KONSULT),
	MODULE_KONSULT_ADMINISTRATOR(RoleCodes.MODULE_KONSULT_ADMINISTRATOR),
	MODULE_KOS_ADMINISTRATOR(RoleCodes.MODULE_KOS_ADMINISTRATOR),
	MODULE_PROJEKT(RoleCodes.MODULE_PROJEKT),
	MODULE_PROJEKT_ADMINISTRATOR(RoleCodes.MODULE_PROJEKT_ADMINISTRATOR),
	MODULE_STRUKTURE(RoleCodes.MODULE_STRUKTURE),
	MODULE_STRUKTURE_ADMINISTRATOR(RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR),
	ORGANIZATION(RoleCodes.ORGANIZATION),
	PROJECT_MANAGER(RoleCodes.PROJECT_MANAGER),
	PROVIDER(RoleCodes.PROVIDER),
	USER(RoleCodes.USER);

	@Nonnull
	private final String code;

	public static boolean containsRoleWithCode(Role[] roles, String code) {
		return Arrays.stream(roles).anyMatch(role -> role.hasCode(code));
	}

	private boolean hasCode(String code) {
		return this.code.equals(code);
	}
}
