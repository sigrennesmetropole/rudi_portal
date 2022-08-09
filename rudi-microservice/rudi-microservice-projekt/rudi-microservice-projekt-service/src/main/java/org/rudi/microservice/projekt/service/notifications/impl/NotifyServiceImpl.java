package org.rudi.microservice.projekt.service.notifications.impl;

import lombok.RequiredArgsConstructor;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.projekt.service.notifications.NotifyService;
import org.rudi.microservice.projekt.service.workflow.LinkedDatasetTaskServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

	private final ProcessEngine processEngine;
	private final BpmnHelper bpmnHelper;
	private final ACLHelper aclHelper;

	// chercher les tasks de type LD via processEngine
	private List<Task> getTasks() {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		// Type de tasks à chercher
		taskQuery.processDefinitionKey(LinkedDatasetTaskServiceImpl.PROCESS_DEFINITION_ID);
		List<Task> tasks = taskQuery.list();
		return tasks;
	}

	@Override
	public void handleAddOrganizationMember(UUID organizationUuid, UUID userUUid) {
		String login = aclHelper.getUserByUUID(userUUid).getLogin();
		List<Task> tasks = getTasks();
		if (CollectionUtils.isNotEmpty(tasks)) {
			for(Task task : tasks) {
				// Calcule et retourne la nouvelle liste des candidats éligibles
				List<String> potentialsUsers = bpmnHelper.recomputeCandidateUsers(task);
				// Si notre user en fait partie, alors on l'ajoute vraiment à la task
				if (CollectionUtils.isNotEmpty(potentialsUsers) && potentialsUsers.contains(login)) {
					// S'assurer que la task n'était pas déjà affectée à l'utilisateur
					if(!bpmnHelper.isCandidateUser(task.getId(), login)) {
						bpmnHelper.addTaskCandidateUser(task.getId(), login);
					}
				}
			}
		}
	}


	@Override
	public void handleRemoveOrganizationMember(UUID organizationUuid, UUID userUUid) {
		String login = aclHelper.getUserByUUID(userUUid).getLogin();
		List<Task> tasks = getTasks();
		if (CollectionUtils.isNotEmpty(tasks)) {
			for(Task task : tasks) {
				// Vérifier que notre user faisait partie des users auxquels la task était affectée
				if(bpmnHelper.isCandidateUser(task.getId(), login)) {
					// Calcule et retourne la nouvelle liste des candidats éligibles
					List<String> potentialsUsers = bpmnHelper.recomputeCandidateUsers(task);
					// Si le user ne fait plus partie de la liste recalculée, alors on le supp oui
					if (CollectionUtils.isNotEmpty(potentialsUsers) && !potentialsUsers.contains(login)) {
						bpmnHelper.removeTaskCandidateUser(task.getId(), login);
					}
				}
			}
		}
	}
}
