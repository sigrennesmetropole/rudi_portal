/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

/**
 * @author FNI18300
 *
 */
@RestController
public class JWKsController {

	@Autowired
	private JWKSet jwkSet;

	@GetMapping("/oauth/jwks")
	public Map<String, Object> keys() {
		Map<String, Object> keys = jwkSet.toJSONObject();
		@SuppressWarnings("unchecked")
		List<String> values = (List<String>) keys.get("keys");
		if (CollectionUtils.isEmpty(values)) {
			keys = jwkSet.toJSONObject(false);
		}
		return keys;
	}
}
