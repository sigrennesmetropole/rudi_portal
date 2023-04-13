package org.rudi.microservice.selfdata.service.selfdata.workflow.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import bean.workflow.SelfdataTaskSearchCriteria;
import org.activiti.engine.ProcessEngine;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.FormService;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.TaskQueryService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest.SelfdataInformationRequestAssigmentHelper;
import org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest.SelfdataInformationRequestWorkflowHelper;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.rudi.microservice.selfdata.service.selfdata.workflow.SelfdataInformationRequestTaskService;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author KOU21310 Ce service permet de gérer les étapes du wkf associées à selfdataInformationRequest. Elle fait ainsi
 * des actions sur des tasks notamment et non sur des entity (ou asset)
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class SelfdataInformationRequestTaskServiceImpl extends
		AbstractTaskServiceImpl<SelfdataInformationRequestEntity, SelfdataInformationRequest, SelfdataInformationRequestDao, SelfdataInformationRequestWorkflowHelper, SelfdataInformationRequestAssigmentHelper>
		implements SelfdataInformationRequestTaskService {

	public static final String PROCESS_DEFINITION_ID = "selfdata-information-request-process";
	private final FormService formService;
	private final FormHelper formHelper;
	private final TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService;
	private final SelfdataMatchingDataHelper selfdataMatchingDataHelper;

	protected SelfdataInformationRequestTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper,
			BpmnHelper bpmnHelper, UtilContextHelper utilContextHelper, InitializationService initializationService,
			SelfdataInformationRequestDao assetDescriptionDao,
			SelfdataInformationRequestWorkflowHelper assetDescriptionHelper,
			SelfdataInformationRequestAssigmentHelper assignmentHelper, FormService formService, FormHelper formHelper1,
			TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService,
			SelfdataMatchingDataHelper selfdataMatchingDataHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assignmentHelper);

		this.formService = formService;
		this.formHelper = formHelper1;
		this.taskQueryService = taskQueryService;
		this.selfdataMatchingDataHelper = selfdataMatchingDataHelper;
	}

	@Override
	public String getProcessDefinitionKey() {
		return PROCESS_DEFINITION_ID;
	}

	@Override
	@Nullable
	public Form lookupDraftFormWithSelfdata(UUID metadataUuid, Optional<String> languageString)
			throws FormDefinitionException {
		return selfdataMatchingDataHelper.lookupDraftFormWithSelfdata(metadataUuid, languageString);
	}

	@Override
	protected Form lookupOriginalDraftForm(SelfdataInformationRequestEntity assetDescriptionEntity)
			throws FormDefinitionException {
		return selfdataMatchingDataHelper.lookupDraftFormWithSelfdata(assetDescriptionEntity.getDatasetUuid(),
				Optional.empty());
	}

	@Override
	public Form lookupFilledMatchingDataForm(String taskId) throws FormDefinitionException, InvalidDataException {

		// Récupération de l'asset hydraté de la task
		UUID assetUuid = this.taskQueryService.getTask(taskId).getAsset().getUuid();

		Map<String, Object> informationRequestMap = selfdataMatchingDataHelper.getInformationRequestMapByAssetUuid(
				assetUuid);

		Optional<Section> optionalMatchingDataSection = selfdataMatchingDataHelper.getMatchingDataSectionByAssetUuid(
				assetUuid);

		if (optionalMatchingDataSection.isPresent()) {
			Form matchingDataForm = new Form();
			matchingDataForm.addSectionsItem(optionalMatchingDataSection.get());
			formHelper.fillForm(matchingDataForm, informationRequestMap);
			return matchingDataForm;
		} else {
			throw new IllegalArgumentException(
					String.format("Pas de section données pivot pour la taskId: %s", taskId));
		}

	}

	@Override
	@PostConstruct
	public void loadBpmn() throws IOException {
		super.loadBpmn();
		formService.createOrUpdateAllSectionAndFormDefinitions();
	}

	@Override
	protected void updateDraftAssetData(SelfdataInformationRequest assetDescription, SelfdataInformationRequestEntity assetDescriptionEntity)
			throws FormDefinitionException, FormConvertException, InvalidDataException {
		Map<String, Object> datas = formHelper.hydrateData(assetDescriptionEntity.getData());
		Form orignalForm = lookupOriginalDraftForm(assetDescriptionEntity);
		formHelper.copyFormData(assetDescription.getForm(), orignalForm);
		// Chiffrement des matchingDatas.
		selfdataMatchingDataHelper.encrypt(orignalForm);
		formHelper.fillMap(orignalForm, datas);
		assetDescriptionEntity.setData(formHelper.deshydrateData(datas));
	}


	@Override
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
	protected void updateAssetData(Task task, org.activiti.engine.task.Task originalTask, SelfdataInformationRequestEntity assetDescriptionEntity)
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
	@Override
	protected void fillMap(Map<String, Object> datas, Form form, org.activiti.engine.task.Task originalTask,
			String actionName) throws FormDefinitionException, FormConvertException {
		Form actionForm = formHelper.lookupForm(originalTask, actionName);
		formHelper.copyFormData(form, actionForm);
		formHelper.fillMap(actionForm, datas);
		selfdataMatchingDataHelper.encrypt(actionForm);
	}
}


