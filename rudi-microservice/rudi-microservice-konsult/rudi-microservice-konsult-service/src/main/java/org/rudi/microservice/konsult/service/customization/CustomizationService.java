package org.rudi.microservice.konsult.service.customization;

import java.io.IOException;

import org.rudi.common.core.DocumentContent;
import org.rudi.microservice.konsult.core.bean.CustomizationDescription;


public interface CustomizationService {

	CustomizationDescription getCustomizationDescription(String lang) throws IOException;

	DocumentContent loadResources(String resourceName) throws IOException;
}
