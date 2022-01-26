package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.ACCESS_CONSTRAINT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.BIBLIOGRAPHICAL_REFERENCE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CUSTOM_LICENCE_LABEL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MANDATORY_MENTION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.OTHER_CONSTRAINTS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.USAGE_CONSTRAINT;

@Component
class AccessConditionCompoundFieldsMapper extends CompoundFieldsMapper<MetadataAccessCondition> {

	AccessConditionCompoundFieldsMapper(FieldGenerator fieldGenerator) {
		super(fieldGenerator);
	}

	@Override
	void metadataToFields(MetadataAccessCondition accessCondition, List<DatasetMetadataBlockElementField> fields) {
		final Licence licence = accessCondition.getLicence();

		if (licence instanceof LicenceCustom) {
			final LicenceCustom licenceCustom = (LicenceCustom) licence;
			createField(CUSTOM_LICENCE_LABEL, licenceCustom.getCustomLicenceLabel(), fields);
		}

		createField(USAGE_CONSTRAINT, accessCondition.getUsageConstraint(), fields);
		createField(BIBLIOGRAPHICAL_REFERENCE, accessCondition.getBibliographicalReference(), fields);
		createField(MANDATORY_MENTION, accessCondition.getMandatoryMention(), fields);
		createField(ACCESS_CONSTRAINT, accessCondition.getAccessConstraint(), fields);
		createField(OTHER_CONSTRAINTS, accessCondition.getOtherConstraints(), fields);
	}

	@Override
	void fieldsToMetadata(RootFields rootFields, Metadata metadata) {
		fieldToMetadata(rootFields, CUSTOM_LICENCE_LABEL, entries -> {
			final LicenceCustom licenceCustom = (LicenceCustom) metadata.getAccessCondition().getLicence();
			licenceCustom.setCustomLicenceLabel(entries);
		});
		fieldToMetadata(rootFields, USAGE_CONSTRAINT, entries -> metadata.getAccessCondition().setUsageConstraint(entries));
		fieldToMetadata(rootFields, BIBLIOGRAPHICAL_REFERENCE, entries -> metadata.getAccessCondition().setBibliographicalReference(entries));
		fieldToMetadata(rootFields, MANDATORY_MENTION, entries -> metadata.getAccessCondition().setMandatoryMention(entries));
		fieldToMetadata(rootFields, ACCESS_CONSTRAINT, entries -> metadata.getAccessCondition().setAccessConstraint(entries));
		fieldToMetadata(rootFields, OTHER_CONSTRAINTS, entries -> metadata.getAccessCondition().setOtherConstraints(entries));
	}


}
