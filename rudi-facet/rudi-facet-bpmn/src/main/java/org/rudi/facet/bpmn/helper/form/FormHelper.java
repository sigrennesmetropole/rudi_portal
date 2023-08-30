/**
 *
 */
package org.rudi.facet.bpmn.helper.form;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Field;
import org.rudi.bpmn.core.bean.FieldDefinition;
import org.rudi.bpmn.core.bean.FieldType;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.dao.form.ProcessFormDefinitionCustomDao;
import org.rudi.facet.bpmn.entity.form.ProcessFormDefinitionEntity;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.mapper.form.FormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
public class FormHelper {

	private static final String DEFAULT_DATA_FORMAT = "dd/MM/yyyy";

	private static final String DRAFT_USER_TASK_ID = "draft";

	@Autowired
	private FormMapper formMapper;

	private BpmnHelper bpmnHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProcessFormDefinitionCustomDao processFormDefinitionCustomDao;

	/**
	 * Parse une définition de formulaire
	 *
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> hydrateData(String datas) throws InvalidDataException {
		Map<String, Object> result = null;
		if (StringUtils.isNotEmpty(datas)) {
			ObjectReader objectReader = objectMapper.readerFor(Map.class);
			try {
				return objectReader.readValue(datas);
			} catch (IOException e) {
				throw new InvalidDataException("Failed to hydrate:" + datas, e);
			}
		} else {
			result = new HashMap<>();
		}
		return result;
	}

	/**
	 * Serialize une définition de formulaire
	 *
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateData(Map<String, Object> datas) throws InvalidDataException {
		String result = null;
		if (datas != null) {
			ObjectWriter objectWriter = objectMapper.writer();
			try {
				result = objectWriter.writeValueAsString(datas);
			} catch (JsonProcessingException e) {
				throw new InvalidDataException("Failed to deshydrate:" + datas, e);
			}
		}
		return result;
	}

	/**
	 * Retourne le formulaire le plus adapté à la tâche
	 *
	 * Lors de la recherche on ramène tous les formulaires pour le workflow, la révision courante ou null, la tâche utilisateur ou null ce qui permet
	 * d'avoir des formulaires génériques pour toutes les révisions ou pour toutes les tâches etc.
	 *
	 * @param input      la tâche
	 * @param actionName nom de l'action
	 * @return
	 * @throws FormDefinitionException
	 */
	public Form lookupForm(org.activiti.engine.task.Task input, String actionName) throws FormDefinitionException {
		Form result = null;
		ProcessFormDefinitionSearchCriteria searchCriteria = createSearchCriteria(input, actionName);
		Page<ProcessFormDefinitionEntity> processFormDefinitionEntities = processFormDefinitionCustomDao
				.searchProcessFormDefintions(searchCriteria, PageRequest.of(0, 1, createSortCriteria()));
		if (!processFormDefinitionEntities.isEmpty()) {
			ProcessFormDefinitionEntity processFormDefinitionEntity = processFormDefinitionEntities.getContent().get(0);
			try {
				result = formMapper.entityToDto(processFormDefinitionEntity.getFormDefinition());
			} catch (FormDefinitionException e) {
				log.warn("Failed to set form for task:" + input.getId(), e);
			}
		}
		return result;
	}

	public Form lookupDraftForm(String processDefinitionKey) throws FormDefinitionException {
		Form result = null;
		ProcessFormDefinitionSearchCriteria searchCriteria = createSearchCriteria(processDefinitionKey);
		Page<ProcessFormDefinitionEntity> processFormDefinitionEntities = processFormDefinitionCustomDao
				.searchProcessFormDefintions(searchCriteria, PageRequest.of(0, 1, createSortCriteria()));
		if (!processFormDefinitionEntities.isEmpty()) {
			ProcessFormDefinitionEntity processFormDefinitionEntity = processFormDefinitionEntities.getContent().get(0);
			try {
				result = formMapper.entityToDto(processFormDefinitionEntity.getFormDefinition());
			} catch (FormDefinitionException e) {
				log.warn("Failed to set form for draft task", e);
			}
		}
		return result;
	}

	/**
	 * Copie les données d'un formulaire source dans une cible. Le formulaire cible est considéré comme la référence. Ce qui signifie que seul les champs
	 * présents dans la cible sont copiés et les autres sont ingorés
	 *
	 * @param source
	 * @param target
	 */
	public void copyFormData(Form source, Form target) {
		if (source != null && target != null && CollectionUtils.isNotEmpty(target.getSections())) {
			for (Section section : target.getSections()) {
				copySectionData(source, section);
			}
		}
	}

	private void copySectionData(Form source, Section section) {
		if (CollectionUtils.isNotEmpty(section.getFields())) {
			for (Field targetField : section.getFields()) {
				Field sourceField = lookupField(source, targetField.getDefinition().getName());
				if (sourceField != null) {
					targetField.setValues(sourceField.getValues());
				}
			}
		}
	}

