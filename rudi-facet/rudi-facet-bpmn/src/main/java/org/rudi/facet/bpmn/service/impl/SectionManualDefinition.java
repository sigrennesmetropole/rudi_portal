package org.rudi.facet.bpmn.service.impl;

import java.util.List;

import org.rudi.bpmn.core.bean.FieldDefinition;

import lombok.Getter;

/**
 * Définition manuelle d'une section.
 */
@Getter
class SectionManualDefinition {
	/**
	 * Optionnel. Si renseigné, alors la section sera matérialisée par une bordure/card côté front.
	 */
	private String label;
	/**
	 * Optionnel. Si renseigné, apparait uniquement si label défini conjointement
	 */
	private String help;
	private List<FieldDefinition> fieldDefinitions;
}
