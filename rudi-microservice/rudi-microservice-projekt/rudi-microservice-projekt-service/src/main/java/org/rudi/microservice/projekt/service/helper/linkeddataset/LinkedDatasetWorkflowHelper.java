/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.linkeddataset;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class LinkedDatasetWorkflowHelper
		extends AbstactAssetDescriptionHelper<LinkedDatasetEntity, LinkedDataset, LinkedDatasetMapper> {

	public LinkedDatasetWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, LinkedDatasetMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected LinkedDatasetEntity createAsset() {
		return new LinkedDatasetEntity();
	}

}
