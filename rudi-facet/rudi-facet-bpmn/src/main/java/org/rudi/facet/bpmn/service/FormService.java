/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 */
public interface FormService {

	SectionDefinition createSectionDefinition(SectionDefinition section);

	SectionDefinition updateSectionDefinition(SectionDefinition section);

	void deleteSectionDefinition(UUID sectionUuid);

	SectionDefinition getSectionDefinition(UUID sectionUuid);

	Page<SectionDefinition> searchSectionDefinitions(SectionDefinitionSearchCriteria searchCriteria, Pageable pageable);

	FormDefinition createFormDefinition(FormDefinition form);

	FormDefinition updateFormDefinition(FormDefinition form);

	FormDefinition addSectionDefinition(UUID formUuid, UUID sectionUuid, boolean readOnly, Integer position);

	FormDefinition removeFormSectionDefinition(UUID formUuid, UUID formSectionUuid);

	void deleteFormDefinition(UUID formUuid);

	FormDefinition getFormDefinition(UUID sectionUuid);

	Page<FormDefinition> searchFormDefinitions(FormDefinitionSearchCriteria searchCriteria, Pageable pageable);

	ProcessFormDefinition createProcessFormDefinition(ProcessFormDefinition processFormDefinition);

	ProcessFormDefinition updateProcessFormDefinition(ProcessFormDefinition processFormDefinition);

	void deleteProcessFormDefinition(UUID processFormUuid);

	Page<ProcessFormDefinition> searchProcessFormDefinitions(ProcessFormDefinitionSearchCriteria searchCriteria,
			Pageable pageable);

	/**
	 * Crée ou met à jour toutes les sections définies dans les fichiers JSON
	 * <pre>bpmn/sections/`processDefinitionId`__`userTaskId`__`actionName`.json</pre>
	 * <p>
	 * Ainsi que tous les formulaires définis dans les fichiers JSON
	 * <pre>bpmn/forms/`processDefinitionId`__`userTaskId`__`actionName`.json</pre>
	 *
	 * @return toutes les formulaires créés ou mis à jour
	 */
	Collection<ProcessFormDefinition> createOrUpdateAllSectionAndFormDefinitions() throws IOException;
}
