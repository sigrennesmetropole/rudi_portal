package org.rudi.facet.apimaccess.helper.search;


import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.DESCRIPTION;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.EXTENSION;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.GLOBAL_ID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.INTERFACE_CONTRACT;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.MEDIA_UUID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.NAME;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_CODE;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.PROVIDER_UUID;
import static org.rudi.facet.apimaccess.constant.APISearchPropertyKey.VERSION;

@Component
public class QueryBuilder {
	public String buildFrom(APISearchCriteria apiSearchCriteria) {
		if (apiSearchCriteria == null) {
			return StringUtils.EMPTY;
		}

		final var criteriaMap = new CriteriaMap();
		criteriaMap.put(NAME, apiSearchCriteria.getName());
		criteriaMap.put(VERSION, apiSearchCriteria.getVersion());
		criteriaMap.put(DESCRIPTION, apiSearchCriteria.getDescription());
		criteriaMap.put(GLOBAL_ID, apiSearchCriteria.getGlobalId());
		criteriaMap.put(PROVIDER_UUID, apiSearchCriteria.getProviderUuid());
		criteriaMap.put(PROVIDER_CODE, apiSearchCriteria.getProviderCode());
		criteriaMap.put(EXTENSION, apiSearchCriteria.getExtension());
		criteriaMap.put(MEDIA_UUID, apiSearchCriteria.getMediaUuid());
		criteriaMap.put(INTERFACE_CONTRACT, apiSearchCriteria.getInterfaceContract());

		return criteriaMap.toQuery();
	}

	public String buildFrom(ApplicationSearchCriteria applicationSearchCriteria) {
		if (applicationSearchCriteria != null) {
			final var name = applicationSearchCriteria.getName();
			if (name != null) {
				return name;
			}
		}
		return StringUtils.EMPTY;
	}

	private static class CriteriaMap {
		private final Map<String, String> map = new TreeMap<>();

		<T> void put(String apiSearchPropertyKey, T value) {
			if (value != null) {
				map.put(apiSearchPropertyKey, value.toString());
			}
		}

		String toQuery() {
			return map.entrySet().stream()
					.map(entry -> StringUtils.isEmpty(entry.getValue()) ? "" : entry.getKey() + ":" + entry.getValue())
					.collect(Collectors.joining(" "));
		}
	}

}
