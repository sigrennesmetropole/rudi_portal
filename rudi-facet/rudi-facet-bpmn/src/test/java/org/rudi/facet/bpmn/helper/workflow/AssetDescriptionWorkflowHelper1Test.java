/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.AssetDescription1Test;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity1Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper1Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssetDescriptionWorkflowHelper1Test extends
		AbstactAssetDescriptionHelper<AssetDescriptionEntity1Test, AssetDescription1Test, AssetDescriptionMapper1Test> {

	public AssetDescriptionWorkflowHelper1Test(UtilContextHelper utilContextHelper, FormHelper formHelper, BpmnHelper bpmnHelper,
			AssetDescriptionMapper1Test assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected AssetDescriptionEntity1Test createAsset() {
		return new AssetDescriptionEntity1Test();
	}

}
