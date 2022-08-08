/**
 * 
 */
package org.rudi.facet.bpmn.bean.form;

import java.util.ArrayList;
import java.util.List;

import org.rudi.bpmn.core.bean.FieldDefinition;

import lombok.Data;

/**
 * Classe représentant un formulaire
 * 
 * @author FNI18300
 *
 */
@Data
public class FormDefinition {

	private List<FieldDefinition> fieldDefinitions;

	/**
	 * Ajoute une définition dans la définition du formulaire
	 * 
	 * @param fieldDefinition
	 */
	public void addFieldDefinitions(FieldDefinition fieldDefinition) {
		if (fieldDefinitions == null) {
			fieldDefinitions = new ArrayList<>();
		}
		if (fieldDefinition != null) {
			fieldDefinitions.add(fieldDefinition);
		}
	}
}