	/**
	 * Retourne un champ depuis un form par son nom
	 *
	 * @param form
	 * @param name
	 * @return
	 */
	public Field lookupField(Form form, String name) {
		Field result = null;
		if (form != null && CollectionUtils.isNotEmpty(form.getSections())) {
			for (Section section : form.getSections()) {
				result = lookupField(section, name);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public Field lookupField(Section section, String name) {
		Field result = null;
		if (CollectionUtils.isNotEmpty(section.getFields())) {
			for (Field field : section.getFields()) {
				if (field.getDefinition().getName().equals(name)) {
					result = field;
					break;
				}
			}
		}
		return result;
	}

	/**
	 *
	 * @param form
	 * @return la map des champs existants
	 */
	public Map<String, FieldDefinition> buildFieldDefinition(Form form) {
		Map<String, FieldDefinition> result = new HashMap<>();
		if (form != null && CollectionUtils.isNotEmpty(form.getSections())) {
			for (Section section : form.getSections()) {
				if (CollectionUtils.isNotEmpty(section.getFields())) {
					for (Field field : section.getFields()) {
						result.put(field.getDefinition().getName(), field.getDefinition());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Rempli le formulaire à partir de la map des données
	 *
	 * @param form
	 * @param datas
	 */
	public void fillForm(Form form, Map<String, Object> datas) {
		for (Section section : form.getSections()) {
			if (CollectionUtils.isNotEmpty(section.getFields())) {
				for (Field field : section.getFields()) {
					Object value = datas.get(field.getDefinition().getName());
					fillField(field, value);
				}
			}
		}
	}

	private void fillField(Field field, Object value) {
		if (value != null) {
			if (FieldType.LIST == field.getDefinition().getType()
					&& Boolean.TRUE.equals(field.getDefinition().getMultiple())) {
				fillFieldList(field, value);
			} else {
				field.addValuesItem(value.toString());
			}
		}
	}

	private void fillFieldList(Field field, Object value) {
		if (value instanceof Collection) {
			for (Object itemValue : ((Collection<?>) value)) {
				field.addValuesItem(itemValue.toString());
			}
		} else {
			field.addValuesItem(value.toString());
		}
	}

	/**
	 * Rempli la map à partir du formulaire
	 *
	 * @param form
	 * @param datas
	 * @throws FormConvertException
	 */
	public void fillMap(Form form, Map<String, Object> datas) throws FormConvertException {
		if (form != null && CollectionUtils.isNotEmpty(form.getSections())) {
			for (Section section : form.getSections()) {
				if (CollectionUtils.isNotEmpty(section.getFields())) {
					for (Field field : section.getFields()) {
						fillMap(field.getDefinition(), datas, field.getValues());
					}
				}
			}
		}
	}

	private void fillMap(FieldDefinition fieldDefinition, Map<String, Object> datas, List<String> values)
			throws FormConvertException {
		if (CollectionUtils.isNotEmpty(values)) {
			if (FieldType.LIST == fieldDefinition.getType() && Boolean.TRUE.equals(fieldDefinition.getMultiple())) {
				datas.put(fieldDefinition.getName(), values);
			} else {
				// il n'y a qu'une valeur car on gère le multiple que pour les listes à choix
				String value = values.get(0);
				Object convertedValue = convertValue(fieldDefinition, value);
				datas.put(fieldDefinition.getName(), convertedValue);
			}
		}
	}

	private Object convertValue(FieldDefinition fieldDefinition, String value) throws FormConvertException {
		Object result = null;
		try {
			switch (fieldDefinition.getType()) {
			case BOOLEAN:
				result = Boolean.valueOf(value);
				break;
			case DATE:
				String pattern = StringUtils.isNotEmpty(fieldDefinition.getExtendedType())
						? fieldDefinition.getExtendedType()
						: DEFAULT_DATA_FORMAT;
				SimpleDateFormat dataFormat = new SimpleDateFormat(pattern);
				result = dataFormat.parse(value);

				break;
			case DOUBLE:
				result = Double.valueOf(value);
				break;
			case LONG:
				result = Long.valueOf(value);
				break;
			default:
				result = value;
				break;
			}
		} catch (ParseException e) {
			throw new FormConvertException("Failed to convert value:" + value + " for field:" + fieldDefinition, e);
		}
		return result;
	}

	private ProcessFormDefinitionSearchCriteria createSearchCriteria(org.activiti.engine.task.Task input,
			String actionName) {
		ProcessInstance processInstance = lookupBpmnHelper().lookupProcessInstance(input);
		UserTask userTask = lookupBpmnHelper().lookupUserTask(input);
		return ProcessFormDefinitionSearchCriteria.builder()
				.processDefinitionId(processInstance.getProcessDefinitionKey())
				.revision(processInstance.getProcessDefinitionVersion()).acceptFlexRevision(true)
				.userTaskId(userTask.getId()).acceptFlexUserTaskId(true)
				.actionName(actionName != null ? actionName : "main").acceptFlexActionName(actionName == null).build();
	}

	private ProcessFormDefinitionSearchCriteria createSearchCriteria(String processDefinitionKey) {
		String processInstanceId = lookupBpmnHelper().lookupProcessInstanceBusinessKey(processDefinitionKey, null);
		return ProcessFormDefinitionSearchCriteria.builder().processDefinitionId(processInstanceId)
				.acceptFlexRevision(true).userTaskId(DRAFT_USER_TASK_ID).acceptFlexUserTaskId(true).build();
	}

	private Sort createSortCriteria() {
		return Sort.by(Order.asc("revision"), Order.asc("userTaskId"));
	}

	/**
	 * cicular reference
	 * 
	 * @return
	 */
	protected BpmnHelper lookupBpmnHelper() {
		if (bpmnHelper == null) {
			bpmnHelper = ApplicationContext.getBean(BpmnHelper.class);
		}
		return bpmnHelper;
	}
}
