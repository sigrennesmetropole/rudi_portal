/**
 *
 */
package org.rudi.facet.bpmn.service.impl;

import java.io.FileInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.bpmn.exception.BpmnInitializationException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.mapper.workflow.ProcessDefinitionMapper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.ProcessDefinitionActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

/**
 * @author FNI18300
 *
 */
@Component
public class InitializationServiceImpl implements InitializationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationServiceImpl.class);

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private ProcessDefinitionMapper processDefinitionMapper;

	@Autowired(required = false)
	private List<ProcessDefinitionActionListener> validators;

	@Override
	public void updateProcessDefinition(String deploymentName, DocumentContent documentContent)
			throws BpmnInitializationException {
		LOGGER.info("Start update process definition ...");
		if (documentContent == null || StringUtils.isEmpty(deploymentName)) {
			throw new IllegalArgumentException("Deployement name and process definition could not be null");
		}
		if (!documentContent.getContentType().equalsIgnoreCase(MimeTypeUtils.APPLICATION_XML_VALUE)
				&& !documentContent.getContentType().equalsIgnoreCase(MimeTypeUtils.TEXT_XML_VALUE)) {
			throw new IllegalArgumentException(String.format("Process definition must be an XML File (found: %s)",
					documentContent.getContentType()));
		}
		RepositoryService repositoryService = processEngine.getRepositoryService();
		if (documentContent.isFile()) {
			try (FileInputStream fis = new FileInputStream(documentContent.getFile())) {
				LOGGER.info("Deploy file {}", deploymentName);
				Deployment deployment = repositoryService.createDeployment().name(deploymentName)
						.category(deploymentName + "_category").addInputStream(documentContent.getFileName(), fis)
						.deploy();
				LOGGER.info("Deploy {}", deployment.getId());
			} catch (Exception e) {
				throw new BpmnInitializationException("Failed to deploy file:" + deploymentName, e);
			}
		} else if (documentContent.isStream()) {
			try {
				LOGGER.info("Deploy stream {}", deploymentName);
				Deployment deployment = repositoryService.createDeployment().name(deploymentName)
						.category(deploymentName + "_category")
						.addInputStream(documentContent.getFileName(), documentContent.getFileStream()).deploy();
				LOGGER.info("Deploy {}", deployment.getId());
			} catch (Exception e) {
				throw new BpmnInitializationException("Failed to deploy stream:" + deploymentName, e);
			} finally {
				documentContent.closeStream();
			}
		}
	}

	@Override
	public List<org.rudi.bpmn.core.bean.ProcessDefinition> searchProcessDefinitions() {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
		return processDefinitions.stream().map(processDefinitionMapper::entityToDto).collect(Collectors.toList());
	}

	@Override
	public boolean deleteProcessDefinition(String processDefinitionKey, Integer version) throws InvalidDataException {
		boolean result = true;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey);
		if (version != null) {
			LOGGER.info("A version has been provided : {}", version);
			query = query.processDefinitionVersion(version);
		}
		List<ProcessDefinition> processDefinitions = query.list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			org.activiti.engine.TaskService taskService = processEngine.getTaskService();
			ProcessDefinition latestVersion = getMostRecentVersion(processDefinitions);
			for (ProcessDefinition processDefinition : processDefinitions) {
				List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinition.getKey())
						.list();
				if (acceptDeletion(processDefinition, latestVersion, tasks)) {
					LOGGER.info("Start delete deployment {}.", processDefinition.getDeploymentId());
					repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
					LOGGER.info("Delete deployment {} done.", processDefinition.getDeploymentId());
				} else {
					LOGGER.info("Can't delete used workflow : {}.", processDefinition.getDeploymentId());
					result = false;
				}
			}
		} else {
			throw new IllegalArgumentException(
					String.format("Could not find any definition for %s", processDefinitionKey));
		}
		return result;
	}

	protected boolean acceptDeletion(ProcessDefinition processDefinition, ProcessDefinition latestVersion,
			List<Task> tasks) {
		boolean result = true;
		if (CollectionUtils.isNotEmpty(validators)) {
			for (ProcessDefinitionActionListener validator : validators) {
				result &= validator.acceptDeletion(processDefinition, latestVersion, tasks);
			}
		}
		return result;
	}

	protected ProcessDefinition getMostRecentVersion(List<ProcessDefinition> processDefinitions) {
		return processDefinitions.stream().sorted(Comparator.comparingInt(ProcessDefinition::getVersion).reversed())
				.collect(Collectors.toList()).get(0);
	}

}
