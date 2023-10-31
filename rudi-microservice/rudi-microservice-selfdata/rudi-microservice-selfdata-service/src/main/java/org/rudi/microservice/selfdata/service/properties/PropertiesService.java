package org.rudi.microservice.selfdata.service.properties;

import java.util.List;

import org.rudi.microservice.selfdata.core.bean.FrontOfficeProperties;
import org.rudi.microservice.selfdata.core.bean.SelfdataRequestAllowedAttachementType;

public interface PropertiesService {
	FrontOfficeProperties getFrontOfficeProperties();
	List<SelfdataRequestAllowedAttachementType> getAllowedAttachementTypes();
}
