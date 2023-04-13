package org.rudi.microservice.kalim.service.integration.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.core.bean.Report;
import org.rudi.microservice.kalim.core.bean.ReportError;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.helper.provider.KalimProviderHelper;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.rudi.microservice.kalim.service.integration.impl.handlers.DeleteIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.mapper.IntegrationRequestMapper;
import org.rudi.microservice.kalim.service.mapper.ReportErrorMapper;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestCustomDao;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestDao;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Service de gestion des IntegrationRequest
 */
@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class IntegrationRequestServiceImpl implements IntegrationRequestService {

	private final ObjectMapper objectMapper;

	@Value("${rudi.kalim.sendReport.retry:5}")
	private int sendReportRetry;

	@Value("${rudi.kalim.sendReport.period:60}")
	private int sendReportPeriod;

	private final IntegrationRequestMapper integrationRequestMapper;
	private final ReportErrorMapper reportErrorMapper;
	private final IntegrationRequestDao integrationRequestDao;
	private final IntegrationRequestCustomDao integrationRequestCustomDao;
	private final KalimProviderHelper kalimProviderHelper;
	private final ProviderHelper providerHelper;
	protected final UtilContextHelper utilContextHelper;
	private final PostIntegrationRequestTreatmentHandler postIntegrationRequestHandler;
	private final PutIntegrationRequestTreatmentHandler putIntegrationRequestHandler;
	private final DeleteIntegrationRequestTreatmentHandler deleteIntegrationRequestHandler;
	private final DatasetService datasetService;

	@Override
	public Page<IntegrationRequest> searchIntegrationRequests(IntegrationRequestSearchCriteria searchCriteria,
			Pageable pageable) {
		return integrationRequestMapper.entitiesToDto(
				integrationRequestCustomDao.searchIntegrationRequests(searchCriteria, pageable), pageable);
	}

	@Override
	@Transactional // readOnly = false
	public IntegrationRequest createIntegrationRequest(Metadata metadata, Method method)
			throws AppServiceUnauthorizedException, IntegrationException {
		final NodeProvider nodeProvider = kalimProviderHelper.getAuthenticatedNodeProvider();
		checkAuthenticatedNodeProviderIsNotNull(nodeProvider);
		return createIntegrationRequest(metadata, method, nodeProvider, false);
	}

	private void checkAuthenticatedNodeProviderIsNotNull(NodeProvider authenticatedNodeProvider) throws AppServiceUnauthorizedException {
		if (authenticatedNodeProvider == null) {
			throw new AppServiceUnauthorizedException("Invalid node provider authentication");
		}
	}

	@Override
	@Transactional // readOnly = false
	public IntegrationRequest createDeleteIntegrationRequestFromGlobalId(UUID globalId) throws DataverseAPIException, AppServiceException, IntegrationException {
		final var nodeProvider = kalimProviderHelper.getAuthenticatedNodeProvider();
		checkAuthenticatedNodeProviderIsNotNull(nodeProvider);

		final Metadata metadata;
		try {
			metadata = datasetService.getDataset(globalId);
		} catch (DatasetNotFoundException e) {
			throw new AppServiceNotFoundException(Metadata.class, globalId);
		}

		return createIntegrationRequest(metadata, Method.DELETE, nodeProvider, false);
	}

	@Override
	@Transactional // readOnly = false
	public IntegrationRequest createIntegrationRequestFromHarvesting(Metadata metadata, Method method, NodeProvider nodeProvider) throws IntegrationException {
		return createIntegrationRequest(metadata, method, nodeProvider, true);
	}

	private IntegrationRequest createIntegrationRequest(Metadata metadata, Method method, NodeProvider nodeProvider, boolean fromHarvesting)
			throws IntegrationException {

		if (metadata == null || method == null) {
			throw new IllegalArgumentException("Missing metadata or metadata uuid or method");
		}
		log.info("Create Integration request {} {}", metadata.getGlobalId(), method);

		final IntegrationRequestEntity integrationRequestEntity = IntegrationRequestEntity.builder()
				.uuid(UUID.randomUUID())
				.method(method)
				.submissionDate(LocalDateTime.now())
				.submittedByHarvesting(fromHarvesting)
				.resourceTitle(metadata.getResourceTitle())
				.globalId(metadata.getGlobalId())
				.progressStatus(ProgressStatus.CREATED)
				.integrationStatus(IntegrationStatus.KO)
				.nodeProviderId(nodeProvider.getUuid())
				.version(nodeProvider.getVersion())
				.file(deshydrateMetadata(metadata))
				.build();

		log.info("Create Integration request {} {} done", metadata.getGlobalId(), method);
		return integrationRequestMapper.entityToDto(integrationRequestDao.save(integrationRequestEntity));
	}

	@Override
	@Transactional // readOnly = false
	public void handleIntegrationRequest(UUID uuid) {
		log.info("Handle Integration request {}", uuid);
		IntegrationRequestEntity integrationRequest = integrationRequestCustomDao.findByUUIDAndLock(uuid);

		if (integrationRequest.getProgressStatus() == ProgressStatus.CREATED) {
			handleIntegrationRequestTreatment(integrationRequest);
		} else if (integrationRequest.getProgressStatus() == ProgressStatus.INTEGRATION_HANDLED) {
			handleIntegrationRequestReport(integrationRequest);
		}
		log.info("Handle Integration request {} done.", uuid);
	}

	/**
	 * @param integrationRequest requête
	 */
	private void handleIntegrationRequestTreatment(IntegrationRequestEntity integrationRequest) {
		final Method method = integrationRequest.getMethod();
		switch (method) {
			case POST:
				postIntegrationRequestHandler.handle(integrationRequest);
				break;
			case PUT:
				putIntegrationRequestHandler.handle(integrationRequest);
				break;
			case DELETE:
				deleteIntegrationRequestHandler.handle(integrationRequest);
				break;
		}
		integrationRequest.setProgressStatus(ProgressStatus.INTEGRATION_HANDLED);
		integrationRequest.setTreatmentDate(LocalDateTime.now());
		integrationRequestDao.save(integrationRequest);
	}

	private void handleIntegrationRequestReport(IntegrationRequestEntity integrationRequest) {

		// Test si l'intégration request peut être traitée
		if (!doesIntegrationRequestReportNeeded(integrationRequest)) {
			return;
		}

		try {
			// mise à jour de la date d'émission du rapport
			integrationRequest.setSendRequestDate(LocalDateTime.now());

			// création du rapport
			Report report = createReport(integrationRequest);

			// envoie du rapport
			NodeProvider nodeProvider = providerHelper.requireNodeProviderByUUID(integrationRequest.getNodeProviderId());
			kalimProviderHelper.sendReport(nodeProvider.getUrl(), report);

			// mise à jour de l'état de la demande
			integrationRequest.setProgressStatus(ProgressStatus.REPORT_SUCCESS);

		} catch (Exception e) {
			log.error("Erreur lors de l'envoie du rapport d'intégration: ", e);
			// Incrémentation du nombre de tentatives
			integrationRequest.setRapportTransmissionAttempts(integrationRequest.getRapportTransmissionAttempts() + 1);
			if (integrationRequest.getRapportTransmissionAttempts() >= sendReportRetry) {
				// Si au bout de 5 fois, le rapport n'a toujorus pas pu être transmis, on le mets en report error
				integrationRequest.setProgressStatus(ProgressStatus.REPORT_ERROR);
			}
		}

		integrationRequestDao.save(integrationRequest);
	}

	private Report createReport(IntegrationRequestEntity integrationRequest) {
		Report report = new Report();

		// Génération du reportId
		report.setReportId(integrationRequest.getUuid());
		report.setResourceId(integrationRequest.getGlobalId());

		report.setTreatmentDate(integrationRequest.getTreatmentDate());
		report.setMethod(integrationRequest.getMethod());
		report.setResourceTitle(integrationRequest.getResourceTitle());
		report.setVersion(integrationRequest.getVersion());
		report.setIntegrationStatus(integrationRequest.getIntegrationStatus());

		// Passage des integrationRequestErros en reportError
		List<ReportError> errorsDto = reportErrorMapper.entitiesToDto(integrationRequest.getErrors());
		report.setIntegrationErrors(errorsDto);

		// Génération du commentaire
		val requestTypeForComment = getRequestTypeForComment(integrationRequest);
		String comment;
		if (integrationRequest.getIntegrationStatus() == IntegrationStatus.OK) {
			comment = String.format("%s du jeu de données %s s’est bien déroulée le %s",
					requestTypeForComment, integrationRequest.getResourceTitle(), LocalDateTime.now());
		} else {
			comment = String.format(
					"%s du jeu de données %s ne s’est pas déroulée correctement, le %s. Veuillez consulter les erreurs ci-dessous et après correction des erreurs, renvoyer votre jeu de données. Pour plus d’information, vous pouvez contacter votre administrateur Rudi.",
					requestTypeForComment, integrationRequest.getResourceTitle(), LocalDateTime.now());
		}
		report.setComment(comment);
		report.setSubmissionDate(integrationRequest.getSubmissionDate());
		report.setSubmittedByHarvesting(integrationRequest.isSubmittedByHarvesting());
		return report;
	}

	private String getRequestTypeForComment(IntegrationRequestEntity integrationRequest) {
		switch (integrationRequest.getMethod()) {
			case PUT:
				return "la modification";
			case DELETE:
				return "la suppression";
			default:
				return "l’intégration";
		}
	}

	/**
	 * Test si l'intégration request peut être être traitée.
	 *
	 * @param integrationRequest requête
	 * @return true si peut être traitée, false sinon
	 */
	private boolean doesIntegrationRequestReportNeeded(IntegrationRequestEntity integrationRequest) {
		final LocalDateTime sendDate = integrationRequest.getSendRequestDate();
		// si on a une date d'envoi du rapport
		if (sendDate != null) {

			// Ajout de la période de renvoi à la date de dernier envoi
			final LocalDateTime resendDate = sendDate.plus(sendReportPeriod, ChronoUnit.MINUTES);

			// si la date calculée est dans le futur
			// l'envoi du rapport n'est pas nécessaire
			return !resendDate.isAfter(LocalDateTime.now());
		}

		return true;
	}

	/**
	 * Transforme un objet Metadata en une chaine de caractères
	 *
	 * @param metadata le metadata à sérialiser en String
	 * @return une chaine de caractères représentant le metadata au format JSON
	 * @throws IntegrationException en cas d'erreur lors de la sérialisation JSON de la metadata
	 */
	private String deshydrateMetadata(Metadata metadata) throws IntegrationException {
		try {
			return objectMapper.writeValueAsString(metadata);
		} catch (Exception e) {
			throw new IntegrationException(
					"Error lors de la récupération des Metadata dans l'Integration Request : transformation Metadata -> JSON",
					e);
		}
	}

	@Override
	public UUID generateMetaDataId() {
		return UUID.randomUUID();
	}
}
