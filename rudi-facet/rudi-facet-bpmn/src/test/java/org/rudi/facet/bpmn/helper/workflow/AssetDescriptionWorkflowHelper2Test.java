/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.helper.workflow;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.bean.AssetDescription2Test;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity2Test;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper2Test;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AssetDescriptionWorkflowHelper2Test extends
		AbstactAssetDescriptionHelper<AssetDescriptionEntity2Test, AssetDescription2Test, AssetDescriptionMapper2Test> {

	public AssetDescriptionWorkflowHelper2Test(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, AssetDescriptionMapper2Test assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected AssetDescriptionEntity2Test createAsset() {
		return new AssetDescriptionEntity2Test();
	}

}
