package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.util.HashMap;
import java.util.Map;

/**
 * Exemple de CompoundValue :
 *
 * <pre>{@code
 *     "value": [
 *       {
 *         "rudi_access_condition_usage_constraint_language": {
 *           "typeName": "rudi_access_condition_usage_constraint_language",
 *           "typeClass": "primitive",
 *           "multiple": false,
 *           "value": "fr-FR"
 *         },
 *         "rudi_access_condition_usage_constraint_text": {
 *           "typeName": "rudi_access_condition_usage_constraint_text",
 *           "typeClass": "primitive",
 *           "multiple": false,
 *           "value": "Usage libre sous réserve des mentions obligatoires sur tout document de diffusion"
 *         }
 *       }
 *     ]
 * }</pre>
 *
 * <p>
 * Dans cet exemple les {@link Field champs} <code>rudi_access_condition_usage_constraint_language</code> et <code>rudi_access_condition_usage_constraint_text</code>
 * font partie de la CompoundValue.
 * <p>
 */
class CompoundValue {
	final Map<?, ?> jsonMap;
	final Map<String, Field> fieldsByTypeName;

	CompoundValue(Map<?, ?> jsonMap) {
		this.jsonMap = jsonMap;
		fieldsByTypeName = new HashMap<>(jsonMap.size());
	}

	Field get(String typeName) {
		return fieldsByTypeName.computeIfAbsent(typeName, k -> {
			final Object jsonField = jsonMap.get(typeName);
			return Field.createFieldFrom(jsonField);
		});
	}
}
