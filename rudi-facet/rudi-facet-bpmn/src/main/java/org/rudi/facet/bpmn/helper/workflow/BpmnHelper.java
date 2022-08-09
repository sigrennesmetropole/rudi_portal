/**
 * 
 */
package org.rudi.facet.bpmn.helper.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.service.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FNI18300
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BpmnHelper {

	public static final String DEFAULT_ACTION = "default";
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmnHelper.class);

	@Value("${rudi.bpmn.role.name}")
	private String adminRoleName;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Autowired
	private FormHelper formHelper;

	@Autowired
	private final ApplicationContext applicationContext;


	/**
	 * Retourne la tâche activiti par son id
	 * 
	 * @param taskId
	 * @return
	 */
	public org.activiti.engine.task.Task queryTaskById(String taskId) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Retourne la tâche activiti par son id en tant qu'admin ou non
	 * 
	 * @param taskId
	 * @param isAdmin
	 * @return
	 */
	public org.activiti.engine.task.Task queryTaskById(String taskId, boolean asAdmin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		applyACLCriteria(taskQuery, asAdmin);
		applyTaskIdCriteria(taskQuery, taskId);
		applySortCriteria(taskQuery);

		List<org.activiti.engine.task.Task> tasks = taskQuery.list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	public org.activiti.engine.task.Task queryTaskByAssetId(Long assetId) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		taskQuery.taskVariableValueEquals(TaskConstants.ME_ID, assetId);

		List<org.activiti.engine.task.Task> tasks = taskQuery.list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	public org.activiti.engine.task.Task saveTask(org.activiti.engine.task.Task task) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		return taskService.saveTask(task);
	}

	/**
	 * Applique les critères de filtrage d'assignation
	 * 
	 * @param taskQuery
	 * @param asAdmin
	 */
	public void applyACLCriteria(TaskQuery taskQuery, boolean asAdmin) {
		String username = utilContextHelper.getAuthenticatedUser().getLogin();
		boolean userIsAdmin = utilContextHelper.hasRole(adminRoleName);
		List<String> roleNames = utilContextHelper.getAuthenticatedUser().getRoles();

		// si on a pas de demandé "en tant qu'admin et que l'on effectivement admin on ne filtre pas
		if (!(userIsAdmin && asAdmin)) {
			if (CollectionUtils.isNotEmpty(roleNames)) {
				taskQuery.or().taskCandidateOrAssigned(username).taskCandidateGroupIn(roleNames).endOr();
			} else {
				taskQuery.taskCandidateOrAssigned(username);
			}
		}
	}

	/**
	 * Applique le critère de tri pas défaut
	 * 
	 * @param taskQuery
	 */
	public void applySortCriteria(TaskQuery taskQuery) {
		taskQuery.orderByTaskPriority().asc().orderByTaskCreateTime().desc();
	}

	/**
	 * Applique le critère taskId
	 * 
	 * @param taskQuery
	 * @param taskId
	 */
	public void applyTaskIdCriteria(TaskQuery taskQuery, String taskId) {
		taskQuery.taskId(taskId);
	}

	/**
	 * Retourne la ProcessInstance d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public ProcessInstance lookupProcessInstance(org.activiti.engine.task.Task task) {
		ProcessInstance result = null;
		RuntimeService runtimeService = processEngine.getRuntimeService();
		String processInstanceId = task.getProcessInstanceId();
		List<ProcessInstance> associatedInstances = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).list();
		if (CollectionUtils.isNotEmpty(associatedInstances)) {
			result = associatedInstances.get(0);
		}
		return result;
	}

	/**
	 * Retourne la businessKey d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public String lookupProcessInstanceBusinessKey(org.activiti.engine.task.Task task) {
		ProcessInstance processInstance = lookupProcessInstance(task);
		return processInstance != null ? processInstance.getBusinessKey() : null;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends AssetDescriptionEntity> lookupProcessInstanceAssetType(org.activiti.engine.task.Task task)
			throws InvalidDataException {
		Class<? extends AssetDescriptionEntity> result = null;
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, VariableInstance> variables = runtimeService.getVariableInstances(task.getExecutionId());
		VariableInstance variableInstance = variables.get(TaskConstants.ME_TYPE);
		if (variableInstance != null) {
			try {
				result = (Class<? extends AssetDescriptionEntity>) Thread.currentThread().getContextClassLoader()
						.loadClass(variableInstance.getTextValue());
			} catch (ClassNotFoundException e) {
				throw new InvalidDataException(String.format("Failed to get type %s", variableInstance.getTextValue()),
						e);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param contextDescription
	 * @return l'id de process instance pour un contexte
	 */
	public String lookupProcessInstanceBusinessKey(String processDefinitionKey, Integer version) {
		String result = null;
		RepositoryService repositoryService = processEngine.getRepositoryService();

		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().active()
				.processDefinitionKey(processDefinitionKey);
		if (version != null) {
			processDefinitionQuery.processDefinitionVersion(version);
		} else {
			processDefinitionQuery.latestVersion();
		}
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			result = processDefinitions.get(0).getKey();
		}
		return result;
	}

	/**
	 * Recherche la tâche utilisateur de la task
	 * 
	 * @param task
	 * @return
	 */
	public UserTask lookupUserTask(org.activiti.engine.task.Task task) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			if (flowElement instanceof UserTask) {
				return (UserTask) flowElement;
			}
		}
		return null;
	}

	/**
	 * Calcule la liste des actions d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public List<Action> computeTaskActions(org.activiti.engine.task.Task task) {
		List<Action> result = new ArrayList<>();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			if (flowElement instanceof UserTask) {
				handleUserTask(result, flowElement);
			}
		}
		if (result.isEmpty()) {
			Action action = new Action();
			action.setLabel("Envoyer");
			action.setName(DEFAULT_ACTION);
			result.add(action);
		}
		for (Action action : result) {
			try {
				action.setForm(formHelper.lookupForm(task, action.getName()));
			} catch (Exception e) {
				log.warn("Failed to set form for action {}", action);
			}
		}
		return result;
	}

	/**
	 * Ajoute un candidat sur une tâche
	 * 
	 * @param taskId
	 * @param userId
	 */
	public void addTaskCandidateUser(String taskId, String userLogin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		taskService.addCandidateUser(taskId, userLogin);
	}

	/**
	 * Supprime un candidate d'une tâche
	 */
	public void removeTaskCandidateUser(String taskId, String userLogin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		taskService.deleteCandidateUser(taskId, userLogin);
	}

	/**
	 * Retourne le séquence flow correspondant à une action
	 * 
	 * @param task
	 * @param actionName
	 * @return
	 */
	public SequenceFlow lookupSequenceFlow(org.activiti.engine.task.Task task, String actionName) {
		SequenceFlow result = null;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			result = lookupSequenceFlowInUserTask(flowElement, actionName);
		}
		return result;
	}

	private SequenceFlow lookupSequenceFlowInUserTask(FlowElement flowElement, String actionName) {
		SequenceFlow result = null;
		if (flowElement instanceof UserTask) {
			List<SequenceFlow> outgoings = ((UserTask) flowElement).getOutgoingFlows();
			for (SequenceFlow outgoing : outgoings) {
				result = lookupSequenceFlowInGateway(outgoing, actionName);
				if (result == null) {
					result = lookupSequenceFlowInTask(outgoing, actionName);
				}
			}
		}
		return result;
	}

	private SequenceFlow lookupSequenceFlowInTask(SequenceFlow outgoing, String actionName) {
		SequenceFlow result = null;
		FlowElement subFlowElement = outgoing.getTargetFlowElement();
		if (subFlowElement instanceof org.activiti.bpmn.model.Task) {
			if (outgoing.getName().equalsIgnoreCase(actionName)) {
				result = outgoing;
			}
		}
		return result;
	}

	private SequenceFlow lookupSequenceFlowInGateway(SequenceFlow outgoing, String actionName) {
		SequenceFlow result = null;
		FlowElement subFlowElement = outgoing.getTargetFlowElement();
		if (subFlowElement instanceof ExclusiveGateway) {
			List<SequenceFlow> subOutgoings = ((ExclusiveGateway) subFlowElement).getOutgoingFlows();
			for (SequenceFlow sequenceFlow : subOutgoings) {
				if (sequenceFlow.getName().equalsIgnoreCase(actionName)) {
					result = sequenceFlow;
					break;
				}
			}
		}
		return result;
	}

	private void handleUserTask(List<Action> result, FlowElement flowElement) {
		List<SequenceFlow> outgoings = ((UserTask) flowElement).getOutgoingFlows();
		// on parcourt les flow de sortie
		for (SequenceFlow outgoing : outgoings) {
			handleExclusiveGateway(result, outgoing);
			handleTaskFlow(result, outgoing);
		}
	}

	private void handleExclusiveGateway(List<Action> result, SequenceFlow outgoing) {
		FlowElement subFlowElement = outgoing.getTargetFlowElement();
		// si le flow est un gateway exclusive on utilise les flow de sortie de la gateway
		if (subFlowElement instanceof ExclusiveGateway) {
			List<SequenceFlow> subOutgoings = ((ExclusiveGateway) subFlowElement).getOutgoingFlows();
			for (SequenceFlow sequenceFlow : subOutgoings) {
				Action action = createAction(sequenceFlow);
				result.add(action);
			}
		}
	}

	private void handleTaskFlow(List<Action> result, SequenceFlow outgoing) {
		FlowElement subFlowElement = outgoing.getTargetFlowElement();
		// si le flow de sortie est une tâche on prend directement le flow
		if (subFlowElement instanceof org.activiti.bpmn.model.Task) {
			Action action = createAction(outgoing);
			result.add(action);
		}
	}

	private Action createAction(SequenceFlow sequenceFlow) {
		Action action = new Action();
		action.setLabel(sequenceFlow.getDocumentation());
		action.setName(sequenceFlow.getName());
		return action;
	}

	public List<String> recomputeCandidateUsers(Task task) {
		List<String> result = new ArrayList<>();
		UserTask userTask = lookupUserTask(task);
		// Liste de script à executer pour recalculer les users
		List<String> candidateUsers = userTask.getCandidateUsers();
		for(String candidateUser  : candidateUsers) {
			try {
				result.addAll(executeCandidateUser(candidateUser, task));
			} catch (InvocationTargetException | IllegalAccessException exception) {
				LOGGER.error("Exeception thrown during scriplet execution", exception);
			}
		}
		return result;
	}

	private List<String> executeCandidateUser(String candidateUser, Task task) throws InvocationTargetException, IllegalAccessException {
		String inputWithoutBrackets = removeBrackets(candidateUser);
		String objectName = extractObject(inputWithoutBrackets);
		String methodName = extractMethod(candidateUser);
		String[] parametersFromScriptlet = extractParameters(candidateUser);
		Object bean = applicationContext.getBean(objectName);
		Method[] methods = bean.getClass().getDeclaredMethods();
		Method targetMethod = lookupMethod(methods, methodName, parametersFromScriptlet.length);
		ExecutionEntity executionEntity = lookupExecution(task);
		Object[] parametersConverted = convertParameters(executionEntity, targetMethod.getParameters(), parametersFromScriptlet);
		Object result  = targetMethod.invoke(bean, parametersConverted);
		return convertResult(result);
	}

	private Object[] convertParameters(ExecutionEntity entity, Parameter[] trueTypes, String[] valuesGot) {
		List<Object> arguments = new ArrayList<>();
		arguments.add(null);
 		arguments.add(entity);
		if (ArrayUtils.isNotEmpty(valuesGot)) {
			for (int i = 2; i < valuesGot.length; i++) {
				if(trueTypes[i].getType().equals(String.class)) {
					arguments.add(removeDoubleQuotes(valuesGot[i]));
				} else if(trueTypes[i].getType().equals(int.class) || trueTypes[i].getType().equals(Integer.class)) {
					arguments.add(Integer.valueOf(valuesGot[i].trim()));
				} else if(trueTypes[i].getType().equals(long.class) || trueTypes[i].getType().equals(Long.class)) {
					arguments.add(Long.valueOf(valuesGot[i]));
				} else if( trueTypes[i].getType().equals(float.class) || trueTypes[i].getType().equals(Float.class)) {
					arguments.add(Float.valueOf(valuesGot[i]));
				} else if( trueTypes[i].getType().equals(double.class) || trueTypes[i].getType().equals(Double.class)) {
					arguments.add(Double.valueOf(valuesGot[i]));
				} else if( trueTypes[i].getType().equals(boolean.class) || trueTypes[i].getType().equals(Boolean.class)) {
					arguments.add(Boolean.valueOf(valuesGot[i]));
				} else {
					LOGGER.error("Type of paramater not allowed", trueTypes[i].getType().toString());
					arguments.add(null);
				}
			}
		}
		return arguments.toArray();
	}

	private String removeDoubleQuotes(String input) {
		return input.replace("\"", "");
	}

	private Method lookupMethod(Method[] methods, String methodName, int numberOfParam) {
		for(Method method : methods) {
			if(method.getName().equals(methodName) && method.getParameterCount() == numberOfParam) {
				return method;
			}
		}
		return null;
	}

	private String extractObject(String input) {
		return input.substring(0, input.indexOf("."));
	}

	private String extractMethod(String input) {
		String result = null;
		input = removeBrackets(input);
		int index1 = input.indexOf('.');
		int index2 = input.indexOf('(');
		if (index1 >= 0 && index2 > index1) {
			result = input.substring(index1 + 1, index2);
		}
		return result;
	}

	private String[] extractParameters(String input) {
		input = removeBrackets(input);
		int index1 = input.indexOf('(');
		int index2 = input.indexOf(')');
		if (index1 >= 0 && index2 > index1) {
			input = input.substring(index1 + 1, index2);
			return input.split(",");
		} else {
			return new String[0];
		}
	}

	private String removeBrackets(String input) {
		if (input.startsWith("${")) {
			input = input.substring(2, input.length() - 1);
		}
		return input;
	}

	private List<String> convertResult(Object input) {
		if(input instanceof List) {
			return (List<String>) input;
		}
		List<String> result = new ArrayList<>();
		result.add((String) input);
		return result;
	}

	public boolean isCandidateUser(String taskId, String login) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		return taskService.getIdentityLinksForTask(taskId)
				.stream().filter(element -> element.getType().equals("USER"))
				.map(IdentityLink::getUserId)
				.collect(Collectors.toList())
				.contains(login);
	}

	/**
	 * @param task
	 */
	private ExecutionEntity lookupExecution(Task task) {
		ExecutionEntityImpl executionEntity = new ExecutionEntityImpl();
		executionEntity.setActive(true);
		executionEntity.setDeleted(true);
		executionEntity.setProcessInstance(executionEntity);
		executionEntity.setTenantId(task.getTenantId());
		executionEntity.setParentId(task.getParentTaskId());
		executionEntity.setProcessDefinitionId(task.getProcessDefinitionId());
		executionEntity.setProcessDefinitionName(lookupProcessInstance(task).getProcessDefinitionName());
		executionEntity.setProcessDefinitionKey(lookupProcessInstance(task).getProcessDefinitionKey());
		executionEntity
				.setProcessDefinitionVersion(lookupProcessInstance(task).getProcessDefinitionVersion());
		executionEntity.setBusinessKey(lookupProcessInstanceBusinessKey(task));
		executionEntity.setStartTime(task.getCreateTime());
		executionEntity.setDescription(task.getDescription());
		executionEntity.setVariables(task.getProcessVariables());
		executionEntity.setVariablesLocal(task.getTaskLocalVariables());
		return executionEntity;
	}
}
