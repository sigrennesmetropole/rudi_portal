package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.DictionaryEntryFieldSpecs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.LANG_FIELD_SUFFIX;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.TEXT_FIELD_SUFFIX;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
abstract class CompoundFieldsMapper<T> {

	private final FieldGenerator fieldGenerator;

	abstract void metadataToFields(T metadataElement, List<DatasetMetadataBlockElementField> fields);

	@Nullable
	DatasetMetadataBlockElementField createCompoundFieldFromEntries(FieldSpec fieldSpec, final List<DictionaryEntry> entries) {
		final var dictionaryEntryFieldSpecs = DictionaryEntryFieldSpecs.from(fieldSpec);
		final FieldSpec langFieldSpec = dictionaryEntryFieldSpecs.lang;
		final FieldSpec textFieldSpec = dictionaryEntryFieldSpecs.text;
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
			final var compoundFieldFromEntries = createCompoundFieldFromEntries(spec, entries);
			if (compoundFieldFromEntries != null) {
				fields.add(compoundFieldFromEntries);
			}
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
