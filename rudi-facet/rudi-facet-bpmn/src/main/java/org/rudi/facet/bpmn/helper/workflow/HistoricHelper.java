/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.bpmn.core.bean.HistoricInformation;
import org.rudi.facet.bpmn.mapper.workflow.HistoricInformationMapper;
import org.rudi.facet.bpmn.service.TaskConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
public class HistoricHelper {

	private final ProcessEngine processEngine;

	private final HistoricInformationMapper historicInformationMapper;

	public Page<HistoricInformation> collectHistoricByAssetUuid(UUID assetUuid, Pageable pageable) {
		return historicInformationMapper.entitiesToDto(collectHistoricActivitiByAssetUuid(assetUuid, pageable),
				pageable);
	}

	/**
	 * Retourne l'historique d'une tâche par son TaskId en mode paginé
	 *
	 * @param executionId
	 * @param pageable
	 * @return la page demandée
	 */
	public Page<HistoricActivityInstance> collectHistoricActivitiByExecutionId(String executionId, Pageable pageable) {
		HistoryService historyService = processEngine.getHistoryService();
		HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();
		query.executionId(executionId).orderByHistoricActivityInstanceEndTime().asc();
		if (pageable == null || pageable.isUnpaged()) {
			return new PageImpl<>(query.list());
		} else {
			long count = query.count();
			return new PageImpl<>(query.listPage((int) pageable.getOffset(), pageable.getPageSize()), pageable, count);
		}
	}

	/**
	 * Retourne l'historique d'une tâche par son TaskId en mode paginé
	 *
	 * @param processInstanceId
	 * @param pageable
	 * @return la page demandée
	 */
	public Page<HistoricActivityInstance> collectHistoricActivitiByProcessInstanceId(String processInstanceId,
			Pageable pageable) {
		HistoryService historyService = processEngine.getHistoryService();
		HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();
		query.processInstanceId(processInstanceId).finished().activityType("userTask")
				.orderByHistoricActivityInstanceStartTime().asc();
		if (pageable == null || pageable.isUnpaged()) {
			return new PageImpl<>(query.list());
		} else {
			long count = query.count();
			return new PageImpl<>(query.listPage((int) pageable.getOffset(), pageable.getPageSize()), pageable, count);
		}
	}

	/**
	 * Retourne l'historique d'une tâche par l'uuid de son asset en mode paginé
	 *
	 * @param assetUuid
	 * @param pageable
	 * @return la page demandée
	 */
	public Page<HistoricActivityInstance> collectHistoricActivitiByAssetUuid(UUID assetUuid, Pageable pageable) {
		HistoryService historyService = processEngine.getHistoryService();
		// récupération de l'historique des variables
		HistoricVariableInstanceQuery variableQuery = historyService.createHistoricVariableInstanceQuery();
		variableQuery.variableValueEquals(TaskConstants.ME_UUID, assetUuid.toString());
		List<HistoricVariableInstance> variableResults = variableQuery.list();
		if (CollectionUtils.isNotEmpty(variableResults)) {
			// conversion de l'id
			String executionId = convertExecutionId(variableResults.get(0));
			if (executionId != null) {
				return collectHistoricActivitiByExecutionId(executionId, pageable);
			}
		}
		return Page.empty(pageable);
	}

	private String convertExecutionId(HistoricVariableInstance variable) {
		if (variable instanceof HistoricVariableInstanceEntity) {
			String variableExecutionId = ((HistoricVariableInstanceEntity) variable).getExecutionId();
			Page<HistoricActivityInstance> taskResults = collectHistoricActivitiByProcessInstanceId(variableExecutionId,
					Pageable.unpaged());
			if (!taskResults.isEmpty()) {
				HistoricActivityInstance taskInstance = taskResults.getContent().get(0);
				return taskInstance.getExecutionId();
			}
		}
		return null;
	}

	public HistoricTaskInstance getAssetLastFinishedTask(UUID assetUuid) {
		List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
				.processInstanceBusinessKey(assetUuid.toString()).orderByHistoricTaskInstanceEndTime().desc().list()
				.stream().filter(t -> t.getEndTime() != null)
				// Le .finsihed ne semble pas fonctionner ou en tout cas
				// pas sur ce champ, obliger de le faire à la main
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
