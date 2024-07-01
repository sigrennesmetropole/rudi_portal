package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.rudi.microservice.kalim.service.IntegrationError.ERR_403;

/**
 * Le nœud fournisseur authentifié correspond à celui qui a créé le JDD ?
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatasetCreatorIsAuthenticatedValidator {
	private final DatasetService datasetService;

	/**
	 *
	 * @param handler de la requête
	 * @return false -- contrôle désactivé
	 */
	public boolean canBeUsedBy(IntegrationRequestTreatmentHandler handler) {
		// Contrôle désactivé par la RUDI-1459
		return false;
	}

	public Set<IntegrationRequestErrorEntity> validate(IntegrationRequestEntity integrationRequest) {
		final UUID globalId = integrationRequest.getGlobalId();
		final Set<IntegrationRequestErrorEntity> errors = new HashSet<>();

		try {
			final var creatorProviderId = getCreatorProviderId(globalId);
			if (creatorProviderId != null) {

				final var authenticatedProviderId = integrationRequest.getNodeProviderId();
				if (!creatorProviderId.equals(authenticatedProviderId)) {
					errors.add(new IntegrationRequestErrorEntity(
							UUID.randomUUID(),
							ERR_403.getCode(),
							ERR_403.getMessage(),
							RudiMetadataField.METADATA_INFO_PROVIDER.getName(),
							LocalDateTime.now()));
				}

			}
		} catch (final DataverseAPIException e) {
			log.error("Impossible de retrouver le créateur du JDD {} à cause d'une erreur Dataverse", globalId, e);
			errors.add(new Error500Builder().build());
		}

		return errors;
	}

	@Nullable
	private UUID getCreatorProviderId(UUID globalId) throws DataverseAPIException {
		final var existingMetadata = datasetService.getDataset(globalId);
		final var creatorMetadata = existingMetadata.getMetadataInfo().getMetadataProvider();
		return creatorMetadata != null ? creatorMetadata.getOrganizationId() : null;
	}

}
