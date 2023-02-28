package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.springframework.stereotype.Component;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.CUSTOM_LICENCE_URI;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GDPR_SENSITIVE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LICENCE_LABEL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LICENCE_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESTRICTED_ACCESS;

@Component
class AccessConditionPrimiviteFieldsMapper extends PrimitiveFieldsMapper<MetadataAccessCondition> {
	private static final LicenceStandard.LicenceLabelEnum DEFAULT_SKOS_CODE = LicenceStandard.LicenceLabelEnum.PUBLIC_DOMAIN_CC0;

	AccessConditionPrimiviteFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(MetadataAccessCondition accessCondition, Map<String, Object> fields) throws DataverseMappingException {
		final MetadataAccessConditionConfidentiality confidentiality = accessCondition.getConfidentiality();
		if (confidentiality != null) {
			createField(RESTRICTED_ACCESS, confidentiality.getRestrictedAccess(), fields);
			createField(GDPR_SENSITIVE, confidentiality.getGdprSensitive(), fields);
		}

		final Licence licence = accessCondition.getLicence();
		createField(LICENCE_TYPE, licence.getLicenceType().getValue(), fields);

		if (licence instanceof LicenceStandard) {
			final LicenceStandard licenceStandard = (LicenceStandard) licence;
			createField(LICENCE_LABEL, licenceStandard.getLicenceLabel(), fields);
		} else if (licence instanceof LicenceCustom) {
			final LicenceCustom licenceCustom = (LicenceCustom) licence;
			createField(CUSTOM_LICENCE_URI, licenceCustom.getCustomLicenceUri(), fields);
		}
	}

	@Nonnull
	@Override
	public MetadataAccessCondition fieldsToMetadata(@Nonnull MapOfFields fields) {
		return createAccessCondition(fields);
	}

	private MetadataAccessCondition createAccessCondition(MapOfFields fields) {
		final var accessCondition = new MetadataAccessCondition();

		final MetadataAccessConditionConfidentiality confidentiality = new MetadataAccessConditionConfidentiality()
				.restrictedAccess(fields.get(RESTRICTED_ACCESS).getValueAsBoolean())
				.gdprSensitive(fields.get(GDPR_SENSITIVE).getValueAsBoolean());
		accessCondition.setConfidentiality(ObjectsUtils.nullIfEmpty(confidentiality));

		final var licenceTypeField = fields.get(LICENCE_TYPE);
		final Licence licence;
		final var licenceTypeEnum = licenceTypeField.getValueAsEnumWith(Licence.LicenceTypeEnum::valueOf);
		switch (licenceTypeEnum) {
			case STANDARD:
				licence = new LicenceStandard()
						.licenceLabel(fields.get(LICENCE_LABEL).getValueAsEnumWith(LicenceStandard.LicenceLabelEnum::fromValue));
				break;
			case CUSTOM:
				licence = new LicenceCustom()
						.customLicenceUri(fields.get(CUSTOM_LICENCE_URI).getValueAsString());
				break;
			default:
				throw new NotImplementedException("Mapping not implemented for licenceTypeEnum '" + licenceTypeEnum + "'");
		}
		licence.setLicenceType(licenceTypeEnum);
		accessCondition.setLicence(licence);

		return accessCondition;
	}

	@Nullable
	@Override
	public MetadataAccessCondition defaultMetadata() {
		return new MetadataAccessCondition()
				.licence(new LicenceStandard()
						.licenceLabel(DEFAULT_SKOS_CODE)
						.licenceType(Licence.LicenceTypeEnum.STANDARD)
				);
	}

}
