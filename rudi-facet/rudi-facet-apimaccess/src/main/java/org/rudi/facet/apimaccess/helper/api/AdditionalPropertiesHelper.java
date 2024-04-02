package org.rudi.facet.apimaccess.helper.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.wso2.carbon.apimgt.rest.api.publisher.APIInfoAdditionalPropertiesInner;

@Component
public class AdditionalPropertiesHelper {
	public @Valid List<@Valid APIInfoAdditionalPropertiesInner> getAdditionalPropertiesMapAsList(
			Map<String, String> mapAdditionalProperties) {
		return MapUtils.emptyIfNull(mapAdditionalProperties).entrySet().stream()
				.map(e -> new APIInfoAdditionalPropertiesInner().name(e.getKey()).value(e.getValue()).display(false) // display false pour que l'information puisse etre utilisée pour la recherche
				).collect(Collectors.toList());
	}

	public Map<String, String> getAdditionalPropertiesListAsMap(
			@Valid List<@Valid APIInfoAdditionalPropertiesInner> listAdditionalProperties) {
		return ListUtils.emptyIfNull(listAdditionalProperties).stream().collect(Collectors
				.toMap(APIInfoAdditionalPropertiesInner::getName, APIInfoAdditionalPropertiesInner::getValue));
	}

}
