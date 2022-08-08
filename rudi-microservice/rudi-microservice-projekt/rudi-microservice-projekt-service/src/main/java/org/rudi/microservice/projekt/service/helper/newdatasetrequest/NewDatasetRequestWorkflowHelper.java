/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.newdatasetrequest;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class NewDatasetRequestWorkflowHelper
		extends AbstactAssetDescriptionHelper<NewDatasetRequestEntity, NewDatasetRequest, NewDatasetRequestMapper> {

	public NewDatasetRequestWorkflowHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, NewDatasetRequestMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected NewDatasetRequestEntity createAsset() {
		return new NewDatasetRequestEntity();
	}

}
