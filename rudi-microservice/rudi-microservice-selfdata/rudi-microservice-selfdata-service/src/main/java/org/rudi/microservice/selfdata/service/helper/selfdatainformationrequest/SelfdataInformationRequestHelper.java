package org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest;

import java.util.List;
import java.util.stream.Collectors;

import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.apimaccess.api.ContentTypeUtils;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstactAssetDescriptionHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequest;
import org.rudi.microservice.selfdata.core.bean.SelfdataRequestAllowedAttachementType;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataRequestAttachementProperties;
import org.rudi.microservice.selfdata.service.mapper.SelfdataInformationRequestMapper;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SelfdataInformationRequestHelper extends AbstactAssetDescriptionHelper<
		SelfdataInformationRequestEntity, SelfdataInformationRequest, SelfdataInformationRequestMapper> {

	@Value("${rudi.selfdata.attachement.allowed.types:application/pdf,image/jpeg,image/png,image/tiff}")
	private List<String> allowedMediaTypes;
	private final SelfdataRequestAttachementProperties attachementProperties;



	protected SelfdataInformationRequestHelper(UtilContextHelper utilContextHelper, FormHelper formHelper,
			BpmnHelper bpmnHelper, SelfdataInformationRequestMapper assetDescriptionMapper, SelfdataRequestAttachementProperties attachementProperties) {
		super(utilContextHelper, formHelper, bpmnHelper, assetDescriptionMapper);
		this.attachementProperties = attachementProperties;
	}

	@Override
	protected SelfdataInformationRequestEntity createAsset() {
		return new SelfdataInformationRequestEntity();
	}

	public void checkMediaType(String contentType){
		var list = attachementProperties.getTypes()
				.stream().map(SelfdataRequestAllowedAttachementType::getMediaType).collect(Collectors.toList());
		var mediaTypeList = MediaType.parseMediaTypes(list);
		if(!mediaTypeList
				.contains(ContentTypeUtils.normalize(contentType))){
			throw new IllegalArgumentException(
					String.format("Not allowed content type for attachment : %s", contentType));
		}
	}
}
