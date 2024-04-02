package org.rudi.microservice.konsent.service.consent.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.persistence.NoResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.buckets3.DocumentStorageService;
import org.rudi.facet.crypto.HashUtils;
import org.rudi.facet.generator.docx.DocxGenerator;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.pdf.PDFConvertor;
import org.rudi.facet.generator.pdf.PDFSigner;
import org.rudi.facet.generator.pdf.exception.ConvertorException;
import org.rudi.facet.generator.pdf.exception.SignerException;
import org.rudi.facet.generator.pdf.exception.ValidationException;
import org.rudi.facet.generator.pdf.model.SignatureDescription;
import org.rudi.facet.generator.pdf.model.ValidationResult;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.microservice.konsent.core.bean.Consent;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.core.bean.TreatmentStatus;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.service.consent.ConsentsService;
import org.rudi.microservice.konsent.service.consent.utils.ConsentsUtils;
import org.rudi.microservice.konsent.service.exception.KonsentUnauthorizedException;
import org.rudi.microservice.konsent.service.mapper.consent.ConsentsMapper;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentCustomDao;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentDao;
import org.rudi.microservice.konsent.storage.dao.treatment.TreatmentsCustomDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TreatmentVersionCustomDao;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
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
 * @author FNI18300
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConsentsServiceImpl implements ConsentsService {

	private static final Logger LOGGER_CHECKER = LoggerFactory.getLogger("ConsentsChecker");

	private final ConsentDao consentDao;
	private final ConsentCustomDao consentCustomDao;
	private final ConsentsMapper consentsMapper;
	private final ACLHelper aclHelper;
	private final UtilContextHelper utilContextHelper;
	private final OrganizationHelper organizationHelper;
	private final TreatmentsCustomDao treatmentsCustomDao;
	private final TreatmentVersionCustomDao treatmentVersionCustomDao;
	private final UtilPageable utilPageable;
	private final DocxGenerator docxGenerator;
	private final PDFSigner pdfSigner;
	private final PDFConvertor pdfConvertor;
	private final DocumentStorageService documentStorageService;

	@Value("${rudi.consent.validate.sha.salt}")
	private String consentValidateShaSalt;

	@Value("${rudi.consent.validate.sha.salt}")
	private String consentRevokeShaSalt;

	@Value("${application.role.administrateur.code}")
	private String administrateurCode;

	@Override
	public PagedConsentList searchConsents(ConsentSearchCriteria searchCriteria, Pageable pageable)
			throws AppServiceException {
		if (searchCriteria == null) {
			searchCriteria = new ConsentSearchCriteria();
		}
		val userUuid = aclHelper.getAuthenticatedUserUuid();
		boolean isAdmin = utilContextHelper.hasRole(administrateurCode);

		val myOrganizationsUuids = organizationHelper.getMyOrganizationsUuids(userUuid);
		searchCriteria.setMyOrganizationsUuids(myOrganizationsUuids);

		if (!isAdmin) {
			searchCriteria.setUserUuids(List.of(userUuid));
		}

		val result = consentCustomDao.searchConsents(searchCriteria, pageable);
		return new PagedConsentList().total(result.getTotalElements())
				.elements(consentsMapper.entitiesToDto(result.getContent()));
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Consent createConsent(UUID treatmentVersionUuid) throws AppServiceException {
		val consentor = aclHelper.getAuthenticatedUser(); // Ne peut pas être null

		TreatmentEntity treatmentEntity;
		try {
			treatmentEntity = treatmentsCustomDao.getTreatmentByVersionUuid(treatmentVersionUuid);
		} catch (NoResultException e) {
			throw new AppServiceException(String.format("Aucun traitement ne correspond à la version de traitement: %s",
					treatmentVersionUuid), e);
		}
		val searchCriteria = new TreatmentVersionSearchCriteria().treatmentUuid(treatmentEntity.getUuid())
				.status(TreatmentStatus.VALIDATED);
		val pageable = utilPageable.getPageable(0, 1, "-updatedDate");
		val treatmentVersions = treatmentVersionCustomDao.searchTreatmentVersions(searchCriteria, pageable);
		if (treatmentVersions.isEmpty()) {
			throw new AppServiceUnauthorizedException(String.format(
					"Le traitement %s n'a pas de version valide à laquelle consentir.", treatmentEntity.getUuid()));
		}
		val treatmentVersion = treatmentVersions.getContent().get(0);
		if (!treatmentVersion.getUuid().equals(treatmentVersionUuid)
				|| treatmentVersion.getObsoleteDate().isBefore(OffsetDateTime.now())) { // La version à laquelle on veut consentir n'est pas la dernière validée
			throw new KonsentUnauthorizedException(
					String.format("La version %s est une version obsolète du traitement %s.", treatmentVersionUuid,
							treatmentEntity.getUuid()));
		}

		ConsentEntity consent = null;
		try {
			// creation du consentement
			consent = createConsent(treatmentEntity, treatmentVersion, consentor);

			// génération et conversion en PDFA
			val signedConsent = generateSignedConsent(consent, consentor);

			// Stockage dans S3
			documentStorageService.storeDocument(consent.getStorageKey(), createMetadata(consent, consentor),
					signedConsent);
		} catch (Exception e) {
			throw new AppServiceException("Failed to create consent", e);
		}

		return consentsMapper.entityToDto(consent);
	}

	@Override
	public PagedConsentList searchMyTreatmentsConsents(ConsentSearchCriteria searchCriteria)
			throws AppServiceException {
		if (searchCriteria == null) {
			searchCriteria = new ConsentSearchCriteria();
		}
		val userUuid = aclHelper.getAuthenticatedUserUuid();// ne peut pas être null

		val myOrganizationsUuids = organizationHelper.getMyOrganizationsUuids(userUuid);
		searchCriteria.setMyOrganizationsUuids(myOrganizationsUuids);
		searchCriteria.setUserUuids(List.of(userUuid));
		val result = consentCustomDao.searchConsents(searchCriteria, Pageable.unpaged());
		return new PagedConsentList().total(result.getTotalElements())
				.elements(consentsMapper.entitiesToDto(result.getContent()));
	}

	private DocumentContent generateSignedConsent(ConsentEntity consent, User consentor)
			throws GetOrganizationException, GenerationException, IOException, ConvertorException, ValidationException,
			SignerException {
		// Steps de génération du docx
		// Recup des infos sur le owner du traitement (Objectif : avoir plus d'infos que l'uuid juste)
		val ownerUuid = consent.getTreatment().getOwnerUuid();
		User treatmentUser = null;
		Organization treatmentOrganization = null;
		if (consent.getTreatment().getOwnerType() == OwnerType.USER) {
			treatmentUser = aclHelper.getUserByUUID(ownerUuid);
		} else {
			treatmentOrganization = organizationHelper.getOrganization(ownerUuid);
		}
		// Construction du modèle du docx + génération
		ConsentDataModel userConsent = new ConsentDataModel(consent, Locale.FRENCH, consentor, consent.getTreatment(),
				consent.getTreatmentVersion(), treatmentUser, treatmentOrganization);
		val consentDocx = docxGenerator.generateDocument(userConsent);

		// Conversion en PDFA + Signature
		val pdfaToSign = convert2PDFA(consentDocx);
		return signConsentFile(pdfaToSign);
	}

	private ConsentEntity createConsent(TreatmentEntity treatment, TreatmentVersionEntity treatmentVersion,
			User consentor) throws NoSuchAlgorithmException, JsonProcessingException {
		// Création de l'instance de consentEntity
		ConsentEntity consent = new ConsentEntity();
		consent.setUuid(UUID.randomUUID());
		consent.setOwnerType(OwnerType.USER); // On part du principe que le consentement est personnel
		consent.setOwnerUuid(consentor.getUuid());
		consent.setConsentDate(OffsetDateTime.now());
		consent.setTreatment(treatment);
		consent.setTreatmentVersion(treatmentVersion);

		int daysToExpire = ConsentsUtils.convertRetentionPeriodToDays(treatmentVersion.getRetention());
		consent.setExpirationDate(OffsetDateTime.now().plusDays(daysToExpire));

		consent.setStorageKey(computeStorageKey(treatment, consentor));
		consent.setRevokeHash(null);

		val savedConsent = consentDao.save(consent);
		// Attention cette ligne doit être faite en dernier après la sauvegarde et toutes les affectations pour avoir un hash correct
		savedConsent.setConsentHash(HashUtils.saltSha3(savedConsent, consentValidateShaSalt));
		return savedConsent;
	}

	private String computeStorageKey(TreatmentEntity treatment, User consentor) {
		return "/consents/" + treatment.getOwnerUuid() + "/" + consentor.getUuid() + "/" + consentor.getUuid();
	}

	private Map<String, String> createMetadata(ConsentEntity consent, User consentor) {
		Map<String, String> data = new HashMap<>();
		data.put("consentor", consent.getOwnerUuid().toString());
		data.put("consentorLogin", consentor.getLogin());
		data.put("treatmentVersion", consent.getTreatmentVersion().getUuid().toString());
		return data;
	}

	private DocumentContent convert2PDFA(DocumentContent consentDocx)
			throws ConvertorException, IOException, ValidationException {
		DocumentContent pdf = pdfConvertor.convertDocx2PDF(consentDocx);
		DocumentContent pdfa = pdfConvertor.convertPDF2PDFA(pdf);
		ValidationResult result = pdfConvertor.validatePDFA(pdfa);
		log.debug("Valid: " + result.isValid());
		return pdfa;
	}

	private DocumentContent signConsentFile(DocumentContent documentToSign) throws IOException, SignerException {
		return pdfSigner.sign(documentToSign,
				SignatureDescription.builder().name("Rudi").location("Rennes").reason("Consent").build());
	}

	@Override
	@Transactional(readOnly = false)
	public void revokeConsent(UUID consentUuid) throws AppServiceException {
		ConsentEntity consent = consentDao.findByUuid(consentUuid);
		if (consent == null) {
			throw new AppServiceNotFoundException(ConsentEntity.class, consentUuid);
		}

		try {
			if (!checkConsentHash(consent)) {
				throw new AppServiceBadRequestException("Consent is invalid");
			}
			consent.setExpirationDate(OffsetDateTime.now());
			consent.setRevokeHash(HashUtils.saltSha3(consent, consentRevokeShaSalt));
		} catch (AppServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new AppServiceException("Failed to create revokeHash", e);
		}

		consentDao.save(consent);
	}

	protected boolean checkConsentHash(ConsentEntity consent) throws NoSuchAlgorithmException, JsonProcessingException {
		ConsentEntity clone = new ConsentEntity(consent);
		clone.setConsentHash(null);
		String hash = HashUtils.sha3(clone);
		return hash.equals(consent.getConsentHash());
	}

	protected boolean checkRevokeHash(ConsentEntity consent) throws NoSuchAlgorithmException, JsonProcessingException {
		ConsentEntity clone = new ConsentEntity(consent);
		clone.setRevokeHash(null);
		String hash = HashUtils.sha3(clone);
		return hash.equals(consent.getRevokeHash());
	}

	@Override
	public void checkConsentValidities(List<UUID> consentUuids) {
		if (CollectionUtils.isNotEmpty(consentUuids)) {
			for (UUID uuid : consentUuids) {
				ConsentEntity consent = consentDao.findByUuid(uuid);
				try {
					if (!checkConsentHash(consent)) {
						LOGGER_CHECKER.error("Consent ({}) has invalid hash", uuid);
					}
				} catch (Exception e) {
					log.warn("Failed to check consent " + uuid, e);
					LOGGER_CHECKER.warn("Failed to check consent {}", uuid);
				}
			}
		}
	}

	@Override
	public void checkRevokeValidities(List<UUID> consentUuids) {
		if (CollectionUtils.isNotEmpty(consentUuids)) {
			for (UUID uuid : consentUuids) {
				ConsentEntity consent = consentDao.findByUuid(uuid);

				if (consent != null && consent.getRevokeHash() != null) {
					try {
						if (!checkRevokeHash(consent)) {
							LOGGER_CHECKER.error("Consent ({}) has invalid revoke hash", uuid);
						}
					} catch (Exception e) {
						log.warn("Failed to check consent " + uuid, e);
						LOGGER_CHECKER.warn("Failed to check revoke consent {}", uuid);
					}
				}
			}
		}
	}
}
