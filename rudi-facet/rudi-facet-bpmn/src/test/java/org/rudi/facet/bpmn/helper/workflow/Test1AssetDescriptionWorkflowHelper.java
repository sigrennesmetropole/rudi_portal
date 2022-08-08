/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.Test1AssetDescription;
import org.rudi.facet.bpmn.entity.workflow.Test1AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.Test1AssetDescriptionMapper;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class Test1AssetDescriptionWorkflowHelper extends
		AbstactAssetDescriptionHelper<Test1AssetDescriptionEntity, Test1AssetDescription, Test1AssetDescriptionMapper> {

	public Test1AssetDescriptionWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper, BpmnHelper bpmnHelper,
			Test1AssetDescriptionMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected Test1AssetDescriptionEntity createAsset() {
		return new Test1AssetDescriptionEntity();
	}

}
