package org.rudi.microservice.kalim.service.integration.impl.handlers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.bean.OrganizationRole;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.AddUserToOrganizationException;
import org.rudi.facet.organization.helper.exceptions.CreateOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.helper.ApiManagerHelper;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.MetadataInfoProviderIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractIntegrationRequestTreatmentHandlerWithValidation
		extends AbstractIntegrationRequestTreatmentHandler {

	protected final ObjectMapper objectMapper;
	protected final List<AbstractMetadataValidator<?>> metadataValidators;
	protected final MetadataInfoProviderIsAuthenticatedValidator metadataInfoProviderIsAuthenticatedValidator;
	protected final DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator;
	private final OrganizationHelper organizationHelper;
	@Getter
	@Value("${default.organization.password:12345678Mm$}")
	private String defaultOrganizationPassword;

	protected AbstractIntegrationRequestTreatmentHandlerWithValidation(DatasetService datasetService,
			ApiManagerHelper apiGatewayManagerHelper, APIManagerHelper apiManagerHelper, ObjectMapper objectMapper,
			List<AbstractMetadataValidator<?>> metadataValidators, Error500Builder error500Builder,
			MetadataInfoProviderIsAuthenticatedValidator metadataInfoProviderIsAuthenticatedValidator,
			DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator,
			OrganizationHelper organizationHelper) {
		super(datasetService, apiGatewayManagerHelper, apiManagerHelper, error500Builder);
		this.objectMapper = objectMapper;
		this.metadataValidators = metadataValidators;
		this.metadataInfoProviderIsAuthenticatedValidator = metadataInfoProviderIsAuthenticatedValidator;
		this.datasetCreatorIsAuthenticatedValidator = datasetCreatorIsAuthenticatedValidator;
		this.organizationHelper = organizationHelper;
	}

	protected void handleInternal(IntegrationRequestEntity integrationRequest)
			throws IntegrationException, DataverseAPIException, APIManagerException {
		final var metadata = hydrateMetadata(integrationRequest.getFile());
		if (validateAndSetErrors(metadata, integrationRequest)) {
			treat(integrationRequest, metadata);
			createOrganizations(metadata, integrationRequest.getNodeProviderId());
			integrationRequest.setIntegrationStatus(IntegrationStatus.OK);
		} else {
			integrationRequest.setIntegrationStatus(IntegrationStatus.KO);
		}
	}

	private void createOrganizations(Metadata metadata, UUID nodeProviderId) {
		try {

			final var producerOrganization = metadata.getProducer();
			createOrganization(producerOrganization);

			final var providerOrganization = metadata.getMetadataInfo().getMetadataProvider();
			createOrganization(providerOrganization);
			addNodeProviderToOrganization(nodeProviderId, providerOrganization);

		} catch (StruktureApiException e) {
			log.warn("Erreur lors de la création des organisations liées au JDD " + metadata.getGlobalId(), e);
		}
	}

	private void createOrganization(@Nullable org.rudi.facet.kaccess.bean.Organization metadataOrganization)
			throws GetOrganizationException, CreateOrganizationException {
		if (metadataOrganization != null) {
			final var organizationId = metadataOrganization.getOrganizationId();
			organizationHelper.createOrganizationIfNotExists(
					new Organization().uuid(organizationId).name(metadataOrganization.getOrganizationName())
							.openingDate(LocalDateTime.now()).password(defaultOrganizationPassword) // Password identique pour toutes les organisations des JDDs
			);
		}
	}

	private void addNodeProviderToOrganization(UUID nodeProviderId,
			@Nullable org.rudi.facet.kaccess.bean.Organization metadataOrganization)
			throws AddUserToOrganizationException, GetOrganizationMembersException {
		if (metadataOrganization != null) {
			final var organizationId = metadataOrganization.getOrganizationId();
			final var member = new OrganizationMember().userUuid(nodeProviderId).role(OrganizationRole.ADMINISTRATOR);
			organizationHelper.addMemberToOrganizationIfNotExists(member, organizationId);
		}
	}

	/**
	 * Transforme une chaine de caractères en Metadata.
	 *
	 * @param file la chaine de caractères qui contient un metadata au format JSON
	 * @return Metadata l'objet java Metadata obtenu à partir du contenu JSON désérialisé
	 * @throws IntegrationException en cas d'erreur lors du parsing JSON de la metadata
	 */
	private Metadata hydrateMetadata(String file) throws IntegrationException {
		try {
			return objectMapper.readValue(file, Metadata.class);
		} catch (Exception e) {
			throw new IntegrationException(
					"Error lors de la récupération des Metadata dans l'Integration Request : transformation JSON -> Metadata",
					e);
		}
	}

	private boolean validateAndSetErrors(Metadata metadata, IntegrationRequestEntity integrationRequest) {
		final Set<IntegrationRequestErrorEntity> errors = validate(metadata);
//		errors.addAll(metadataInfoProviderIsAuthenticatedValidator.validate(metadata, integrationRequest)); // Contrôle désactivé par la RUDI-1459
		if (datasetCreatorIsAuthenticatedValidator.canBeUsedBy(this)) {
			errors.addAll(datasetCreatorIsAuthenticatedValidator.validate(integrationRequest));
		}

		// Sauvegarde des erreurs
		integrationRequest.setErrors(errors);

		return errors.isEmpty();
	}

	protected Set<IntegrationRequestErrorEntity> validate(Metadata metadata) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();

		metadataValidators.forEach(metadataValidator -> {
			if (metadataValidator.canBeUsedBy(this)) {
				integrationRequestsErrors.addAll(metadataValidator.validateMetadata(metadata));
			}
		});
		return integrationRequestsErrors;
	}

	protected abstract void treat(IntegrationRequestEntity integrationRequest, Metadata metadata)
			throws DataverseAPIException, APIManagerException;
}
