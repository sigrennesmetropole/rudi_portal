/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.bpmn.core.bean.FormSectionDefinition;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.rudi.facet.bpmn.dao.form.FormDefinitionCustomDao;
import org.rudi.facet.bpmn.dao.form.FormDefinitionDao;
import org.rudi.facet.bpmn.dao.form.ProcessFormDefinitionCustomDao;
import org.rudi.facet.bpmn.dao.form.ProcessFromDefintionDao;
import org.rudi.facet.bpmn.dao.form.SectionDefinitionCustomDao;
import org.rudi.facet.bpmn.dao.form.SectionDefinitionDao;
import org.rudi.facet.bpmn.entity.form.FormDefinitionEntity;
import org.rudi.facet.bpmn.entity.form.FormSectionDefinitionEntity;
import org.rudi.facet.bpmn.entity.form.ProcessFormDefinitionEntity;
import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;
import org.rudi.facet.bpmn.mapper.form.FormDefinitionMapper;
import org.rudi.facet.bpmn.mapper.form.FormSectionDefinitionMapper;
import org.rudi.facet.bpmn.mapper.form.ProcessFormDefinitionMapper;
import org.rudi.facet.bpmn.mapper.form.SectionDefinitionMapper;
import org.rudi.facet.bpmn.service.FormService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author FNI18300
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FormServiceImpl implements FormService {

	private static final String FORM_DEFINITION_FILE_NAME_SEPARATOR = "__";
	private final SectionDefinitionDao sectionDefintionDao;

	private final SectionDefinitionCustomDao sectionDefintionCustomDao;

	private final FormDefinitionDao formDefintionDao;

	private final FormDefinitionCustomDao formDefintionCustomDao;

	private final ProcessFromDefintionDao processFormDefintionDao;

	private final ProcessFormDefinitionCustomDao processFormDefinitionCustomDao;

	private final SectionDefinitionMapper sectionDefinitionMapper;

	private final FormSectionDefinitionMapper formSectionDefinitionMapper;

	private final FormDefinitionMapper formDefinitionMapper;

	private final ProcessFormDefinitionMapper processFormDefinitionMapper;

	private final ResourceHelper resourceHelper;

	private final SectionAdaptor sectionAdaptor = this.new SectionAdaptor();
	private final FormAdaptor formAdaptor = this.new FormAdaptor();
	private final ProcessFormAdaptor processFormAdaptor = this.new ProcessFormAdaptor();
	private final JsonResourceReader jsonResourceReader;
	private final DefinitionResources definitionResources;

	@Override
	@Transactional(readOnly = false)
	public SectionDefinition createSectionDefinition(SectionDefinition section) {
		validateSection(section);
		SectionDefinitionEntity entity = sectionDefinitionMapper.dtoToEntity(section);
		entity.setUuid(UUID.randomUUID());
		sectionDefintionDao.save(entity);
		return sectionDefinitionMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public SectionDefinition updateSectionDefinition(SectionDefinition section) {
		validateSection(section);
		SectionDefinitionEntity entity = loadSectionDefinition(section.getUuid());
		sectionDefinitionMapper.dtoToEntity(section, entity);
		sectionDefintionDao.save(entity);
		return sectionDefinitionMapper.entityToDto(entity);
	}

	private SectionDefinition createOrUpdateSectionDefinition(SectionDefinition section) {
		return sectionAdaptor.createOrUpdateElement(section);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSectionDefinition(UUID sectionUuid) {
		SectionDefinitionEntity entity = loadSectionDefinition(sectionUuid);
		sectionDefintionDao.delete(entity);
	}

	@Override
	public SectionDefinition getSectionDefinition(UUID sectionUuid) {
		return sectionDefinitionMapper.entityToDto(loadSectionDefinition(sectionUuid));
	}

	private void validateSection(SectionDefinition section) {
		if (section == null) {
			throw new IllegalArgumentException("Section is required");
		} else if (StringUtils.isEmpty(section.getName())) {
			throw new IllegalArgumentException("Section name is required");
		}
	}

	private SectionDefinitionEntity loadSectionDefinition(UUID sectionUuid) {
		SectionDefinitionEntity entity = sectionDefintionDao.findByUuid(sectionUuid);
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + sectionUuid);
		}
		return entity;
	}

	@Override
	public Page<SectionDefinition> searchSectionDefinitions(SectionDefinitionSearchCriteria searchCriteria,
			Pageable pageable) {
		return sectionDefinitionMapper
				.entitiesToDto(sectionDefintionCustomDao.searchSectionDefinitions(searchCriteria, pageable), pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public FormDefinition createFormDefinition(FormDefinition form) {
		FormDefinitionEntity entity = formDefinitionMapper.dtoToEntity(form);
		if (CollectionUtils.isNotEmpty(form.getFormSectionDefinitions())) {
			for (FormSectionDefinition formSectionDefinition : form.getFormSectionDefinitions()) {
				SectionDefinitionEntity section = loadSectionDefinition(
						formSectionDefinition.getSectionDefinition().getUuid());
				FormSectionDefinitionEntity formSectionDefinitionEntity = formSectionDefinitionMapper
						.dtoToEntity(formSectionDefinition);
				formSectionDefinitionEntity.setUuid(UUID.randomUUID());
				formSectionDefinitionEntity.setSectionDefinition(section);
				entity.addFormSectionDefinition(formSectionDefinitionEntity);
			}
		}
		entity.setUuid(UUID.randomUUID());
		formDefintionDao.save(entity);
		return formDefinitionMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public FormDefinition updateFormDefinition(FormDefinition form) {
		validateForm(form);
		FormDefinitionEntity entity = loadFormDefinition(form.getUuid());
		formDefinitionMapper.dtoToEntity(form, entity);

		// ajout des formSection manquant
		if (CollectionUtils.isNotEmpty(form.getFormSectionDefinitions())) {
			for (FormSectionDefinition formSectionDefinition : form.getFormSectionDefinitions()) {
				FormSectionDefinitionEntity formSectionDefinitionEntity = entity
						.lookupFormSectionDefinition(formSectionDefinition.getUuid());
				if (formSectionDefinitionEntity == null) {
					formSectionDefinitionEntity = formSectionDefinitionMapper.dtoToEntity(formSectionDefinition);
					formSectionDefinitionEntity.setUuid(UUID.randomUUID());
					formSectionDefinition.setUuid(formSectionDefinitionEntity.getUuid());
				} else {
					formSectionDefinitionMapper.dtoToEntity(formSectionDefinition, formSectionDefinitionEntity);
				}
				SectionDefinitionEntity section = loadSectionDefinition(
						formSectionDefinition.getSectionDefinition().getUuid());
				formSectionDefinitionEntity.setSectionDefinition(section);
				entity.addFormSectionDefinition(formSectionDefinitionEntity);
			}
		}
		// suppression des formSection en trop
		Iterator<FormSectionDefinitionEntity> it = entity.getFormSectionDefinitions().iterator();
		while (it.hasNext()) {
			FormSectionDefinitionEntity formSectionDefinitionEntity = it.next();
			if (CollectionUtils.isEmpty(form.getFormSectionDefinitions()) || form.getFormSectionDefinitions().stream()
					.noneMatch(f -> formSectionDefinitionEntity.getUuid().equals(f.getUuid()))) {
				it.remove();
			}
		}
		formDefintionDao.save(entity);
		return formDefinitionMapper.entityToDto(entity);
	}

	private FormDefinition createOrUpdateFormDefinition(FormDefinition form) {
		return formAdaptor.createOrUpdateElement(form);
	}

	@Override
	@Transactional(readOnly = false)
	public FormDefinition addSectionDefinition(UUID formUuid, UUID sectionUuid, boolean readOnly, Integer position) {
		FormDefinitionEntity entity = loadFormDefinition(formUuid);
		SectionDefinitionEntity section = loadSectionDefinition(sectionUuid);
		FormSectionDefinitionEntity formSectionDefinitionEntity = new FormSectionDefinitionEntity();
		formSectionDefinitionEntity
				.setOrder(position != null ? position : entity.getFormSectionDefinitions().size() * 10);
		formSectionDefinitionEntity.setReadOnly(readOnly);
		formSectionDefinitionEntity.setSectionDefinition(section);
		entity.addFormSectionDefinition(formSectionDefinitionEntity);
		formDefintionDao.save(entity);
		return formDefinitionMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public FormDefinition removeFormSectionDefinition(UUID formUuid, UUID formSectionUuid) {
		FormDefinitionEntity entity = loadFormDefinition(formUuid);
		Iterator<FormSectionDefinitionEntity> it = entity.getFormSectionDefinitions().iterator();
		while (it.hasNext()) {
			FormSectionDefinitionEntity formSectionDefinitionEntity = it.next();
			if (formSectionDefinitionEntity.getUuid().equals(formSectionUuid)) {
				it.remove();
			}
		}
		formDefintionDao.save(entity);
		return formDefinitionMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteFormDefinition(UUID formUuid) {
		FormDefinitionEntity entity = loadFormDefinition(formUuid);
		formDefintionDao.delete(entity);
	}

	@Override
	public Page<FormDefinition> searchFormDefinitions(FormDefinitionSearchCriteria searchCriteria, Pageable pageable) {
		return formDefinitionMapper
				.entitiesToDto(formDefintionCustomDao.searchFormDefinitions(searchCriteria, pageable), pageable);
	}

	@Override
	public FormDefinition getFormDefinition(UUID formDefinitionUuid) {
		return formDefinitionMapper.entityToDto(loadFormDefinition(formDefinitionUuid));
	}

	private void validateForm(FormDefinition form) {
		if (form == null) {
			throw new IllegalArgumentException("Form is required");
		} else if (StringUtils.isEmpty(form.getName())) {
			throw new IllegalArgumentException("Form name is required");
		}
	}

	private FormDefinitionEntity loadFormDefinition(UUID formUuid) {
		FormDefinitionEntity entity = formDefintionDao.findByUuid(formUuid);
		if (entity == null) {
			throw new IllegalArgumentException("Missing ressource:" + formUuid);
		}
		return entity;
	}

	@Override
	@Transactional(readOnly = false)
	public ProcessFormDefinition createProcessFormDefinition(ProcessFormDefinition processFormDefinition) {
		validateProcessForm(processFormDefinition);
		ProcessFormDefinitionEntity entity = processFormDefinitionMapper.dtoToEntity(processFormDefinition);
		entity.setUuid(UUID.randomUUID());
		FormDefinitionEntity formDefinitionEntity = loadFormDefinition(
				processFormDefinition.getFormDefinition().getUuid());
		entity.setFormDefinition(formDefinitionEntity);
		processFormDefintionDao.save(entity);
		return processFormDefinitionMapper.entityToDto(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public ProcessFormDefinition updateProcessFormDefinition(ProcessFormDefinition processFormDefinition) {
		validateProcessForm(processFormDefinition);
		ProcessFormDefinitionEntity entity = loadProcessFormDefinition(processFormDefinition.getUuid());
		processFormDefinitionMapper.dtoToEntity(processFormDefinition, entity);
		FormDefinitionEntity formDefinitionEntity = loadFormDefinition(
				processFormDefinition.getFormDefinition().getUuid());
		entity.setFormDefinition(formDefinitionEntity);
		processFormDefintionDao.save(entity);
		return processFormDefinitionMapper.entityToDto(entity);
	}

	private ProcessFormDefinition createOrUpdateProcessFormDefinition(ProcessFormDefinition processFormDefinition) {
		return processFormAdaptor.createOrUpdateElement(processFormDefinition);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteProcessFormDefinition(UUID processFormUuid) {
		ProcessFormDefinitionEntity entity = loadProcessFormDefinition(processFormUuid);
		processFormDefintionDao.delete(entity);
	}

	@Override
	public Page<ProcessFormDefinition> searchProcessFormDefinitions(ProcessFormDefinitionSearchCriteria searchCriteria,
			Pageable pageable) {
		return processFormDefinitionMapper.entitiesToDto(
				processFormDefinitionCustomDao.searchProcessFormDefintions(searchCriteria, pageable), pageable);
	}

	private void validateProcessForm(ProcessFormDefinition processForm) {
		if (processForm == null) {
			throw new IllegalArgumentException("Process form is required");
		} else if (StringUtils.isEmpty(processForm.getProcessDefinitionId())) {
			throw new IllegalArgumentException("Process form definition is required");
		} else if (StringUtils.isEmpty(processForm.getUserTaskId())) {
			throw new IllegalArgumentException("Process form userTaskId is required");
		} else if (processForm.getFormDefinition() == null || processForm.getFormDefinition().getUuid() == null) {
			throw new IllegalArgumentException("Process form form is required");
		}
	}

	private ProcessFormDefinitionEntity loadProcessFormDefinition(UUID processFormUuid) {
		ProcessFormDefinitionEntity entity = processFormDefintionDao.findByUuid(processFormUuid);
		if (entity == null) {
			throw new IllegalArgumentException("Missing ressource:" + processFormUuid);
		}
		return entity;
	}

	@Override
	@Transactional // readOnly = false
	public Collection<ProcessFormDefinition> createOrUpdateAllSectionAndFormDefinitions() throws IOException {

		final Collection<SectionDefinition> sectionDefinitions = definitionResources.getSections();
		final Map<String, SectionDefinition> createdOrUpdatedSectionsByName = new HashMap<>(sectionDefinitions.size());
		for (final var sectionDefinition : sectionDefinitions) {
			final String referencedSectionName = sectionDefinition.getName();
			final var createdOrUpdatedSection = createOrUpdateSectionDefinition(sectionDefinition);
			createdOrUpdatedSectionsByName.put(referencedSectionName, createdOrUpdatedSection);
		}

		final Map<String, FormManualDefinition> formManualDefinitionsByName = definitionResources.getForms();
		final Collection<ProcessFormDefinition> createdOrUpdatedProcessFormDefinitions = new ArrayList<>(formManualDefinitionsByName.size());
		for (final Map.Entry<String, FormManualDefinition> entry : formManualDefinitionsByName.entrySet()) {
			final String formName = entry.getKey();
			final var formManualDefinition = entry.getValue();

			val form = new FormDefinition()
					.name(formName);

			var nextSectionOrder = 1;
			for (final var referencedSection : formManualDefinition.getSections()) {
				final var section = createdOrUpdatedSectionsByName.get(referencedSection.getName());
				form.addFormSectionDefinitionsItem(new FormSectionDefinition()
						.order(nextSectionOrder)
						.readOnly(false)
						.sectionDefinition(section));
				++nextSectionOrder;
			}

			final var createdOrUpdatedForm = createOrUpdateFormDefinition(form);
			final var criteria = createCriteriaFromSectionFormManualDefinitionFilename(formName);
			createdOrUpdatedProcessFormDefinitions.add(createOrUpdateProcessFormDefinition(new ProcessFormDefinition()
					.processDefinitionId(criteria.getProcessDefinitionId())
					.userTaskId(criteria.getUserTaskId())
					.actionName(criteria.getActionName())
					.formDefinition(createdOrUpdatedForm)));
		}

		return createdOrUpdatedProcessFormDefinitions;
	}

	/**
	 * @return null si le JSON ne correspond pas à un fichier JSON de définition de formulaire valide, ou en cas d'erreur
	 */
	@Nonnull
	private ProcessFormDefinitionSearchCriteria createCriteriaFromSectionFormManualDefinitionFilename(String formName) {
		if (formName != null) {
			final var splitFilename = formName.split(FORM_DEFINITION_FILE_NAME_SEPARATOR);
			if (splitFilename.length >= 2) {
				return ProcessFormDefinitionSearchCriteria.builder()
						.processDefinitionId(splitFilename[0])
						.userTaskId(splitFilename[1])
						.actionName(splitFilename.length > 2 ? splitFilename[2] : null)
						.build();
			}
		}
		throw new InvalidParameterException("Form name does not match process form definition naming convention \"<processDefinitionId>__<userTaskId>[__<actionName>]\" : " + formName);
	}

	interface AbstractFormElementAdaptor<T, C> {

		void validate(T element);

		C buildSearchCriteriaToFindExisting(T element);

		Page<T> search(C criteria, Pageable pageable);

		void beforeUpdate(T existingElement, T element);

		T create(T element);

		T update(T element);

		default T createOrUpdateElement(T element) {
			validate(element);

			val criteria = buildSearchCriteriaToFindExisting(element);
			val pageable = PageRequest.of(0, 1);
			val existingElements = search(criteria, pageable);
			val elementDoesNotExist = existingElements.getTotalElements() == 0;
			if (elementDoesNotExist) {
				return create(element);
			} else {
				val existingElement = existingElements.getContent().get(0);
				beforeUpdate(existingElement, element);
				return update(element);
			}
		}

	}

	private class SectionAdaptor implements AbstractFormElementAdaptor<SectionDefinition, SectionDefinitionSearchCriteria> {
		@Override
		public void validate(SectionDefinition element) {
			validateSection(element);
		}

		@Override
		public SectionDefinitionSearchCriteria buildSearchCriteriaToFindExisting(SectionDefinition element) {
			final var criteria = new SectionDefinitionSearchCriteria();
			criteria.setName(element.getName());
			return criteria;
		}

		@Override
		public Page<SectionDefinition> search(SectionDefinitionSearchCriteria criteria, Pageable pageable) {
			return searchSectionDefinitions(criteria, pageable);
		}

		@Override
		public void beforeUpdate(SectionDefinition existingElement, SectionDefinition element) {
			element.setUuid(existingElement.getUuid());
		}

		@Override
		public SectionDefinition create(SectionDefinition element) {
			return createSectionDefinition(element);
		}

		@Override
		public SectionDefinition update(SectionDefinition element) {
			return updateSectionDefinition(element);
		}
	}


	private class FormAdaptor implements AbstractFormElementAdaptor<FormDefinition, FormDefinitionSearchCriteria> {

		private final Comparator<SectionDefinition> sectionComparator = Comparator.comparing(SectionDefinition::getUuid);
		private final Comparator<FormSectionDefinition> formSectionComparator = Comparator.comparing(FormSectionDefinition::getSectionDefinition, sectionComparator);

		@Override
		public void validate(FormDefinition element) {
			validateForm(element);
		}

		@Override
		public FormDefinitionSearchCriteria buildSearchCriteriaToFindExisting(FormDefinition form) {
			final var criteria = new FormDefinitionSearchCriteria();
			criteria.setFormName(form.getName());
			return criteria;
		}

		@Override
		public Page<FormDefinition> search(FormDefinitionSearchCriteria criteria, Pageable pageable) {
			return searchFormDefinitions(criteria, pageable);
		}

		@Override
		public void beforeUpdate(FormDefinition existingElement, FormDefinition element) {
			element.setUuid(existingElement.getUuid());
			syncFormSectionDefinitions(existingElement, element);
		}

		private void syncFormSectionDefinitions(FormDefinition existingElement, FormDefinition element) {
			final var existingFormSections = new TreeSet<>(formSectionComparator);
			existingFormSections.addAll(existingElement.getFormSectionDefinitions());

			final var updatedFormSections = new TreeSet<>(formSectionComparator);
			updatedFormSections.addAll(element.getFormSectionDefinitions());

			final var modifiedFormSections = SetUtils.intersection(existingFormSections, updatedFormSections);
			final var newFormSections = SetUtils.difference(updatedFormSections, existingFormSections);
			element.setFormSectionDefinitions(new ArrayList<>(SetUtils.union(modifiedFormSections, newFormSections)));
		}

		@Override
		public FormDefinition create(FormDefinition element) {
			return createFormDefinition(element);
		}

		@Override
		public FormDefinition update(FormDefinition element) {
			return updateFormDefinition(element);
		}
	}


	private class ProcessFormAdaptor implements AbstractFormElementAdaptor<ProcessFormDefinition, ProcessFormDefinitionSearchCriteria> {
		@Override
		public void validate(ProcessFormDefinition element) {
			validateProcessForm(element);
		}

		@Override
		public ProcessFormDefinitionSearchCriteria buildSearchCriteriaToFindExisting(ProcessFormDefinition processForm) {
			final var criteria = new ProcessFormDefinitionSearchCriteria();
			criteria.setProcessDefinitionId(processForm.getProcessDefinitionId());
			criteria.setUserTaskId(processForm.getUserTaskId());
			criteria.setActionName(processForm.getActionName());
//			criteria.setRevision(processForm.getRevision()); // FIXME criteria.revision INT alors que processForm.revision STRING
			return criteria;
		}

		@Override
		public Page<ProcessFormDefinition> search(ProcessFormDefinitionSearchCriteria criteria, Pageable pageable) {
			return searchProcessFormDefinitions(criteria, pageable);
		}

		@Override
		public void beforeUpdate(ProcessFormDefinition existingElement, ProcessFormDefinition element) {
			element.setUuid(existingElement.getUuid());
		}

		@Override
		public ProcessFormDefinition create(ProcessFormDefinition element) {
			return createProcessFormDefinition(element);
		}

		@Override
		public ProcessFormDefinition update(ProcessFormDefinition element) {
			return updateProcessFormDefinition(element);
		}
	}
}
