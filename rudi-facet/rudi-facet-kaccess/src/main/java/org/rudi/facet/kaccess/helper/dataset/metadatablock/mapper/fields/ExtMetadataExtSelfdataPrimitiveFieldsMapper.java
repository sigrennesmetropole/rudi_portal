package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.DELETABLE_DATA;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DELETION_REASON;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MATCHING_DATA;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SELFDATA_ACCESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SELFDATA_CATEGORIES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SELFDATA_HELD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.STORAGE_PERIOD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.STORAGE_PERIOD_UNIT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.STORAGE_PERIOD_VALUE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TREATMENT_PERIOD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TREATMENT_PERIOD_UNIT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TREATMENT_PERIOD_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.facet.kaccess.bean.MetadataExtMetadataExtSelfdata;
import org.rudi.facet.kaccess.bean.Period;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.bean.SelfdataContent.SelfdataCategoriesEnum;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ExtMetadataExtSelfdataPrimitiveFieldsMapper extends PrimitiveFieldsMapper<MetadataExtMetadataExtSelfdata> {
	public ExtMetadataExtSelfdataPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper,
			DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(MetadataExtMetadataExtSelfdata metadataElement, Map<String, Object> fields)
			throws DataverseMappingException {
		final SelfdataContent extSelfdataContent = metadataElement.getExtSelfdataContent();
		createField(SELFDATA_ACCESS, extSelfdataContent.getSelfdataAccess(), fields);

		final Period storagePeriod = extSelfdataContent.getStoragePeriod();
		if (storagePeriod != null) {
			createField(STORAGE_PERIOD_VALUE, storagePeriod.getValue(), fields);
			createField(STORAGE_PERIOD_UNIT, storagePeriod.getUnit(), fields);
		}

		final Period treatmentPeriod = extSelfdataContent.getTreatmentPeriod();
		if (treatmentPeriod != null) {
			createField(TREATMENT_PERIOD_VALUE, treatmentPeriod.getValue(), fields);
			createField(TREATMENT_PERIOD_UNIT, treatmentPeriod.getUnit(), fields);
		}

		final List<SelfdataCategoriesEnum> selfdataCategoriesEnums = extSelfdataContent.getSelfdataCategories();
		if (CollectionUtils.isNotEmpty(selfdataCategoriesEnums)) {
			createField(SELFDATA_CATEGORIES, selfdataCategoriesEnums, fields);
		}

		createField(DELETABLE_DATA, extSelfdataContent.getDeletableData(), fields);
		createField(SELFDATA_HELD, extSelfdataContent.getSelfdataHeld(), fields);

		final List<String> deletionReason = extSelfdataContent.getDeletionReason();
		if (CollectionUtils.isNotEmpty(deletionReason)) {
			createField(DELETION_REASON, deletionReason, fields);
		}

		final List<MatchingData> mathingDatas = extSelfdataContent.getMatchingData();
		if (CollectionUtils.isNotEmpty(mathingDatas)) {
			createField(MATCHING_DATA, mathingDatas, fields);
		}
	}

	@NotNull
	@Override
	public MetadataExtMetadataExtSelfdata fieldsToMetadata(@NotNull MapOfFields fields)
			throws DataverseMappingException {
		final MetadataExtMetadataExtSelfdata selfdata = new MetadataExtMetadataExtSelfdata();
		final SelfdataContent selfdataContent = new SelfdataContent();
		selfdataContent.setSelfdataAccess(
				fields.get(SELFDATA_ACCESS).getValueAsEnumWith(SelfdataContent.SelfdataAccessEnum::valueOf));
		selfdataContent.setSelfdataHeld(fields.get(SELFDATA_HELD).getValueAsString());
		selfdataContent.setDeletableData(fields.get(DELETABLE_DATA).getValueAsBoolean());
		selfdataContent.setDeletionReason(fields.get(DELETION_REASON).getValueAsListOf(String.class, objectMapper));
		selfdataContent.setSelfdataCategories(
				fields.get(SELFDATA_CATEGORIES).getValueAsListOf(SelfdataCategoriesEnum.class, objectMapper));
		final Period storagePeriod = getFieldPeriod(fields, STORAGE_PERIOD);
		selfdataContent.setStoragePeriod(storagePeriod);
		final Period treatmentPeriod = getFieldPeriod(fields, TREATMENT_PERIOD);
		selfdataContent.setTreatmentPeriod(treatmentPeriod);
		selfdataContent.setMatchingData(fields.get(MATCHING_DATA).getValueAsListOf(MatchingData.class, objectMapper));
		selfdata.setExtSelfdataContent(selfdataContent);
		return selfdata;
	}

	private Period getFieldPeriod(@NotNull MapOfFields fields, FieldSpec fieldSpec) {
		List<FieldSpec> periodChildren = new ArrayList<>(fieldSpec.getDirectChildren()); // renvoie les 2 enfants d'une periode [value, unit]
		if (CollectionUtils.isEmpty(periodChildren) || periodChildren.size() < 2) {
			return null;
		}
		Field periodValue = fields.get(periodChildren.get(0));
		if (periodValue.getValueAsBigDecimal() == null) {
			return null;
		}
		Period.UnitEnum unitEnum = fields.get(periodChildren.get(1)).getValueAsEnumWith(Period.UnitEnum::valueOf);
		Period result = new Period();
		result.setValue(periodValue.getValueAsBigDecimal().intValue());
		result.setUnit(unitEnum);
		return result;
	}

	@Nullable
	@Override
	public MetadataExtMetadataExtSelfdata defaultMetadata() {
		return null;
	}
}
