package org.rudi.microservice.kalim.service.integration.impl.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class AbstractIntegrationRequestTreatmentHandlerWithValidation extends IntegrationRequestTreatmentHandler {

	protected final ObjectMapper objectMapper;
	protected final List<AbstractMetadataValidator<?>> metadataValidators;

	public AbstractIntegrationRequestTreatmentHandlerWithValidation(DatasetService datasetService, APIManagerHelper apiManagerHelper, ObjectMapper objectMapper, List<AbstractMetadataValidator<?>> metadataValidators, Error500Builder error500Builder) {
		super(datasetService, apiManagerHelper, error500Builder);
		this.objectMapper = objectMapper;
		this.metadataValidators = metadataValidators;
	}

	protected void handleInternal(IntegrationRequestEntity integrationRequest) throws IntegrationException, DataverseAPIException, APIManagerException {
		final Metadata metadata = hydrateMetadata(integrationRequest.getFile());
		if (validateAndSetErrors(metadata, integrationRequest)) {
			treat(integrationRequest, metadata);
			integrationRequest.setIntegrationStatus(IntegrationStatus.OK);
		} else {
			integrationRequest.setIntegrationStatus(IntegrationStatus.KO);
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

	protected abstract void treat(IntegrationRequestEntity integrationRequest, Metadata metadata) throws DataverseAPIException, APIManagerException;
}
