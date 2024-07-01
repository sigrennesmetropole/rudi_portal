package org.rudi.microservice.konsent.service.treatment.impl;

import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.crypto.HashUtils;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentList;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentVersionList;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.TreatmentStatus;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.service.exception.KonsentUnauthorizedException;
import org.rudi.microservice.konsent.service.helper.TreatmentHelper;
import org.rudi.microservice.konsent.service.helper.UserHelper;
import org.rudi.microservice.konsent.service.mapper.data.DataManagerMapper;
import org.rudi.microservice.konsent.service.mapper.treatment.TreatmentsMapper;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.TreatmentVersionMapper;
import org.rudi.microservice.konsent.service.treatment.TreatmentsService;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.storage.dao.data.DataManagerDao;
import org.rudi.microservice.konsent.storage.dao.treatment.TreatmentsCustomDao;
import org.rudi.microservice.konsent.storage.dao.treatment.TreatmentsDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TreatmentVersionCustomDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TreatmentVersionDao;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.data.DataManagerEntity;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author KOU21310
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TreatmentsServiceImpl implements TreatmentsService {

	private static final Logger LOGGER_CHECKER = LoggerFactory.getLogger("TreatmentVersionsChecker");

	private final List<CreateTreatmentVersionFieldProcessor> createTreatmentVersionFieldProcessors;
	private final List<UpdateTreatmentVersionFieldProcessor> updateTreatmentVersionFieldProcessors;
	private final TreatmentsDao treatmentsDao;
	private final TreatmentsCustomDao treatmentsCustomDao;
	private final TreatmentVersionDao treatmentVersionDao;
	private final TreatmentVersionCustomDao treatmentVersionCustomDao;
	private final DataManagerDao dataManagerDao;
	private final DataManagerMapper dataManagerMapper;
	private final TreatmentsMapper treatmentsMapper;
	private final TreatmentVersionMapper treatmentVersionMapper;
	private final ACLHelper aclHelper;
	private final OrganizationHelper organizationHelper;
	private final TreatmentUtils treatmentUtils;
	private final UserHelper userHelper;
	private final TreatmentHelper treatmentHelper;

	@Value("${rudi.treatmentversion.publish.sha.salt}")
	private String treatmentVersionPublishShaSalt;


	@Override
	@Transactional(readOnly = false)
	public Treatment createTreatment(Treatment treatment) throws AppServiceException {
		val treatmentEntity = treatmentsMapper.dtoToEntity(treatment);
		treatmentEntity.setUuid(UUID.randomUUID());
		treatmentEntity.setCreationDate(OffsetDateTime.now());
		treatmentEntity.setUpdatedDate(OffsetDateTime.now());
		val savedTreatment = treatmentsDao.save(treatmentEntity);
		// Enregistrement de la version du traitement si présent
		if (treatment.getVersion() == null) {
			return treatmentsMapper.entityToDto(savedTreatment);
		}
		addVersionToTreatmentEntity(treatment.getVersion(), savedTreatment);
		return treatmentsMapper.entityToDto(savedTreatment);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteTreatment(UUID uuid) throws AppServiceException {
		val treatmentEntity = getTreatment(uuid);
		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();
		boolean canDelete = userOwnsTreatment(treatmentEntity, connectedUserUuid)
				|| userHelper.isAuthenticatedUserModuleAdministrator();

		if (!canDelete) {
			throw new AppServiceUnauthorizedException("L'utilisateur n'a pas le droit de supprimer le traitement");
		}

		boolean deletable = true;
		if (treatmentEntity.getVersions() != null) {
			deletable = treatmentEntity.getVersions().stream()
					.noneMatch(treatmentVersion -> treatmentVersionMapper.entityToDto(treatmentVersion).getStatus() == TreatmentStatus.VALIDATED);
		}
		if (!deletable) {
			throw new AppServiceUnauthorizedException(String.format(
					"Le traitement %s ne peut être supprimé car il a une version publiée.", treatmentEntity.getUuid()));
		} else {
			// Version DRAFT associée au traitement à supprimer
			val treatmentVersion = treatmentEntity.getVersions().stream()
					.filter(version -> treatmentVersionMapper.entityToDto(version).getStatus() == TreatmentStatus.DRAFT).findFirst();
			treatmentVersion.ifPresent(treatmentVersionDao::delete);
			treatmentsDao.delete(treatmentEntity);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteTreatmentVersion(UUID treatmentUuid, UUID versionUuid) throws AppServiceException {

		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();
		val treatmentEntity = getTreatment(treatmentUuid);

		boolean userCanDelete = userOwnsTreatment(treatmentEntity, connectedUserUuid)
				|| userHelper.isAuthenticatedUserModuleAdministrator();
		if (!userCanDelete) {
			throw new AppServiceUnauthorizedException("L'utilisateur n'a pas le droit de supprimer cette version");
		}

		TreatmentVersionEntity versionEntity = treatmentEntity.getVersions().stream()
				.filter(version -> version.getUuid().equals(versionUuid))
				.findFirst().orElseThrow(() -> new AppServiceNotFoundException(TreatmentVersionEntity.class, versionUuid));

		if (versionEntity.getStatus() == org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus.DRAFT) {
			treatmentEntity.getVersions().remove(versionEntity);
			treatmentVersionDao.delete(versionEntity);
		} else {
			throw new KonsentUnauthorizedException(
					String.format("La version %s est une version publiée et ne peut donc pas être supprimée.", versionUuid)
			);
		}
	}

	@Override
	public Treatment getTreatment(UUID uuid, Boolean statusIsValidated) throws AppServiceException {

		// Si paramètre pas renseigné, on considère qu'on cherche sans critère de status
		if (statusIsValidated == null) {
			statusIsValidated = false;
		}

		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();
		val treatmentEntity = treatmentsCustomDao.getTreatmentByUuidAndStatus(uuid, statusIsValidated);

		boolean userCanGet = userOwnsTreatment(treatmentEntity, connectedUserUuid)
				|| userHelper.isAuthenticatedUserModuleAdministrator();
		if (!userCanGet) {
			throw new AppServiceUnauthorizedException("L'utilisateur n'a pas le droit de récupérer cette version");
		}

		return treatmentsMapper.entityToDto(treatmentEntity);
	}

	@Override
	public PagedTreatmentVersionList searchTreatmentVersions(TreatmentVersionSearchCriteria searchCriteria,
			Pageable pageable) throws AppServiceException {
		val userUuid = aclHelper.getAuthenticatedUserUuid();
		val treatmentEntity = getTreatment(searchCriteria.getTreatmentUuid());
		if (treatmentUtils.isAllowToDoActionOnTreatment(userUuid, treatmentEntity)) {
			val treatmentVersions = treatmentVersionCustomDao.searchTreatmentVersions(searchCriteria, pageable);
			val treatmentVersionDto = treatmentVersionMapper.entitiesToDto(treatmentVersions, pageable);

			return new PagedTreatmentVersionList().total(treatmentVersionDto.getTotalElements())
					.elements(treatmentVersionDto.getContent());
		} else {
			throw new AppServiceForbiddenException(
					String.format("Utilisateur %s non autorisé à faire cette action sur le traitement %s", userUuid,
							treatmentEntity.getUuid()));
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Treatment publishTreatment(UUID uuid) throws AppServiceException {
		val treatmentEntity = getTreatment(uuid);

		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();
		boolean userCanPublish = userOwnsTreatment(treatmentEntity, connectedUserUuid)
				|| userHelper.isAuthenticatedUserModuleAdministrator();
		if (!userCanPublish) {
			throw new AppServiceUnauthorizedException("L'utilisateur n'a pas le droit de publier le traitement");
		}

		if (treatmentEntity.getVersions() != null) {
			val draftVersion = treatmentEntity.getVersions().stream()
					.filter(treatmentVersion -> treatmentVersionMapper.entityToDto(treatmentVersion).getStatus() == TreatmentStatus.DRAFT)
					.findFirst()
					.orElse(null);
			if (draftVersion != null) {
				draftVersion.setStatus(org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus.VALIDATED);
				draftVersion.setUpdatedDate(OffsetDateTime.now());
				try {
					draftVersion.setTreatmentHash(HashUtils.saltSha3(draftVersion, treatmentVersionPublishShaSalt));
				} catch (Exception e) {
					throw new AppServiceException("Failed to salt treatment " + uuid, e);
				}
				treatmentEntity.setStatus(org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus.VALIDATED);
			} else {
				throw new AppServiceBadRequestException("No draft version for treatment " + uuid);
			}
		}
		return treatmentsMapper.entityToDto(treatmentEntity);
	}

	@Override
	public PagedTreatmentList searchTreatments(TreatmentSearchCriteria searchCriteria, Pageable pageable)
			throws AppServiceException {
		// Enrichir la criteria
		val userUuid = aclHelper.getAuthenticatedUserUuid();
		val myOrganizationsUuids = organizationHelper.getMyOrganizationsUuids(userUuid);
		val userUuids = List.of(userUuid);
		searchCriteria.setMyOrganizationsUuids(myOrganizationsUuids);
		searchCriteria.setUserUuids(userUuids);

		if (CollectionUtils.isEmpty(searchCriteria.getTreatmentStatuses())) {
			searchCriteria.setTreatmentStatuses(List.of(TreatmentStatus.DRAFT, TreatmentStatus.VALIDATED));
		}

		val treatmentEntities = treatmentsCustomDao.searchTreatments(searchCriteria, pageable);
		val treatementDtos = treatmentsMapper.entitiesToDto(treatmentEntities, pageable);
		// Règle fonctionnelle : les traitements retournés ne contionnent pas de version
		treatementDtos.getContent().forEach(treatment -> treatment.setVersion(null));

		return new PagedTreatmentList().total(treatementDtos.getTotalElements()).elements(treatementDtos.getContent());
	}

	@Override
	@Transactional(readOnly = false)
	public Treatment updateTreatment(Treatment treatment) throws AppServiceException {

		// Recherche du traitement
		val existingTreatmentEntity = getTreatment(treatment.getUuid());

		// vérification du droit de MAJ
		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();
		boolean userCanUpdate = userOwnsTreatment(existingTreatmentEntity, connectedUserUuid)
				|| userHelper.isAuthenticatedUserModuleAdministrator();
		if (!userCanUpdate) {
			throw new AppServiceUnauthorizedException("L'utilisateur n'a pas le droit de modifier le traitement");
		}

		// On ne peut MAJ les champs simples, seulement si aucune version déjà publiée
		if (existingTreatmentEntity.getStatus().equals(
				org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus.DRAFT)) {
			// MaJ les champs simples du traitement
			treatmentsMapper.dtoToEntity(treatment, existingTreatmentEntity);
		}

		existingTreatmentEntity.setUpdatedDate(OffsetDateTime.now());

		// Si les versions précédentes étaient toutes publiées déjà, on crée une nouvelle version
		if (treatmentHelper.hasNoDraft(existingTreatmentEntity)) {

			// Notre version courante vaut la version draft rajoutée
			addVersionToTreatmentEntity(treatment.getVersion(), existingTreatmentEntity);

			// MaJ de la version DRAFT présente
		} else {
			val existingVersionEntity = treatmentHelper.findDraftVersion(existingTreatmentEntity);
			// S'assurer que la Version dans le DTO correspond à la Version DRAFT présente dans le traitement à ce moment
			if (treatment.getVersion().getUuid().equals(existingVersionEntity.getUuid())) {
				updateTreatmentVersion(treatment, existingVersionEntity);
			}
		}
		return treatmentsMapper.entityToDto(existingTreatmentEntity);
	}

	private TreatmentEntity getTreatment(UUID treatmentUuid) throws AppServiceNotFoundException,
			AppServiceUnauthorizedException, GetOrganizationMembersException, AppServiceForbiddenException {
		val treatmentEntity = treatmentsDao.findByUuid(treatmentUuid);
		val connectedUserUuid = aclHelper.getAuthenticatedUserUuid();

		if (!treatmentUtils.isAllowToDoActionOnTreatment(connectedUserUuid, treatmentEntity)) {
			throw new AppServiceForbiddenException(
					String.format("Utilisateur %s non autorisé à faire cette action sur le traitement %s",
							connectedUserUuid, treatmentEntity.getUuid()));
		}
		return treatmentEntity;
	}

	private void addVersionToTreatmentEntity(TreatmentVersion treatmentVersion, TreatmentEntity treatmentEntity)
			throws AppServiceException {
		val treatmentVersionEntity = treatmentVersionMapper.dtoToEntity(treatmentVersion);
		// Process des fields du referentiel du treatmentVersion
		for (final CreateTreatmentVersionFieldProcessor treatmentVersionFieldProcessor : createTreatmentVersionFieldProcessors) {
			treatmentVersionFieldProcessor.process(treatmentVersionEntity, null);
		}
		// Enregistrement du manager et du dataOfficer
		createVersionDataManager(treatmentVersionEntity, treatmentVersionEntity.getManager());
		createVersionDataProtectionOfficer(treatmentVersionEntity, treatmentVersionEntity.getDataProtectionOfficer());
		// MaJ des champs remplis par le système
		treatmentVersionEntity.setUuid(UUID.randomUUID());
		treatmentVersionEntity.setCreationDate(OffsetDateTime.now());
		treatmentVersionEntity.setUpdatedDate(OffsetDateTime.now());
		treatmentEntity.getVersions().add(treatmentVersionEntity);
	}

	private void createVersionDataManager(TreatmentVersionEntity treatmentVersionEntity,
			DataManagerEntity dataManager) {
		dataManager.setUuid(UUID.randomUUID());
		val createdDataManger = dataManagerDao.save(dataManager);
		treatmentVersionEntity.setManager(createdDataManger);
	}

	private void createVersionDataProtectionOfficer(TreatmentVersionEntity treatmentVersionEntity,
			DataManagerEntity dataProtectionOfficer) {
		dataProtectionOfficer.setUuid(UUID.randomUUID());
		val createdDataManger = dataManagerDao.save(dataProtectionOfficer);
		treatmentVersionEntity.setDataProtectionOfficer(createdDataManger);
	}

	private void updateTreatmentVersion(Treatment treatment, TreatmentVersionEntity existingVersionEntity)
			throws AppServiceException {
		// MaJ des champs faisant partie du referentiel (les listes)
		for (final UpdateTreatmentVersionFieldProcessor processor : updateTreatmentVersionFieldProcessors) {
			processor.process(treatmentVersionMapper.dtoToEntity(treatment.getVersion()),
					existingVersionEntity);
		}
		// MaJ des champs objets (mais non appartenant au réferentiel)
		// Il faut que les uuids des manager et dataOfficer du DTO correspondent à ceux en BD sinon on a une erreur 409
		if (treatment.getVersion().getManager().getUuid()
				.equals(existingVersionEntity.getManager().getUuid())) {
			dataManagerMapper.dtoToEntity(treatment.getVersion().getManager(),
					existingVersionEntity.getManager());
		}
		if (treatment.getVersion().getDataProtectionOfficer().getUuid()
				.equals(existingVersionEntity.getDataProtectionOfficer().getUuid())) {
			dataManagerMapper.dtoToEntity(treatment.getVersion().getDataProtectionOfficer(),
					existingVersionEntity.getDataProtectionOfficer());
		}
		// MaJ des champs simples de la Version
		treatmentVersionMapper.dtoToEntity(treatment.getVersion(), existingVersionEntity);
		existingVersionEntity.setUpdatedDate(OffsetDateTime.now());
	}

	private boolean userOwnsTreatment(TreatmentEntity treatmentEntity, UUID connectedUserUuid)
			throws GetOrganizationException {
		boolean canDelete = true;
		if (treatmentEntity.getOwnerType().equals(OwnerType.ORGANIZATION)) {
			List<UUID> organizationUuids = organizationHelper.getMyOrganizationsUuids(connectedUserUuid);
			if (!organizationUuids.contains(treatmentEntity.getOwnerUuid())) {
				canDelete = false;
			}
		} else if (treatmentEntity.getOwnerType().equals(OwnerType.USER)
				&& !treatmentEntity.getOwnerUuid().equals(connectedUserUuid)) {
			canDelete = false;
		}
		return canDelete;
	}

	protected boolean checkTreatmentHash(TreatmentVersionEntity treatmentVersion)
			throws NoSuchAlgorithmException, JsonProcessingException {
		TreatmentVersionEntity clone = new TreatmentVersionEntity(treatmentVersion);
		clone.setTreatmentHash(null);
		String hash = HashUtils.saltSha3(clone, treatmentVersionPublishShaSalt);
		return hash.equals(treatmentVersion.getTreatmentHash());
	}

	@Override
	public void checkTreatmentVersionValididies(List<UUID> treatmentVersionUuids) throws AppServiceException {
		if (CollectionUtils.isNotEmpty(treatmentVersionUuids)) {
			for (UUID uuid : treatmentVersionUuids) {
				TreatmentVersionEntity treatmentVersion = treatmentVersionDao.findByUuid(uuid);
				if (treatmentVersionMapper.entityToDto(treatmentVersion).getStatus() == TreatmentStatus.VALIDATED) {
					try {
						if (!checkTreatmentHash(treatmentVersion)) {
							LOGGER_CHECKER.error("Treatment version ({}) has invalid hash", uuid);
						}
					} catch (Exception e) {
						log.warn("Failed to check treatment version " + uuid, e);
						LOGGER_CHECKER.warn("Failed to check treatment version {}", uuid);
					}
				}
			}
		}
	}
}
