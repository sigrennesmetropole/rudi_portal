package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.rudi.microservice.kalim.service.IntegrationError.ERR_202;

/**
 * Le nœud fournisseur authentifié correspond à celui indiqué dans le JDD envoyé ?
 */
@Component
public class MetadataInfoProviderIsAuthenticatedValidator {

	public Set<IntegrationRequestErrorEntity> validate(Metadata metadata, IntegrationRequestEntity integrationRequest) {
		final Set<IntegrationRequestErrorEntity> errors = new HashSet<>();

		final var authenticatedProviderId = integrationRequest.getNodeProviderId();
		final var metadataProvider = metadata.getMetadataInfo().getMetadataProvider();
		if (metadataProvider == null) {

			errors.add(new IntegrationRequestErrorEntity(
					UUID.randomUUID(),
					ERR_202.getCode(),
					ERR_202.getMessage(),
					RudiMetadataField.METADATA_INFO_PROVIDER.getName(),
					LocalDateTime.now()));

		} else {

			final var declaredProviderId = metadataProvider.getOrganizationId();
			if (!authenticatedProviderId.equals(declaredProviderId)) {
				errors.add(new Error303Builder()
						.field(RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_ID)
						.fieldValue(declaredProviderId.toString())
						.expectedString("l'UUID du nœud fournisseur authentifié")
						.build());
			}

		}

		return errors;
	}

}
