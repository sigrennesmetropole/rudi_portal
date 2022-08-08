/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.AssetDescription;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author FNI18300
 *
 */
public abstract class AbstactAssetDescriptionHelper<E extends AssetDescriptionEntity, D extends AssetDescription, M extends AssetDescriptionMapper<E, D>>
		implements AssetDescriptionHelper<E, D> {

	@Getter(value = AccessLevel.PROTECTED)
	private final FormHelper formHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final UtilContextHelper utilContextHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final BpmnHelper bpmnHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final M assetDescriptionMapper;

	protected AbstactAssetDescriptionHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, M assetDescriptionMapper) {
		super();
		this.formHelper = formHelper;
		this.utilContextHelper = utilContextHelper;
		this.bpmnHelper = bpmnHelper;
		this.assetDescriptionMapper = assetDescriptionMapper;
	}

	protected abstract E createAsset();

	@Override
	public E createAssetEntity(D assetDescription) throws FormConvertException, InvalidDataException {
		E assetDescriptionEntity = createAsset();
		assetDescriptionMapper.dtoToEntity(assetDescription, assetDescriptionEntity);
		assetDescriptionEntity.setCreationDate(LocalDateTime.now());
		assetDescriptionEntity.setUuid(UUID.randomUUID());
		return assetDescriptionEntity;
	}

	@Override
	public void updateAssetEntity(D assetDescription, E assetDescriptionEntity) {
		assetDescriptionMapper.dtoToEntity(assetDescription, assetDescriptionEntity);
	}

	@Override
	public Task createTaskFromAsset(E assetDescriptionEntity, Form form) {
		D asset = assetDescriptionMapper.entityToDto(assetDescriptionEntity);
		asset.setForm(form);
		return creatTask(asset);
	}

	@Override
	public Task createTaskFromWorkflow(org.activiti.engine.task.Task originalTask, E assetDescriptionEntity) {
		D asset = assetDescriptionMapper.entityToDto(assetDescriptionEntity);
		Task task = creatTask(asset);
		updateTask(task, originalTask);
		return task;
	}

	protected Task creatTask(D asset) {
		Task task = new Task();
		task.setAsset(asset);
		task.setCreationDate(asset.getCreationDate());
		task.setUpdatedDate(asset.getUpdatedDate());
		task.setStatus(asset.getStatus());
		task.setFunctionalStatus(asset.getFunctionalStatus());
		task.setInitiator(asset.getInitiator());
		task.setFunctionalId(asset.getUuid().toString());
		return task;
	}

	protected void updateTask(Task task, org.activiti.engine.task.Task originalTask) {
		List<Action> actions = bpmnHelper.computeTaskActions(originalTask);
		task.setActions(actions);
		task.setAssignee(originalTask.getAssignee());
		task.setId(originalTask.getId());
	}

}
