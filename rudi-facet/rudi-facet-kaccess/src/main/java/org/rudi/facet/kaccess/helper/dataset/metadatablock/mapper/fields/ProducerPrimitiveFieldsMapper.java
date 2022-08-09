package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.Organization;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_ADDRESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_CAPTION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_COORDINATES_LATITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_COORDINATES_LONGITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_SUMMARY;

@Component
class ProducerPrimitiveFieldsMapper extends PrimitiveFieldsMapper<Organization> {

	ProducerPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(Organization producer, Map<String, Object> fields) throws DataverseMappingException {
		createOrganizationFields(producer, fields,
				PRODUCER_ORGANIZATION_NAME,
				PRODUCER_ORGANIZATION_ADDRESS,
				PRODUCER_ORGANIZATION_ID,
				PRODUCER_ORGANIZATION_COORDINATES_LATITUDE,
				PRODUCER_ORGANIZATION_COORDINATES_LONGITUDE,
				PRODUCER_ORGANIZATION_CAPTION,
				PRODUCER_ORGANIZATION_SUMMARY);
	}

	@Nonnull
	@Override
	public Organization fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException {
		return buildOrganization(fields,
				PRODUCER_ORGANIZATION_NAME,
				PRODUCER_ORGANIZATION_ADDRESS,
				PRODUCER_ORGANIZATION_ID,
				PRODUCER_ORGANIZATION_COORDINATES_LATITUDE,
				PRODUCER_ORGANIZATION_COORDINATES_LONGITUDE,
				PRODUCER_ORGANIZATION_CAPTION,
				PRODUCER_ORGANIZATION_SUMMARY);
	}

	@Nullable
	@Override
	public Organization defaultMetadata() {
		return null;
	}
}
