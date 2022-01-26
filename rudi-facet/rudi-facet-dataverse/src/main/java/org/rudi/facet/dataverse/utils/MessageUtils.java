package org.rudi.facet.dataverse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.rudi.facet.dataverse.fields.FieldSpec;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {

	/**
	 * Message généré lorsqu'une propriété obligatoire est manquante
	 *
	 * @param field nom de la propriété
	 * @return String
	 */
	public static String buildErrorMessageRequiredMandatoryAttributes(FieldSpec field) {
		return String.format("La propriété %s est obligatoire", field.getName());
	}
}
