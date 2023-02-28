package org.rudi.facet.bpmn.service.impl;

import java.util.List;

import lombok.Getter;

/**
 * Définition manuelle de formulaire lié à des processus, des tâches et des actions de WorkFlow.
 */
@Getter
class FormManualDefinition {
	private List<SectionReference> sections;
}
