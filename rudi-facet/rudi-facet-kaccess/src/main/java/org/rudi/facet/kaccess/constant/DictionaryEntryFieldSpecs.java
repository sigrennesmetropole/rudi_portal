package org.rudi.facet.kaccess.constant;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.DictionaryEntry;

public class DictionaryEntryFieldSpecs {

	public final FieldSpec lang;
	public final FieldSpec text;

	private DictionaryEntryFieldSpecs(FieldSpec parentSpec) {
		lang = parentSpec.newChildFromJavaField(DictionaryEntry.class, ConstantMetadata.LANG_FIELD_LOCAL_NAME)
				.allowControlledVocabulary(false);
		text = parentSpec.newChildFromJavaField(DictionaryEntry.class, ConstantMetadata.TEXT_FIELD_LOCAL_NAME);
	}

	public static DictionaryEntryFieldSpecs from(FieldSpec parentSpec) {
		return new DictionaryEntryFieldSpecs(parentSpec);
	}

	public FieldSpec[] toArray() {
		return new FieldSpec[] { lang, text };
	}
}
