/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.rudi.common.core.security.RoleCodes;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class ProjektAuthorisationHelper {

	private static final Boolean DEFAULT_ACCESS_GRANT = Boolean.FALSE;

	private final ACLHelper aclHelper;
	private final OrganizationHelper organizationHelper;
	private final DatasetService datasetService;
	private final MyInformationsHelper myInformationsHelper;

	/**
	 * Map des droits d'accès pour ouvrir l'accès à l'administrateur et au profil technique du module
	 */
	@Getter
	private static final Map<String, Boolean> ADMINISTRATOR_ACCESS = new HashMap<>();
	static {
		ADMINISTRATOR_ACCESS.put(RoleCodes.ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_ACCESS.put(RoleCodes.MODULE_PROJEKT_ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_ACCESS.put(RoleCodes.MODULE_PROJEKT, Boolean.TRUE);
	}

	/**
	 * Map des droits d'accès pour ouvrir l'accès à l'administrateur, au modérateur et au profil technique du module
	 */
	@Getter
	private static final Map<String, Boolean> ADMINISTRATOR_MODERATOR_ACCESS = new HashMap<>();
	static {
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.MODULE_PROJEKT_ADMINISTRATOR, Boolean.TRUE);
		ADMINISTRATOR_MODERATOR_ACCESS.put(RoleCodes.MODULE_PROJEKT, Boolean.TRUE);
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
					.map(Entry::getKey).collect(Collectors.toList());

			// restreindre cette liste aux rôles définis comme "techniques" ou "transverses"
			grantedRoles.retainAll(CollectionUtils.union(TECHNICAL_ROLES, TRANSVERSAL_ROLES));

			// vérifier que l'utilisateur a bien l'un des rôles autorisés
			return Boolean.valueOf(hasAnyRole(user, grantedRoles));
		} catch (AppServiceUnauthorizedException e) {
			return accessRights.getOrDefault(NOT_USER, DEFAULT_ACCESS_GRANT);
		}
	}

	/**
	 * Vérifie si l'utilisateur connecté a le droit d'accéder à l'entité projet : l'utilisateur doit avoir le rôle "PROJECT_MANAGER" ou "USER" et être
	 * owner ou membre de l'organisation owner
	 * 
	 * @param projectEntity le projet
	 * @return true si l'utilisateur a accès
	 * @throws GetOrganizationMembersException en cas de problème avec la récupération des membres de l'organisation
	 * @throws MissingParameterException       en cas d'inforamtion manquante sur le projet
	 */
	public Boolean isAccessGrantedForUserOnProject(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, MissingParameterException {

		try {
			User user = aclHelper.getAuthenticatedUser();

			if (projectEntity != null && hasAnyRole(user, Arrays.asList(RoleCodes.PROJECT_MANAGER, RoleCodes.USER))) {
				// Vérification comme dans le ownerprocessor
				val ownerUuid = projectEntity.getOwnerUuid();

				if (ownerUuid == null) {
					throw new MissingParameterException("owner_uuid manquant");
				}

				switch (projectEntity.getOwnerType()) {
				case USER:
					return user.getUuid() != null && ownerUuid.equals(user.getUuid());

				case ORGANIZATION:
					return user.getUuid() != null
							&& organizationHelper.organizationContainsUser(ownerUuid, user.getUuid());

				default:
					break;
				}
			}

			return DEFAULT_ACCESS_GRANT;
		} catch (AppServiceUnauthorizedException e) {
			return DEFAULT_ACCESS_GRANT;
		}
	}

	/**
	 * Vérifie si l'utilisateur connecté a le droit d'accéder à l'entité linkeddataset : l'utilisateur doit avoir le rôle "USER" et être owner ou membre
	 * de l'organisation owner du JDD
	 * 
	 * @param linkedDatasetEntity le JDD
	 * @return si l'utilisateur a accès
	 * @throws GetOrganizationException en cas de problème avec la récupération des membres de l'organisation
	 */
	public Boolean isAccessGrantedForUserOnLinkedDataset(LinkedDatasetEntity linkedDatasetEntity)
			throws GetOrganizationException {
		try {
			User user = aclHelper.getAuthenticatedUser();

			if (linkedDatasetEntity != null && hasAnyRole(user, Arrays.asList(RoleCodes.USER))) {
				var metadata = datasetService.getDataset(linkedDatasetEntity.getDatasetUuid());
				var organization = metadata.getProducer();
				List<UUID> userAndItsOrganizationUuid = myInformationsHelper.getMeAndMyOrganizationUuids();
				// retourne true si l'utilisateur est provider et que le dataset appartient à lui ou une de ses organisations
				return IterableUtils.contains(userAndItsOrganizationUuid, organization.getOrganizationId());
			}
			return DEFAULT_ACCESS_GRANT;
		} catch (AppServiceUnauthorizedException | DataverseAPIException e) {
			// utilisateur non connecté || dataset non trouvé
			return DEFAULT_ACCESS_GRANT;
		}
	}

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
	 * Définition de l'ouverture des droits la fonctionnalité de création de projet ou d'administration des new dataset request associé : Le projectowner
	 * ou un membre de l'organisation peut / L'administrateur peut (uniquement via Postman) / Un autre user ne peut pas
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	public void checkRightsInitProject(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, AppServiceUnauthorizedException, MissingParameterException {
		Map<String, Boolean> accessRightsByRole = ProjektAuthorisationHelper.getADMINISTRATOR_ACCESS();
		// Vérification des droits d'accès
		// les droits autorisés dans accessRights doivent être cohérents avec ceux définis en PreAuth coté Controller
		if (!(isAccessGrantedByRole(accessRightsByRole) || isAccessGrantedForUserOnProject(projectEntity))) {
			throw new AppServiceUnauthorizedException("Accès non autorisé à la fonctionnalité pour l'utilisateur");
		}
	}

	/**
	 * Définition de l'ouverture des droits la fonctionnalité de d'administration des new dataset request associé : Le projectowner ou un membre de
	 * l'organisation peut / L'administrateur peut (uniquement via Postman) / Un autre user ne peut pas
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	public void checkRightsAdministerProjectDataset(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, AppServiceUnauthorizedException, MissingParameterException {
		// pour le moment les droits d'accès à cette fonction sont les mêmes que la fonction de création de projet
		checkRightsInitProject(projectEntity);
	}

	public void checkStatusForProjectModification(ProjectEntity existingProject) throws AppServiceException {
		if (existingProject != null) {

			switch (existingProject.getProjectStatus()) {
			case DRAFT:
			case REJECTED:
				// l'opération d'ajout ou de suppression de dataset est autorisée
				break;
			case VALIDATED:
				if (!existingProject.getReutilisationStatus().isDatasetSetModificationAllowed()) {
					throw new AppServiceForbiddenException(
							String.format("Cannot modify linkeddataset in project status %s",
									existingProject.getReutilisationStatus().getCode()));
				}
				break;
			case IN_PROGRESS:
			case CANCELLED:
			case DISENGAGED:
			default:
				throw new AppServiceForbiddenException(String.format("Cannot modify linkeddataset in project status %s",
						existingProject.getProjectStatus()));

			}
		}

	}

	/**
	 * Définition de l'ouverture des droits la fonctionnalité d'administration de projet : Le projectowner ou un membre de l'organisation peut modifier un
	 * projet / L'administrateur peut modifier un projet (uniquement via Postman) / L'animateur peut modifier un projet (via postman) / Un autre user ne
	 * peut pas modifier un projet
	 * 
	 * Les droits autorisés doivent être cohérents avec ceux définis en PreAuth coté Controller
	 * 
	 * @param projectEntity l'entité projet pour laquelle vérifier le droit d'accès
	 * @throws GetOrganizationMembersException
	 * @throws GetOrganizationException
	 * @throws AppServiceUnauthorizedException
	 * @throws MissingParameterException
	 */
	public void checkRightsAdministerProject(ProjectEntity projectEntity)
			throws GetOrganizationMembersException, AppServiceUnauthorizedException, MissingParameterException {
		Map<String, Boolean> accessRightsByRole = ProjektAuthorisationHelper.getADMINISTRATOR_MODERATOR_ACCESS();
		// Vérification des droits d'accès
		// les droits autorisés dans accessRights doivent être cohérents avec ceux définis en PreAuth coté Controller
		if (!(isAccessGrantedByRole(accessRightsByRole)
				|| isAccessGrantedForUserOnProject(projectEntity))) {
			throw new AppServiceUnauthorizedException("Accès non autorisé à la fonctionnalité pour l'utilisateur");
		}
	}
}
