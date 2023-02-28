package org.rudi.microservice.selfdata.service.selfdata.workflow.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import bean.workflow.SelfdataTaskSearchCriteria;
import org.activiti.engine.ProcessEngine;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Section;
import org.rudi.common.service.helper.UtilContextHelper;
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
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfDataMatchingDataHelper;
import org.rudi.microservice.selfdata.service.selfdata.workflow.SelfdataInformationRequestTaskService;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author KOU21310 Ce service permet de gérer les étapes du wkf associées à selfdataInformationRequest. Elle fait ainsi
 * des actions sur des tasks notamment et non sur des entity (ou asset)
 */
@Service
@Transactional(readOnly = true)
public class SelfdataInformationRequestTaskServiceImpl extends
		AbstractTaskServiceImpl<SelfdataInformationRequestEntity, SelfdataInformationRequest, SelfdataInformationRequestDao, SelfdataInformationRequestWorkflowHelper, SelfdataInformationRequestAssigmentHelper>
		implements SelfdataInformationRequestTaskService {

	public static final String PROCESS_DEFINITION_ID = "selfdata-information-request-process";
	private final FormService formService;
	private final FormHelper formHelper;
	private final TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService;
	private final SelfDataMatchingDataHelper selfDataMatchingDataHelper;

	protected SelfdataInformationRequestTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper,
			BpmnHelper bpmnHelper, UtilContextHelper utilContextHelper, InitializationService initializationService,
			SelfdataInformationRequestDao assetDescriptionDao,
			SelfdataInformationRequestWorkflowHelper assetDescriptionHelper,
			SelfdataInformationRequestAssigmentHelper assignmentHelper, FormService formService, FormHelper formHelper1,
			TaskQueryService<SelfdataTaskSearchCriteria> taskQueryService,
			SelfDataMatchingDataHelper selfDataMatchingDataHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assignmentHelper);

		this.formService = formService;
		this.formHelper = formHelper1;
		this.taskQueryService = taskQueryService;
		this.selfDataMatchingDataHelper = selfDataMatchingDataHelper;
	}

	@Override
	public String getProcessDefinitionKey() {
		return PROCESS_DEFINITION_ID;
	}

	@Override
	@Nullable
	public Form lookupDraftFormWithSelfdata(UUID metadataUuid, Optional<String> languageString)
			throws FormDefinitionException {
		return selfDataMatchingDataHelper.lookupDraftFormWithSelfdata(metadataUuid, languageString);
	}

	@Override
	protected Form lookupOriginalDraftForm(SelfdataInformationRequestEntity assetDescriptionEntity)
			throws FormDefinitionException {
		return selfDataMatchingDataHelper.lookupDraftFormWithSelfdata(assetDescriptionEntity.getDatasetUuid(),
				Optional.empty());
	}

	@Override
	public Form lookupFilledMatchingDataForm(String taskId) throws FormDefinitionException, InvalidDataException {

		// Récupération de l'asset hydraté de la task
		UUID assetUuid = this.taskQueryService.getTask(taskId).getAsset().getUuid();

		Map<String, Object> informationRequestMap = selfDataMatchingDataHelper.getInformationRequestMapByAssetUuid(
				assetUuid);

		Optional<Section> optionalMatchingDataSection = selfDataMatchingDataHelper.getMatchingDataSectionByAssetUuid(
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

}


