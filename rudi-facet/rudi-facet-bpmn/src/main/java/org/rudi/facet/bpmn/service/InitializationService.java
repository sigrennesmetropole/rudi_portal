/**
 *
 */
package org.rudi.facet.bpmn.service;

import java.util.List;

import org.rudi.bpmn.core.bean.ProcessDefinition;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.bpmn.exception.BpmnInitializationException;
import org.rudi.facet.bpmn.exception.InvalidDataException;

/**
 * @author FNI18300
 *
 */
public interface InitializationService {

	/**
	 * Charge une nouvelle définition d'un processus sous forme d'un fichier bpmn2.0 au format xml
	 *
	 * @param processDefinitionKey
	 * @param documentContent
	 * @throws BpmnInitializationException
	 */
	void updateProcessDefinition(String processDefinitionKey, DocumentContent documentContent)
			throws BpmnInitializationException;

	/**
	 * Supprime une définition
	 *
	 * @param processDefinitionKey
	 * @throws BpmnInitializationException
	 */
	boolean deleteProcessDefinition(String processDefinitionKey, Integer version) throws InvalidDataException;

	/**
	 * Retourne la liste des définitions
	 *
	 * @return
	 */
	List<ProcessDefinition> searchProcessDefinitions();

}
