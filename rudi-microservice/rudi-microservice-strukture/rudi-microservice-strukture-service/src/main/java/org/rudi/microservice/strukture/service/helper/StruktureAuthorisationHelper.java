package org.rudi.microservice.strukture.service.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.rudi.common.core.security.RoleCodes;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StruktureAuthorisationHelper {
	private static final Boolean DEFAULT_ACCESS_GRANT = Boolean.FALSE;

	private final ACLHelper aclHelper;
	private final OrganizationMembersHelper organizationMembersHelper;

	/**
	 * Map des droits d'accès pour ouvrir l'accès à l'administrateur et au profil technique du module
	 */
	@Getter
	private static final Map<String, Boolean> ADMINISTRATOR_ACCESS = new HashMap<>();
	static {
		ADMINISTRATOR_ACCESS.put(RoleCodes.ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_ACCESS.put(RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_ACCESS.put(RoleCodes.MODULE_STRUKTURE, Boolean.TRUE);
	}

	/**
	 * Map des droits d'accès pour ouvrir l'accès à l'administrateur, au modérateur et au profil technique du module
	 */
	@Getter
	private static final Map<String, Boolean> ADMINISTRATOR_MODERATOR_ACCESS = new HashMap<>();
	static {
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.MODULE_STRUKTURE, Boolean.TRUE);
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.MODERATOR, Boolean.TRUE);
	}

	public static final String NOT_USER = "NOT_USER";

	/**
	 * Liste des code de rôles dits "techniques"
	 */
	private static final List<String> TECHNICAL_ROLES = Arrays.asList(RoleCodes.MODULE,
			RoleCodes.MODULE_ACL_ADMINISTRATOR, RoleCodes.MODULE_KALIM, RoleCodes.MODULE_KALIM_ADMINISTRATOR,
			RoleCodes.MODULE_KONSULT, RoleCodes.MODULE_KONSULT_ADMINISTRATOR, RoleCodes.MODULE_KOS_ADMINISTRATOR,
			RoleCodes.MODULE_PROJEKT, RoleCodes.MODULE_PROJEKT_ADMINISTRATOR, RoleCodes.MODULE_SELFDATA_ADMINISTRATOR,
			RoleCodes.MODULE_STRUKTURE, RoleCodes.MODULE_SELFDATA, RoleCodes.MODULE_KONSENT,
			RoleCodes.MODULE_KONSENT_ADMINISTRATOR, RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR);

	/**
	 * Liste des code de rôles dits "transverses" (rôles fonctionnels)
	 */
	private static final List<String> TRANSVERSAL_ROLES = Arrays.asList(RoleCodes.ADMINISTRATOR, RoleCodes.ANONYMOUS,
			RoleCodes.MODERATOR, NOT_USER);


	private boolean hasAnyRole(User user, List<String> acceptedRoles) throws AppServiceUnauthorizedException {
		if (user == null) { // NOSONAR il faut pouvoir traiter le cas où le user n'est pas positionné (pour les TU par exemple)
			throw new AppServiceUnauthorizedException("No user");
		}
		if (user.getRoles() == null) {
			throw new AppServiceUnauthorizedException("Role list for user is null");
		}
		List<String> userRoles = user.getRoles().stream().map(Role::getCode).collect(Collectors.toList());
		return CollectionUtils.isNotEmpty(CollectionUtils.intersection(userRoles, acceptedRoles));
	}


	/**
	 * Vérifie les droits d'accès de l'utilisateur: possède-t-il l'un des roles techniques ou transverses définis dans la map ?
	 *
	 * @param accessRights la map des droits d'accès : code du rôle -> droit d'accès
	 * @return true si l'utilisateur a l'un des roles autorisés
	 */
	public Boolean isAccessGrantedByRole(Map<String, Boolean> accessRights) {
		try {
			User user = aclHelper.getAuthenticatedUser();
			// identifier tous les roles autorisés dans la map des droits d'accès
			List<String> grantedRoles = accessRights.entrySet().stream().filter(a -> BooleanUtils.isTrue(a.getValue()))
					.map(Map.Entry::getKey).collect(Collectors.toList());

			// restreindre cette liste aux rôles définis comme "techniques" ou "transverses"
			grantedRoles.retainAll(CollectionUtils.union(TECHNICAL_ROLES, TRANSVERSAL_ROLES));

			// vérifier que l'utilisateur a bien l'un des rôles autorisés
			return Boolean.valueOf(hasAnyRole(user, grantedRoles));
		} catch (AppServiceUnauthorizedException e) {
			return accessRights.getOrDefault(NOT_USER, DEFAULT_ACCESS_GRANT);
		}
	}

	/**
	 * Vérifie si l'utilisateur connecté a le droit d'accéder à l'entité organization : l'utilisateur doit avoir le rôle "USER" et être owner ou membre de l'organisation
	 * @param organizationUuid uuid de l'organization concernée
	 * @return true si l'utilisateur a accès
	 */
	public Boolean isAccessGrantedForUserOnOrganization(UUID organizationUuid){
		try {
			User user = aclHelper.getAuthenticatedUser();
			if(organizationUuid != null && hasAnyRole(user, Arrays.asList(RoleCodes.USER))) {
				return user.getUuid() != null
						&& organizationMembersHelper.isAuthenticatedUserOrganizationMember(organizationUuid);

			}
			return DEFAULT_ACCESS_GRANT;
		} catch (AppServiceException e) {
			return DEFAULT_ACCESS_GRANT;
		}
	}
}
