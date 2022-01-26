package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.rudi.facet.kaccess.constant.ConstantMetadata.LANG_FIELD_LOCAL_NAME;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.LANG_FIELD_SUFFIX;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.TEXT_FIELD_LOCAL_NAME;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.TEXT_FIELD_SUFFIX;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
abstract class CompoundFieldsMapper<T> {

	private final FieldGenerator fieldGenerator;

	abstract void metadataToFields(T metadataElement, List<DatasetMetadataBlockElementField> fields);

	@org.jetbrains.annotations.NotNull
	DatasetMetadataBlockElementField createCompoundFieldFromEntries(FieldSpec fieldSpec, final List<DictionaryEntry> entries) {
		final FieldSpec langFieldSpec = fieldSpec.newChildFromJavaField(LANG_FIELD_LOCAL_NAME);
		final FieldSpec textFieldSpec = fieldSpec.newChildFromJavaField(TEXT_FIELD_LOCAL_NAME);
		final List<Map<String, Object>> fieldValues = new ArrayList<>();
		for (final DictionaryEntry entry : entries) {
			final Map<String, Object> fieldValue = new HashMap<>();
			createField(langFieldSpec, entry.getLang().getValue(), fieldValue);
			createField(textFieldSpec, entry.getText(), fieldValue);
			fieldValues.add(fieldValue);
		}

		return fieldGenerator.generateField(fieldSpec, fieldValues);
	}

	final void createField(FieldSpec spec, Object value, Map<String, Object> fields) {
		if (value != null) {
			final DatasetMetadataBlockElementField field = fieldGenerator.generateField(spec, value);
			if (field != null) {
				fields.put(field.getTypeName(), field);
			}
		}
	}

	final void createField(FieldSpec spec, List<DictionaryEntry> entries, List<DatasetMetadataBlockElementField> fields) {
		if (CollectionUtils.isNotEmpty(entries)) {
			fields.add(createCompoundFieldFromEntries(spec, entries));
		}
	}

	final void fieldToMetadata(RootFields rootFields, FieldSpec spec, Consumer<List<DictionaryEntry>> setter) {
		final Field rootField = rootFields.get(spec);
		if (rootField != null) {
			final List<DictionaryEntry> entries = getDictionaryEntries(rootField);
			setter.accept(entries);
		}
	}

	abstract void fieldsToMetadata(RootFields rudiRootFields, Metadata metadata);

	static List<DictionaryEntry> getDictionaryEntries(Field field) {
		final List<CompoundValue> compoundValues = field.getCompoundValues();
		final List<DictionaryEntry> entries = new ArrayList<>(compoundValues.size());
		for (final CompoundValue compoundValue : compoundValues) {
			final Field languageField = compoundValue.get(field.getTypeName() + LANG_FIELD_SUFFIX);
			final Field textField = compoundValue.get(field.getTypeName() + TEXT_FIELD_SUFFIX);
			final DictionaryEntry entry = new DictionaryEntry()
					.lang(Language.fromValue(languageField.getValueAsString()))
					.text(textField.getValueAsString());
			entries.add(entry);
		}
		return entries;
	}


}
