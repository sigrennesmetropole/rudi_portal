/**
 * 
 */
package org.rudi.facet.bpmn.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.AssetDescription;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.TaskConstants;
import org.rudi.facet.bpmn.service.TaskQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author FNI18300
 * 
 * @param <E> l'entité
 * @param <D> le dto
 * @param <R> le dao
 * @param <A> le helper de construction
 * @param <H> le helper d'assignation
 * @param <W> le workflowcontext d'exécution
 * @param <S> le critère de recherche
 */
public abstract class AbstractTaskQueryServiceImpl<S extends TaskSearchCriteria> implements TaskQueryService<S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskQueryServiceImpl.class);

	@Value("${rudi.bpmn.role.name}")
	@Getter(value = AccessLevel.PROTECTED)
	private String adminRoleName;

	@Getter(value = AccessLevel.PROTECTED)
	private final ProcessEngine processEngine;

	@Getter(value = AccessLevel.PROTECTED)
	private final FormHelper formHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final BpmnHelper bpmnHelper;

	private final ApplicationContext applicationContext;

	private final UtilContextHelper utilContextHelper;

	private Map<Class<? extends AssetDescriptionEntity>, AssetDescriptionDao<AssetDescriptionEntity>> daos = new HashMap<>();

	private Map<Class<? extends AssetDescriptionEntity>, AssetDescriptionHelper<AssetDescriptionEntity, AssetDescription>> helpers = new HashMap<>();

	public AbstractTaskQueryServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, ApplicationContext applicationContext) {
		super();
		this.processEngine = processEngine;
		this.formHelper = formHelper;
		this.bpmnHelper = bpmnHelper;
		this.utilContextHelper = utilContextHelper;
		this.applicationContext = applicationContext;
	}

	@Override
	@Transactional(readOnly = true)
	public Task getTask(String taskId) throws InvalidDataException, FormDefinitionException {
		Task result = null;
		org.activiti.engine.task.Task task = bpmnHelper.queryTaskById(taskId, true);
		if (task != null) {
			result = convertTask(task);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Task> searchTasks(S taskSearchCriteria) throws InvalidDataException, FormDefinitionException {
		List<Task> results = null;
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		applyACLCriteria(taskQuery, taskSearchCriteria);
		applyCommonCriteria(taskQuery, taskSearchCriteria);
		applyExtentedCriteria(taskQuery, taskSearchCriteria);
		bpmnHelper.applySortCriteria(taskQuery);
		List<org.activiti.engine.task.Task> tasks = taskQuery.list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			results = new ArrayList<>(tasks.size());
			for (org.activiti.engine.task.Task originalTask : tasks) {
				Task task = convertTask(originalTask);
				if (task != null) {
					results.add(task);
				}
			}
		} else {
			results = new ArrayList<>();
		}
		return results;
	}

	protected Task convertTask(org.activiti.engine.task.Task task)
			throws InvalidDataException, FormDefinitionException {
		LOGGER.info("Task:{}", task);
		String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(task);
		Class<? extends AssetDescriptionEntity> assetClass = bpmnHelper.lookupProcessInstanceAssetType(task);
		UUID uuid = UUID.fromString(processInstanceBusinessKey);
		AssetDescriptionEntity assetDescriptionEntity = loadAndUpdateAssetDescription(assetClass, uuid);
		// ici on essaye de pas planter si la tâche tourne encore mais l'objet assetDescription
		// à disparu de la bdd
		if (assetDescriptionEntity != null) {
			Task result = lookupHelper(assetClass).createTaskFromWorkflow(task,
					assetClass.cast(assetDescriptionEntity));
			updateDtoAssetData(result, task, assetDescriptionEntity);
			return result;
		} else {
			return null;
		}
	}

	protected AssetDescriptionEntity loadAndUpdateAssetDescription(Class<? extends AssetDescriptionEntity> assetClass,
			UUID uuid) throws InvalidDataException {
		AssetDescriptionEntity assetDescription = lookupDao(assetClass).findByUuid(uuid);
		if (assetDescription != null) {
			assetDescription.setUpdatedDate(LocalDateTime.now());
			assetDescription.setUpdator(getCurrentLogin());
			lookupDao(assetClass).save(assetClass.cast(assetDescription));
		}
		return assetDescription;
	}

	/**
	 * Remplissage du formulaire principal à partir des données (celui des actions est fait dans la création de la task
	 * 
	 * @param task
	 * @param originalTask
	 * @param assetDescriptionEntity
	 * @throws InvalidDataException
	 * @throws FormDefinitionException
	 */
	protected void updateDtoAssetData(Task task, org.activiti.engine.task.Task originalTask,
			AssetDescriptionEntity assetDescriptionEntity) throws InvalidDataException, FormDefinitionException {
		Map<String, Object> datas = formHelper.hydrateData(assetDescriptionEntity.getData());
		Form form = formHelper.lookupForm(originalTask, null);
		if (form != null) {
			formHelper.fillForm(form, datas);
			task.getAsset().setForm(form);
		}
		if (CollectionUtils.isNotEmpty(task.getActions())) {
			for (Action action : task.getActions()) {
				if (action.getForm() != null) {
					formHelper.fillForm(action.getForm(), datas);
				}
			}
		}
	}

	private void applyACLCriteria(TaskQuery taskQuery, S taskSearchCriteria) {
		bpmnHelper.applyACLCriteria(taskQuery, taskSearchCriteria != null && taskSearchCriteria.isAsAdmin());
	}

	/**
	 * Application des critères propres au workflow
	 * 
	 * @param taskQuery          l'object activiti à enrichir
	 * @param taskSearchCriteria les critètes
	 */
	protected void applyCommonCriteria(TaskQuery taskQuery, S taskSearchCriteria) {
		if (StringUtils.isNotEmpty(taskSearchCriteria.getDescription())) {
			taskQuery.or()
					.processVariableValueLikeIgnoreCase(TaskConstants.DESCRIPTION,
							StringUtils.toRootLowerCase(taskSearchCriteria.getDescription()))
					.taskDescriptionLike(taskSearchCriteria.getDescription()).endOr();
		}
		if (taskSearchCriteria.getMinCreationDate() != null) {
			taskQuery.processVariableValueGreaterThanOrEqual(TaskConstants.CREATION_DATE,
					taskSearchCriteria.getMinCreationDate());
		}
		if (taskSearchCriteria.getMaxCreationDate() != null) {
			taskQuery.processVariableValueGreaterThanOrEqual(TaskConstants.CREATION_DATE,
					taskSearchCriteria.getMaxCreationDate());
		}
		if (CollectionUtils.isNotEmpty(taskSearchCriteria.getStatus())) {
			taskQuery.or();
			for (Status status : taskSearchCriteria.getStatus()) {
				taskQuery.processVariableValueEquals(TaskConstants.STATUS, status.name());
			}
			taskQuery.endOr();
		}
		if (CollectionUtils.isNotEmpty(taskSearchCriteria.getFunctionalStatus())) {
			taskQuery.or();
			for (String status : taskSearchCriteria.getFunctionalStatus()) {
				taskQuery.processVariableValueEquals(TaskConstants.FUNCTIONAL_STATUS, status);
			}
			taskQuery.endOr();
		}
	}

	protected void applyExtentedCriteria(TaskQuery taskQuery, S taskSearchCriteria) {
		// c'est ici qu'il faut étendre/surcharge la méthode pour prendre en compte des critères complémentaires
	}

	protected String getCurrentLogin() {
		return utilContextHelper.getAuthenticatedUser().getLogin();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AssetDescriptionDao<AssetDescriptionEntity> lookupDao(Class<? extends AssetDescriptionEntity> assetClass)
			throws InvalidDataException {
		AssetDescriptionDao<AssetDescriptionEntity> result = daos.get(assetClass);
		if (result == null) {
			try {
				String assetDaoClassName = assetClass.getName().replace("Entity", "Dao").replace("entity", "dao");
				Class<? extends AssetDescriptionDao> assetDaoClass = (Class<? extends AssetDescriptionDao>) Thread
						.currentThread().getContextClassLoader().loadClass(assetDaoClassName);
				result = applicationContext.getBean(assetDaoClass);
				daos.put(assetClass, result);
			} catch (Exception e) {
				throw new InvalidDataException(String.format("Failed to get dao for %s", assetClass), e);
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AssetDescriptionHelper<AssetDescriptionEntity, AssetDescription> lookupHelper(
			Class<? extends AssetDescriptionEntity> assetClass) throws InvalidDataException {
		AssetDescriptionHelper<AssetDescriptionEntity, AssetDescription> result = helpers.get(assetClass);
		if (result == null) {
			try {
				String assetHelperClassName = assetClass.getName().replace("Entity", "WorkflowHelper")
						.replace("entity", "helper").replace("storage", "service");
				Class<? extends AssetDescriptionHelper> assetHelperClass = (Class<? extends AssetDescriptionHelper>) Thread
						.currentThread().getContextClassLoader().loadClass(assetHelperClassName);
				result = applicationContext.getBean(assetHelperClass);
				helpers.put(assetClass, result);
			} catch (Exception e) {
				throw new InvalidDataException(String.format("Failed to get helper for %s", assetClass), e);
			}
		}
		return result;
	}

}
