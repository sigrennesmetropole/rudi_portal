/**
 *
 */
package org.rudi.facet.bpmn.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.hibernate.exception.DataException;
import org.rudi.bpmn.core.bean.AssetDescription;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.HistoricInformation;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;

/**
 * Service de gestion des tâches
 *
 * @param <D> le dto
 */
public interface TaskService<D extends AssetDescription> {

	/**
	 * Retourne le formulaire draft associé au service ou null s'il n'y en a pas
	 *
	 * @return
	 * @throws FormDefinitionException
	 */
	@Nullable
	Form lookupDraftForm() throws FormDefinitionException;

	/**
	 * Demande de création d'une tâche draft Tant que la tâche n'est pas démarrée elle n'apparait dans aucune bannette
	 *
	 * @param assertDescription
	 * @return la tâche
	 * @throws InvalidDataException si les données du formulaire sont incorrectes
	 * @throws FormConvertException si le formulaire ne peut pas être traduit en map
	 */
	Task createDraft(D assertDescription) throws InvalidDataException, FormConvertException, FormDefinitionException;

	/**
	 * Demande d'affectation à la personne connectée
	 *
	 * @param taskId
	 * @return
	 */
	Task claimTask(String taskId);

	/**
	 * Désaffectation de la tâche - ne peut être fait que si on est claimer ou admin
	 *
	 * @param taskId
	 */
	void unclaimTask(String taskId);

	/**
	 * Abandon d'un signalement à l'état draft
	 *
	 * @param assetUuid
	 */
	void cancelDraft(UUID assetUuid);

	/**
	 * Déclenchement d'une action sur une tâche
	 *
	 * @param taskId
	 * @param actionName
	 * @throws DataException
	 */
	void doIt(String taskId, String actionName) throws InvalidDataException;

	/**
	 * Mets à jour le signalement associé à la tâche
	 *
	 * @param task
	 * @return
	 * @throws DataException
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	Task updateTask(Task task) throws InvalidDataException, FormDefinitionException, FormConvertException;

	/**
	 * Créé une nouvelle tâche à partir du signalement draft
	 *
	 * @param task
	 * @return
	 * @throws DataException
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	Task startTask(Task task) throws InvalidDataException, FormDefinitionException, FormConvertException;

	/**
	 *
	 * @return le nom du process
	 */
	String getProcessDefinitionKey();

	/**
	 *
	 * @param assetUuid
	 * @return vrai si l'asset à une tâche en cours
	 */
	boolean hasTask(UUID assetUuid);

	/**
	 * Retourne l'id de la tâche associé à l'asset
	 *
	 * @param assetUuid
	 * @return
	 */
	String getAssociatedTaskId(UUID assetUuid);

	List<HistoricInformation> getTaskHistoryByTaskId(String taskId, Boolean asAdmin) throws InvalidDataException;
}
