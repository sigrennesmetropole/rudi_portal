/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.bpmn.core.bean.FormSectionDefinition;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.bpmn.core.bean.SectionDefinition;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FormServiceImpl implements FormService {

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

}
