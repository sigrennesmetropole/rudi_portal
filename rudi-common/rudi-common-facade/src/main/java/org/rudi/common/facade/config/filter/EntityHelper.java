/**
 * RUDI Portail
 */
package org.rudi.common.facade.config.filter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.nimbusds.oauth2.sdk.util.MapUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityHelper {
	public static HttpEntity<MultiValueMap<String, String>> buildFomEntity(String param, String value) {
		Map<String, String> map = new HashMap<>();
		map.put(param, value);
		return buildFomEntity(map);
	}

	public static HttpEntity<MultiValueMap<String, String>> buildFomEntity(Map<String, String> parameters) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		if (MapUtils.isNotEmpty(parameters)) {
			parameters.entrySet().forEach(item -> map.add(item.getKey(), item.getValue()));
		}

		return new HttpEntity<MultiValueMap<String, String>>(map, headers);
	}
}
