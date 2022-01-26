package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.CUSTOM_LICENCE_URI;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GDPR_SENSITIVE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LICENCE_LABEL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LICENCE_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESTRICTED_ACCESS;

@Component
class AccessConditionPrimiviteFieldsMapper extends PrimitiveFieldsMapper<MetadataAccessCondition> {
	// D'après le Swagger : "Default is open licence."
	private static final String DEFAULT_SKOS_CODE = "open-source-licence";

	AccessConditionPrimiviteFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper) {
		super(fieldGenerator, objectMapper);
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
	public MetadataAccessCondition fieldToMetadata(@Nonnull Field primitiveRootField) {
		return createAccessCondition(primitiveRootField);
	}

	private MetadataAccessCondition createAccessCondition(Field accessConditionField) {
		final MetadataAccessCondition accessCondition = new MetadataAccessCondition();

		final Field restrictedAccessField = accessConditionField.get(RESTRICTED_ACCESS);
		final Field gdprSensitiveField = accessConditionField.get(GDPR_SENSITIVE);
		if (restrictedAccessField != null || gdprSensitiveField != null) {
			final MetadataAccessConditionConfidentiality confidentiality = new MetadataAccessConditionConfidentiality();
			if (restrictedAccessField != null) {
				confidentiality.setRestrictedAccess(restrictedAccessField.getValueAsBoolean());
			}
			if (gdprSensitiveField != null) {
				confidentiality.setGdprSensitive(gdprSensitiveField.getValueAsBoolean());
			}
			accessCondition.setConfidentiality(confidentiality);
		}

		final Field licenceTypeField = accessConditionField.get(LICENCE_TYPE);
		final Licence licence;
		final Licence.LicenceTypeEnum licenceTypeEnum = licenceTypeField.getValueWith(Licence.LicenceTypeEnum::valueOf);
		switch (licenceTypeEnum) {
			case STANDARD:
				final LicenceStandard licenceStandard = new LicenceStandard();
				final Field licenceLabelField = accessConditionField.get(LICENCE_LABEL);
				licenceStandard.setLicenceLabel(licenceLabelField.getValueAsString());
				licence = licenceStandard;
				break;
			case CUSTOM:
				final LicenceCustom licenceCustom = new LicenceCustom();
				final Field customLicenceUriField = accessConditionField.get(CUSTOM_LICENCE_URI);
				if (customLicenceUriField != null) {
					licenceCustom.setCustomLicenceUri(customLicenceUriField.getValueAsString());
				}
				licence = licenceCustom;
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
