package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.rudi.facet.kaccess.bean.Organization;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_API_VERSION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_CREATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_DELETED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_EXPIRES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_PUBLISHED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_VALIDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_CAPTION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LATITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LONGITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_SUMMARY;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_SOURCE;

@Component
class MetadataInfoPrimitiveFieldsMapper extends PrimitiveFieldsMapper<MetadataMetadataInfo> {

	MetadataInfoPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(MetadataMetadataInfo metadataInfo, Map<String, Object> fields) throws DataverseMappingException {
		Objects.requireNonNull(metadataInfo, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(METADATA_INFO));

		createField(METADATA_INFO_API_VERSION, metadataInfo.getApiVersion(), fields);
		createField(METADATA_INFO_API_VERSION, metadataInfo.getApiVersion(), fields);

		createDatesFields(metadataInfo.getMetadataDates(), fields,
				METADATA_INFO_DATES_CREATED,
				METADATA_INFO_DATES_VALIDATED,
				METADATA_INFO_DATES_PUBLISHED,
				METADATA_INFO_DATES_UPDATED,
				METADATA_INFO_DATES_EXPIRES,
				METADATA_INFO_DATES_DELETED
		);

		createProviderFields(metadataInfo.getMetadataProvider(), fields);

		createField(METADATA_INFO_SOURCE, metadataInfo.getMetadataSource(), fields);
	}

	private void createProviderFields(Organization provider, Map<String, Object> fields) throws DataverseMappingException {
		createOrganizationFields(provider, fields,
				METADATA_INFO_PROVIDER_ORGANIZATION_NAME,
				METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS,
				METADATA_INFO_PROVIDER_ORGANIZATION_ID,
				METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LATITUDE,
				METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LONGITUDE,
				METADATA_INFO_PROVIDER_ORGANIZATION_CAPTION,
				METADATA_INFO_PROVIDER_ORGANIZATION_SUMMARY);
	}

	@Nonnull
	@Override
	public MetadataMetadataInfo fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException {
		return new MetadataMetadataInfo()
				.apiVersion(fields.get(METADATA_INFO_API_VERSION).getValueAsString())
				.metadataDates(buildDates(fields,
						METADATA_INFO_DATES_CREATED,
						METADATA_INFO_DATES_VALIDATED,
						METADATA_INFO_DATES_PUBLISHED,
						METADATA_INFO_DATES_UPDATED,
						METADATA_INFO_DATES_EXPIRES,
						METADATA_INFO_DATES_DELETED))
				.metadataProvider(buildOrganization(fields,
						METADATA_INFO_PROVIDER_ORGANIZATION_NAME,
						METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS,
						METADATA_INFO_PROVIDER_ORGANIZATION_ID,
						METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LATITUDE,
						METADATA_INFO_PROVIDER_ORGANIZATION_COORDINATES_LONGITUDE,
						METADATA_INFO_PROVIDER_ORGANIZATION_CAPTION,
						METADATA_INFO_PROVIDER_ORGANIZATION_SUMMARY))
				.metadataSource(fields.get(METADATA_INFO_SOURCE).getValueAsString())
				;
	}

	@Nullable
	@Override
	public MetadataMetadataInfo defaultMetadata() {
		return new MetadataMetadataInfo();
	}
}
