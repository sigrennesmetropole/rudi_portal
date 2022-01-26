package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMetadataIdValidator<T> extends AbstractMetadataValidator<Metadata> {

	private final FieldExtractor<T> fieldExtractor;

	@Override
	protected Metadata getMetadataElementToValidate(Metadata metadata) {
		return metadata;
	}

	@Override
	public Set<IntegrationRequestErrorEntity> validate(Metadata metadata) {
		final Set<IntegrationRequestErrorEntity> errors = new HashSet<>();

		final T fieldValue = getFieldValue(metadata);
		if (fieldValue != null) {
			try {
				if (datasetAlreadyExistsWithFieldValue(fieldValue) != validationSucceedsIfDatasetAlreadyExists()) {
					errors.add(getErrorBuilderForFieldValue(fieldValue)
							.build()
					);
				}
			} catch (DataverseAPIException e) {
				log.error("Error checking if Dataset with {} {} already exists.", getField().getLocalName(), fieldValue, e);
				errors.add(new Error500Builder().build());
			}
		}

		return errors;
	}

	protected FieldSpec getField() {
		return fieldExtractor.getField();
	}

	private T getFieldValue(Metadata metadata) {
		return fieldExtractor.getFieldValue(metadata);
	}

	protected abstract boolean datasetAlreadyExistsWithFieldValue(T fieldValue) throws DataverseAPIException;

	/**
	 * @return true si on doit valider que l'id existe, false si on doit vérifier qu'il n'existe pas
	 */
	protected abstract boolean validationSucceedsIfDatasetAlreadyExists();

	protected abstract ErrorBuilder getErrorBuilderForFieldValue(T fieldValue);
}
