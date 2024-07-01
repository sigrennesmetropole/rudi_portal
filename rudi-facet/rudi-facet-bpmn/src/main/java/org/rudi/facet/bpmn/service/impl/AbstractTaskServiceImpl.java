/**
 *
 */
package org.rudi.facet.bpmn.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.AssetDescription;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.BpmnInitializationException;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.AssignmentHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.AssetDescriptionActionListener;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.TaskConstants;
import org.rudi.facet.bpmn.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public abstract class AbstractTaskServiceImpl<E extends AssetDescriptionEntity, D extends AssetDescription, R extends AssetDescriptionDao<E>, A extends AssetDescriptionHelper<E, D>, H extends AssignmentHelper<E>>
		implements TaskService<D>, ActivitiEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskServiceImpl.class);

	private static final String INVALID_ASSET_DESCRIPTION_UUID_MESSAGE = "Invalid asset Uuid";

	private static final String ACTION_VARIABLE_NAME = "action";

	private static final Map<String, String> EXECUTION_ENTITIES = new HashMap<>();

	@Value("${rudi.bpmn.role.name}")
	@Getter(value = AccessLevel.PROTECTED)
	private String adminRoleName;

	@Getter(value = AccessLevel.PROTECTED)
	private final ProcessEngine processEngine;

	@Getter(value = AccessLevel.PROTECTED)
	private final FormHelper formHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final BpmnHelper bpmnHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final A assetDescriptionHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final R assetDescriptionDao;

	@Getter(value = AccessLevel.PROTECTED)
	private final H assignmentHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final UtilContextHelper utilContextHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final InitializationService initializationService;

	@Autowired(required = false)
	private List<AssetDescriptionActionListener<E>> assetListeners;

	protected AbstractTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService, R assetDescriptionDao,
			A assetDescriptionHelper, H assignmentHelper) {
		super();
		this.processEngine = processEngine;
		this.formHelper = formHelper;
		this.bpmnHelper = bpmnHelper;
		this.utilContextHelper = utilContextHelper;

		this.assetDescriptionHelper = assetDescriptionHelper;
		this.assetDescriptionDao = assetDescriptionDao;
		this.assignmentHelper = assignmentHelper;
		this.initializationService = initializationService;
	}

	@Override
	@Nullable
	public Form lookupDraftForm() throws FormDefinitionException {
		return formHelper.lookupDraftForm(getProcessDefinitionKey());
	}

	@Override
	@Transactional(readOnly = false)
	public Task createDraft(D assetDescription)
			throws FormConvertException, InvalidDataException, FormDefinitionException {
		// contrôle des paramètres
		if (assetDescription == null) {
			throw new IllegalArgumentException("Asset is mandatory");
		}
		// chargement de l'asset
		E assetDescriptionEntity = null;
		if (assetDescription.getUuid() != null) {
			assetDescriptionEntity = assetDescriptionDao.findByUuid(assetDescription.getUuid());
		}
		// s'il existe déjà on contrôle qu'on a pas démarré un workflow dessus
		if (assetDescriptionEntity != null) {
			if (bpmnHelper.queryTaskByAssetId(assetDescriptionEntity.getId()) != null
					&& assetDescriptionEntity.getStatus() != Status.DRAFT) {
				throw new IllegalArgumentException("Asset is already linked to a task");
			}
			// vérifications du droit pour l'utilisateur de créer la tache
			checkRightsOnInitEntity(assetDescriptionEntity);
		} else {
			// s'il existe pas => création de l'entité
			assetDescriptionEntity = assetDescriptionHelper.createAssetEntity(assetDescription);
		}
		// mise à jour des informations sur l'asset
		updateAssetCreation(assetDescriptionEntity);
		updateDraftAssetData(assetDescription, assetDescriptionEntity);

		// sauvegarde
		fireBeforeCreate(assetDescriptionEntity);
		assetDescriptionDao.save(assetDescriptionEntity);
		fireAfterCreate(assetDescriptionEntity);

		// conversion
		return assetDescriptionHelper.createTaskFromAsset(assetDescriptionEntity, lookupDraftForm());
	}

	@Override
	@Transactional(readOnly = false)
	public Task startTask(Task task) throws InvalidDataException, FormDefinitionException, FormConvertException {
		// contrôle des données d'entrée
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}
		// récupération du signalement draft
		E assetDescriptionEntity = assetDescriptionDao.findByUuid(task.getAsset().getUuid());
		if (assetDescriptionEntity == null || assetDescriptionEntity.getStatus() != Status.DRAFT) {
			throw new IllegalArgumentException("Invalid task");
		}

		// vérifications du droit pour l'utilisateur de créer la tache
		checkRightsOnInitEntity(assetDescriptionEntity);

		// mise à jour de l'entité
		assetDescriptionEntity = updateDraftAsset(task, assetDescriptionEntity);

		beforeStart(assetDescriptionEntity);

		// Démarrage du processus
		ProcessInstance processInstance = startProcessInstance(assetDescriptionEntity,
				buildProcessVariables(assetDescriptionEntity));

		// conversion
		Task outputTask = assetDescriptionHelper.createTaskFromAsset(assetDescriptionEntity, task.getAsset().getForm());
		outputTask.setId(processInstance.getId());

		afterStart(assetDescriptionEntity, processInstance);
		return outputTask;
	}

	/**
	 * pour surcharge éventuelle
	 *
	 * @param assetDescriptionEntity
	 */
	protected void beforeStart(E assetDescriptionEntity) {
		// rien par défaut
	}

	/**
	 * pour surcharge éventuelle
	 *
	 * @param assetDescriptionEntity
	 * @param processInstance
	 */
	protected void afterStart(E assetDescriptionEntity, ProcessInstance processInstance) {
		// rien par défaut
	}

	@Override
	@Transactional(readOnly = false)
	public Task claimTask(String taskId) {
		Task result = null;
		LOGGER.debug("Claim on task {}", taskId);
		boolean isAdmin = isCurrentAdmin();
		org.activiti.engine.task.Task originalTask = bpmnHelper.queryTaskById(taskId, isAdmin);
		if (originalTask != null) {
			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			LOGGER.debug("Claim on asset {}", processInstanceBusinessKey);
			UUID uuid = UUID.fromString(processInstanceBusinessKey);

			// le claim ne peut être fait que par un admin ou si la tâche n'est pas affectée
			if (isAdmin || StringUtils.isEmpty(originalTask.getAssignee())) {
				E assetDescriptionEntity = loadAndUpdateAssetDescription(uuid);

				org.activiti.engine.TaskService taskService = processEngine.getTaskService();
				taskService.claim(taskId, getCurrentLogin());

				// rechargement de la tâche après claim
				originalTask = bpmnHelper.queryTaskById(taskId);
				result = assetDescriptionHelper.createTaskFromWorkflow(originalTask, assetDescriptionEntity);
			} else {
				LOGGER.warn("Skip claim on task {} invalid assigneee {} vrs {}", taskId, originalTask.getAssignee(),
						getCurrentLogin());
				throw new IllegalArgumentException("Task is already assigned and you're not admin");
			}
		} else {
			throw new IllegalArgumentException("Task could not be claimed by you");
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void unclaimTask(String taskId) {
		LOGGER.debug("Unclaim on task {}", taskId);
		boolean isAdmin = isCurrentAdmin();
		org.activiti.engine.task.Task originalTask = bpmnHelper.queryTaskById(taskId, true);
		if (originalTask != null) {
			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			LOGGER.debug("Unclaim on asset {}", processInstanceBusinessKey);

			if (isAdmin || getCurrentLogin().equalsIgnoreCase(originalTask.getAssignee())) {
				UUID uuid = UUID.fromString(processInstanceBusinessKey);
				E assetDescriptionEntity = loadAndUpdateAssetDescription(uuid);
				assetDescriptionEntity.setAssignee(null);
				updateAssetCommon(assetDescriptionEntity);
				org.activiti.engine.TaskService taskService = processEngine.getTaskService();
				taskService.unclaim(taskId);
			} else {
				throw new IllegalArgumentException("Task could not be unclaimed by you");
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void doIt(String taskId, String actionName) throws InvalidDataException {
		LOGGER.debug("DoIt on task {}=>{}", taskId, actionName);
		org.activiti.engine.task.Task task = bpmnHelper.queryTaskById(taskId);
		if (task != null) {
			// l'action n'est possible que si l'on est assigné à la tâche
			if (getCurrentLogin().equalsIgnoreCase(task.getAssignee())) {
				SequenceFlow sequenceFlow = bpmnHelper.lookupSequenceFlow(task, actionName);
				if (sequenceFlow != null || BpmnHelper.DEFAULT_ACTION.equals(actionName)) {
					String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(task);
					UUID uuid = UUID.fromString(processInstanceBusinessKey);
					LOGGER.debug("DoIt on asset {}", processInstanceBusinessKey);
					E assetDescriptionEntity = loadAndUpdateAssetDescription(uuid);
					Map<String, Object> variables = buildProcessVariables(assetDescriptionEntity);
					variables.put(ACTION_VARIABLE_NAME, actionName);
					org.activiti.engine.TaskService taskService = processEngine.getTaskService();
					taskService.complete(taskId, variables);
					LOGGER.debug("Done on task {}=>{}", taskId, actionName);
				} else {
					LOGGER.warn("Skip doIt on task {} invalid actionname {}", taskId, actionName);
					throw new IllegalArgumentException("Action name is invalid");
				}
			} else {
				LOGGER.warn("Skip doIt on task {} invalid assigneee {} vrs {}", taskId, task.getAssignee(),
						getCurrentLogin());
				throw new IllegalArgumentException("Task is not assigned to you");
			}
		} else {
			LOGGER.warn("Skip doIt on task {} invalid id", taskId);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Task updateTask(Task task) throws FormDefinitionException, FormConvertException, InvalidDataException {
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}

		Task result = null;
		if (task.getId() != null) {
			result = updateRunningTask(task);
		} else {
			// récupération du signalement draft si on a pas trouvé de tâche associé
			// dans ce cas pas de controle d'accès puisqu'il n'y a pas encore d'affectation.
			E assetDescriptionEntity = assetDescriptionDao.findByUuid(task.getAsset().getUuid());
			if (assetDescriptionEntity != null && assetDescriptionEntity.getStatus() == Status.DRAFT) {
				assetDescriptionEntity = updateDraftAsset(task, assetDescriptionEntity);
				result = assetDescriptionHelper.createTaskFromAsset(assetDescriptionEntity, task.getAsset().getForm());
			} else {
				LOGGER.warn("Skip updateTask. Task does not exist or has a bad status");
				throw new IllegalArgumentException("Task does not exist or has a bad status");
			}
		}

		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void cancelDraft(UUID assetDescriptionUuid) {
		E assetDescription = assetDescriptionDao.findByUuid(assetDescriptionUuid);
		if (assetDescription == null) {
			throw new IllegalArgumentException(INVALID_ASSET_DESCRIPTION_UUID_MESSAGE);
		}
		if (Status.DRAFT != assetDescription.getStatus()) {
			throw new IllegalArgumentException("Invalid assetDescription status:" + assetDescription.getStatus());
		}
		try {
			fireBeforeDelete(assetDescription);
			assetDescriptionDao.delete(assetDescription);
			fireAfterDelete(assetDescription);
		} catch (Exception e) {
			LOGGER.warn("Failed to delete attachments", e);
		}
	}

	@Override
	public String getAssociatedTaskId(UUID assetUuid) {
		E assetDescription = assetDescriptionDao.findByUuid(assetUuid);
		org.activiti.engine.task.Task task = bpmnHelper.queryTaskByAssetId(assetDescription.getId());
		if (task != null) {
			return task.getId();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasTask(UUID assetUuid) {
		return getAssociatedTaskId(assetUuid) != null;
	}

	protected E loadAndUpdateAssetDescription(UUID uuid) {
		E assetDescription = assetDescriptionDao.findByUuid(uuid);
		if (assetDescription != null) {
			assetDescription.setUpdatedDate(LocalDateTime.now());
			assetDescription.setUpdator(getCurrentLogin());
			assetDescriptionDao.save(assetDescription);
		}
		return assetDescription;
	}

	protected ProcessInstance startProcessInstance(E assetDescriptionEntity, Map<String, Object> variables) {
		String processDefinitionKey = bpmnHelper
				.lookupProcessInstanceBusinessKey(assetDescriptionEntity.getProcessDefinitionKey(), null);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		return runtimeService.startProcessInstanceByKey(processDefinitionKey,
				assetDescriptionEntity.getUuid().toString(), variables);
	}

	@SuppressWarnings("unchecked")
	protected E updateDraftAsset(Task task, E assetDescriptionEntity)
			throws FormDefinitionException, FormConvertException, InvalidDataException {
		D assetDescription = (D) task.getAsset();

		// mise à jour de l'entité
		fireBeforeUpdate(assetDescriptionEntity);

		assetDescriptionHelper.updateAssetEntity(assetDescription, assetDescriptionEntity);

		// mise à jour des datas de l'entité
		updateDraftAssetData(assetDescription, assetDescriptionEntity);
		updateAssetCommon(assetDescriptionEntity);

		fireAfterUpdate(assetDescriptionEntity);

		return assetDescriptionEntity;
	}

	protected void updateDraftAssetData(D assetDescription, E assetDescriptionEntity)
			throws FormDefinitionException, FormConvertException, InvalidDataException {
		Map<String, Object> datas = formHelper.hydrateData(assetDescriptionEntity.getData());
		Form orignalForm = lookupOriginalDraftForm(assetDescriptionEntity);
		formHelper.copyFormData(assetDescription.getForm(), orignalForm);
		formHelper.fillMap(orignalForm, datas);
		assetDescriptionEntity.setData(formHelper.deshydrateData(datas));
	}

	protected Form lookupOriginalDraftForm(E assetDescriptionEntity) throws FormDefinitionException {
		return formHelper.lookupDraftForm(assetDescriptionEntity.getProcessDefinitionKey());
	}

	protected void updateAssetCreation(E assetDescriptionEntity) {
		assetDescriptionEntity.setProcessDefinitionKey(getProcessDefinitionKey());
		assetDescriptionEntity.setUpdatedDate(assetDescriptionEntity.getCreationDate());
		assetDescriptionEntity.setInitiator(utilContextHelper.getAuthenticatedUser().getLogin());
		assetDescriptionEntity.setStatus(Status.DRAFT);
	}

	protected void updateAssetCommon(E assetDescriptionEntity) {
		// mise à jour dernière date de modification
		assetDescriptionEntity.setUpdatedDate(LocalDateTime.now());
		assetDescriptionEntity.setUpdator(getCurrentLogin());
	}

	protected void updateTask(org.activiti.engine.task.Task originalTask, E assetDescriptionEntity) {
		originalTask.setDescription(assetDescriptionEntity.getDescription());
		originalTask.getTaskLocalVariables().put(TaskConstants.UPDATED_DATE, assetDescriptionEntity.getUpdatedDate());
		originalTask.getTaskLocalVariables().put(TaskConstants.DESCRIPTION, assetDescriptionEntity.getDescription());
		bpmnHelper.saveTask(originalTask);

	}

	@SuppressWarnings("unchecked")
	protected Task updateRunningTask(Task task)
			throws FormDefinitionException, FormConvertException, InvalidDataException {
		Task result = null;
		org.activiti.engine.task.Task originalTask = bpmnHelper.queryTaskById(task.getId());
		if (originalTask != null) {
			String login = getCurrentLogin();
			// l'action n'est possible que si l'on est assigné à la tâche
			if (login.equalsIgnoreCase(originalTask.getAssignee())) {

				// récupération de l'entité associée
				String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
				UUID uuid = UUID.fromString(processInstanceBusinessKey);
				E assetDescription = loadAndUpdateAssetDescription(uuid);

				if (checkUpdate(assetDescription)) {
					// mise à jour de l'entité si dans un état autorisé.
					assetDescriptionHelper.updateAssetEntity((D) task.getAsset(), assetDescription);
				}

				// mise à jour des datas de l'entité
				updateAssetData(task, originalTask, assetDescription);
				updateAssetCommon(assetDescription);

				// mise à jour de la tâche
				updateTask(originalTask, assetDescription);

				// conversion
				result = assetDescriptionHelper.createTaskFromWorkflow(originalTask, assetDescription);
				updateDtoAssetData(task, originalTask, assetDescription);
			} else {
				LOGGER.warn("Skip update on task {} invalid assigneee {} vrs {}", originalTask.getId(),
						originalTask.getAssignee(), login);
				throw new IllegalArgumentException("Task does no exists or not accessible by you");
			}
		} else {
			LOGGER.warn("Skip update on task {} unknown", task.getId());
			throw new IllegalArgumentException("Task does no exists or not accessible by you");
		}
		return result;
	}

	/**
	 * Mise à jour des données de formulaire
	 *
	 * @param task
	 * @param originalTask
	 * @param assetDescriptionEntity
	 * @throws InvalidDataException
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	protected void updateAssetData(Task task, org.activiti.engine.task.Task originalTask, E assetDescriptionEntity)
			throws InvalidDataException, FormDefinitionException, FormConvertException {
		// on recharge les données présentes
		Map<String, Object> datas = formHelper.hydrateData(assetDescriptionEntity.getData());
		// on récupère le formulaire global et on assigne les données
		fillMap(datas, task.getAsset().getForm(), originalTask, null);

		// pour chaque formulaire d'action on fait la même chose
		if (CollectionUtils.isNotEmpty(task.getActions())) {
			for (Action action : task.getActions()) {
				fillMap(datas, action.getForm(), originalTask, action.getName());
			}
		}

		assetDescriptionEntity.setData(formHelper.deshydrateData(datas));
	}

	/**
	 * remplissage de la map des données à partir d'un formulaire (avec écrasement des données présentes)
	 *
	 * @param datas
	 * @param form
	 * @param originalTask
	 * @param actionName
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	protected void fillMap(Map<String, Object> datas, Form form, org.activiti.engine.task.Task originalTask,
			String actionName) throws FormDefinitionException, FormConvertException {
		Form actionForm = formHelper.lookupForm(originalTask, actionName);
		formHelper.copyFormData(form, actionForm);
		formHelper.fillMap(actionForm, datas);
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
	protected void updateDtoAssetData(Task task, org.activiti.engine.task.Task originalTask, E assetDescriptionEntity)
			throws InvalidDataException, FormDefinitionException {
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

	/**
	 *
	 * @param assetDescriptionEntity
	 * @return
	 * @throws InvalidDataException
	 */
	protected Map<String, Object> buildProcessVariables(E assetDescriptionEntity) throws InvalidDataException {
		Map<String, Object> variables = new HashMap<>();
		if (assetDescriptionEntity != null) {
			variables.put(TaskConstants.ME_ID, assetDescriptionEntity.getId());
			variables.put(TaskConstants.ME_UUID, assetDescriptionEntity.getUuid());
			variables.put(TaskConstants.ME_TYPE, assetDescriptionEntity.getClass().getName());
			variables.put(TaskConstants.CREATION_DATE, assetDescriptionEntity.getCreationDate());
			variables.put(TaskConstants.STATUS, assetDescriptionEntity.getStatus().name());
			variables.put(TaskConstants.FUNCTIONAL_STATUS, assetDescriptionEntity.getFunctionalStatus());
			variables.put(TaskConstants.DESCRIPTION, assetDescriptionEntity.getDescription());

			fillProcessVariables(variables, assetDescriptionEntity);

			Map<String, Object> datas = formHelper.hydrateData(assetDescriptionEntity.getData());
			if (MapUtils.isNotEmpty(datas)) {
				for (Map.Entry<String, Object> data : datas.entrySet()) {
					variables.put(data.getKey(), data.getValue());
				}
			}
		}
		return variables;
	}

	/**
	 * Méthode pour ajouter des données dans les variables du processus
	 *
	 * @param variables
	 * @param assetDescriptionEntity
	 */
	protected void fillProcessVariables(Map<String, Object> variables, E assetDescriptionEntity) {
		// Nothing
	}

	@PostConstruct
	public void initialize() {
		processEngine.getRuntimeService().addEventListener(this);
	}

	@Override
	@Transactional(readOnly = false)
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
		case ENTITY_CREATED:
			cacheEntiy(event);
			break;
		case TASK_ASSIGNED:
			assign(event);
			break;
		default:
			// NOTHING
		}

	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

	protected void cacheEntiy(ActivitiEvent event) {
		ActivitiEntityEvent ea = (ActivitiEntityEvent) event;
		if (ea.getEntity() instanceof ExecutionEntity) {
			ExecutionEntity executionEntity = (ExecutionEntity) ea.getEntity();
			if (executionEntity.getBusinessKey() != null) {
				// stocke ici lors de la création de l'entité d'exécution d'un nouveau workflow,
				// la business key si elle est pas nulle
				// dans le workflow simple, on passe ici 2 fois (pour chaque étape)
				// mais la deuxième fois, il n'y a pas de businessKey
				EXECUTION_ENTITIES.put(executionEntity.getProcessInstanceId(), executionEntity.getBusinessKey());
			}
		}
	}

	protected void assign(ActivitiEvent event) {
		ActivitiEntityEvent ea = (ActivitiEntityEvent) event;
		if (ea.getEntity() instanceof org.activiti.engine.task.Task) {
			org.activiti.engine.task.Task originalTask = (org.activiti.engine.task.Task) ea.getEntity();

			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			if (processInstanceBusinessKey == null) {
				// si on a pas trouvé la business key, c'est sans doute que tout ça n'a pas été
				// encore flushé
				// du coup on ne peut pas retrouver toutes les informations
				processInstanceBusinessKey = EXECUTION_ENTITIES.get(event.getProcessInstanceId());
			}
			String assignee = originalTask.getAssignee();
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			E assetDescriptionEntity = loadAndUpdateAssetDescription(uuid);
			if (assetDescriptionEntity != null) {
				assetDescriptionEntity.setAssignee(assignee);
			} else {
				LOGGER.warn("Failed to update assignee for {} to {}", originalTask.getId(), originalTask.getAssignee());
			}
		}
	}

	protected boolean isCurrentAdmin() {
		return utilContextHelper.hasRole(adminRoleName);
	}

	protected String getCurrentLogin() {
		return utilContextHelper.getAuthenticatedUser().getLogin();
	}

	private void fireAfterCreate(E assetDescriptionEntity) {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.afterCreate(assetDescriptionEntity);
			}
		}
	}

	private void fireBeforeCreate(E assetDescriptionEntity) throws InvalidDataException {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.beforeCreate(assetDescriptionEntity);
			}
		}
	}

	private void fireAfterUpdate(E assetDescriptionEntity) {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.afterUpdate(assetDescriptionEntity);
			}
		}
	}

	private void fireBeforeUpdate(E assetDescriptionEntity) throws InvalidDataException {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.beforeUpdate(assetDescriptionEntity);
			}
		}
	}

	private void fireAfterDelete(E assetDescriptionEntity) {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.afterDelete(assetDescriptionEntity);
			}
		}
	}

	private void fireBeforeDelete(E assetDescriptionEntity) throws InvalidDataException {
		if (CollectionUtils.isNotEmpty(assetListeners)) {
			for (AssetDescriptionActionListener<E> assetListener : assetListeners) {
				assetListener.beforeDelete(assetDescriptionEntity);
			}
		}
	}

	/**
	 * @return le nom du fichier de processus associé à l'implémentation
	 */
	protected String computeBpmnFilename() {
		return "bpmn/" + getProcessDefinitionKey() + ".bpmn20.xml";
	}

	/**
	 * charge le fichier bpmn si il a changé
	 *
	 * @throws IOException
	 */
	protected void loadBpmn() throws IOException {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
		ProcessDefinition lastProcessDefinition = null;
		int version = -1;
		for (ProcessDefinition processDefinition : processDefinitions) {
			if (processDefinition.getKey().contentEquals(getProcessDefinitionKey())
					&& processDefinition.getVersion() > version) {
				lastProcessDefinition = processDefinition;
				version = processDefinition.getVersion();
			}
		}
		if (lastProcessDefinition == null) {
			loadProcessDefinition();
		} else {
			try (InputStream bpmnStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(computeBpmnFilename());
					InputStream lastProcessDefinitionStream = repositoryService.getResourceAsStream(
							lastProcessDefinition.getDeploymentId(), lastProcessDefinition.getResourceName());) {
				String lastProcessDefinitionMd5 = DigestUtils.md5DigestAsHex(lastProcessDefinitionStream);
				String bpmnMd5 = DigestUtils.md5DigestAsHex(bpmnStream);
				if (!lastProcessDefinitionMd5.equals(bpmnMd5)) {
					loadProcessDefinition();
				}
			} catch (Exception e) {
				log.error("Failed to load workflow", e);
			}
		}
	}

	/**
	 * Charge le fichier de processus
	 *
	 * @throws IOException
	 */
	protected void loadProcessDefinition() throws IOException {
		try (InputStream bpmnStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(computeBpmnFilename());) {
			DocumentContent documentContent = new DocumentContent(computeBpmnFilename(), "application/xml", -1,
					bpmnStream);
			initializationService.updateProcessDefinition(getProcessDefinitionKey(), documentContent);
		} catch (BpmnInitializationException e) {
			log.error("Failed to load workflow", e);
		}
	}

	protected abstract AbstractTaskServiceImpl<E, D, R, A, H> lookupMe();

	// assetDescriptionEntity utilisé dans les enfants
	protected boolean checkUpdate(E assetDescriptionEntity) {// NOSONAR
		return true;
	}

	protected void checkRightsOnInitEntity(E assetDescriptionEntity) throws IllegalArgumentException {
		// nothing to do by default
	}
}
