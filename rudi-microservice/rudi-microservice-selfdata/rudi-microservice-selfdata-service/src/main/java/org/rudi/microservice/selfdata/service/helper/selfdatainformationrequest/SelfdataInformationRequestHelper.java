package org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.service.mapper.SelfdataInformationRequestMapper;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Component;

@Component
public class SelfdataInformationRequestHelper extends AbstactAssetDescriptionHelper<
		SelfdataInformationRequestEntity, SelfdataInformationRequest, SelfdataInformationRequestMapper> {

	protected SelfdataInformationRequestHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, SelfdataInformationRequestMapper assetDescriptionMapper) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
	}

	@Override
	protected SelfdataInformationRequestEntity createAsset() {
		return new SelfdataInformationRequestEntity();
	}
}
