/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.Test2AssetDescription;
import org.rudi.facet.bpmn.entity.workflow.Test2AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.Test2AssetDescriptionMapper;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Test2AssetDescriptionWorkflowHelper extends
		AbstactAssetDescriptionHelper<Test2AssetDescriptionEntity, Test2AssetDescription, Test2AssetDescriptionMapper> {

	public Test2AssetDescriptionWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, Test2AssetDescriptionMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected Test2AssetDescriptionEntity createAsset() {
		return new Test2AssetDescriptionEntity();
	}

}
