package org.rudi.facet.apimaccess.helper.search;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
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
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_SEARCH_MAPPER;

@Component(value = API_MACCESS_SEARCH_MAPPER)
public class SearchCriteriaMapper {

    public APISearchCriteria buildAPISearchCriteriaQuery(APISearchCriteria apiSearchCriteria) {
        if (apiSearchCriteria == null) {
            apiSearchCriteria = new APISearchCriteria();
        }
        Map<String, String> map = Map.of(NAME, Objects.toString(apiSearchCriteria.getName(), "") ,
                VERSION, Objects.toString(apiSearchCriteria.getVersion(), ""),
                DESCRIPTION, Objects.toString(apiSearchCriteria.getDescription(), ""),
                GLOBAL_ID, Objects.toString(apiSearchCriteria.getGlobalId(), ""),
                PROVIDER_UUID, Objects.toString(apiSearchCriteria.getProviderUuid(), ""),
                PROVIDER_CODE, Objects.toString(apiSearchCriteria.getProviderCode(), ""),
                EXTENSION, Objects.toString(apiSearchCriteria.getExtension(), ""),
                MEDIA_UUID, Objects.toString(apiSearchCriteria.getMediaUuid(), ""),
                INTERFACE_CONTRACT, Objects.toString(apiSearchCriteria.getInterfaceContract(), ""));
        apiSearchCriteria.setQuery(buildQueryCriteria(map));
        return apiSearchCriteria;
    }

    public ApplicationSearchCriteria buildApplicationSearchCriteriaQuery(ApplicationSearchCriteria applicationSearchCriteria) {
        if (applicationSearchCriteria == null) {
            applicationSearchCriteria = new ApplicationSearchCriteria();
        }
        applicationSearchCriteria.setQuery(Objects.toString(applicationSearchCriteria.getName(), ""));
        return applicationSearchCriteria;
    }

    private String buildQueryCriteria(Map<String, String> queryParameters) {
        if (!MapUtils.isEmpty(queryParameters)) {
            return queryParameters.entrySet().stream()
                    .map(entry -> StringUtils.isEmpty(entry.getValue()) ? "" : entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(" "));
        }
        return "";
    }
}
