/**
 * 
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.bpmn.core.bean.AssetDescription;
import org.rudi.bpmn.core.bean.Form;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.InvalidDataException;

/**
 * @author FNI18300
 *
 */
public interface AssetDescriptionHelper<E extends AssetDescriptionEntity, D extends AssetDescription> {

	E createAssetEntity(D assetDescription) throws FormConvertException, InvalidDataException;

	Task createTaskFromAsset(E assetDescriptionEntity, Form form);

	Task createTaskFromWorkflow(org.activiti.engine.task.Task originalTask, E assetDescriptionEntity);

	void updateAssetEntity(D assetDescription, E assetDescriptionEntity) throws InvalidDataException;

}
