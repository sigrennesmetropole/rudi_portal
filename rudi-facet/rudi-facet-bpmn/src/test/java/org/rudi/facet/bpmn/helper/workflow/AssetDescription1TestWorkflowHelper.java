/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.AssetDescription1TestData;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper1Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssetDescription1TestWorkflowHelper extends
		AbstactAssetDescriptionHelper<AssetDescription1TestEntity, AssetDescription1TestData, AssetDescriptionMapper1Test> {

	public AssetDescription1TestWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper, BpmnHelper bpmnHelper,
			AssetDescriptionMapper1Test assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected AssetDescription1TestEntity createAsset() {
		return new AssetDescription1TestEntity();
	}

}
