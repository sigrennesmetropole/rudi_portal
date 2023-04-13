package org.rudi.microservice.selfdata.service.workflow;

import java.util.Map;

import bean.workflow.SelfdataTaskSearchCriteria;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.impl.AbstractTaskQueryServiceImpl;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SelfdataTaskQueryServiceImpl extends AbstractTaskQueryServiceImpl<SelfdataTaskSearchCriteria> {
	private final SelfdataMatchingDataHelper selfdataMatchingDataHelper;

	public SelfdataTaskQueryServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, ApplicationContext applicationContext, SelfdataMatchingDataHelper selfdataMatchingDataHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, applicationContext);
		this.selfdataMatchingDataHelper = selfdataMatchingDataHelper;
	}

	@Override
	protected void applyExtentedCriteria(TaskQuery taskQuery, SelfdataTaskSearchCriteria taskSearchCriteria) {
		if (StringUtils.isNotEmpty(taskSearchCriteria.getTitle())) {
			taskQuery.processVariableValueLikeIgnoreCase(
					SelfdataWorkflowConstants.TITLE, taskSearchCriteria.getTitle());
		}
	}

	@Override
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
		Map<String, Object> datas = getFormHelper().hydrateData(assetDescriptionEntity.getData());
		Form form = getFormHelper().lookupForm(originalTask, null);
		if (form != null) {
			getFormHelper().fillForm(form, datas);
			// Dechiffrement des matchingData contenues dans le form.
			selfdataMatchingDataHelper.decrypt(form);
			task.getAsset().setForm(form);
		}
		if (CollectionUtils.isNotEmpty(task.getActions())) {
			for (Action action : task.getActions()) {
				if (action.getForm() != null) {
					getFormHelper().fillForm(action.getForm(), datas);
				}
			}
		}
	}
}
