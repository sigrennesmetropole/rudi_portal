/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.AssetDescription2TestData;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription2TestEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper2Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssetDescription2TestWorkflowHelper extends
		AbstactAssetDescriptionHelper<AssetDescription2TestEntity, AssetDescription2TestData, AssetDescriptionMapper2Test> {

	public AssetDescription2TestWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, AssetDescriptionMapper2Test assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected AssetDescription2TestEntity createAsset() {
		return new AssetDescription2TestEntity();
	}

}
