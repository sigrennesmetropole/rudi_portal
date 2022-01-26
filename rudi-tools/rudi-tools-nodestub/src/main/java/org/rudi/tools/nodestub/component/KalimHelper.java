/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.component;

import java.util.UUID;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.kalim.core.bean.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;

/**
 * @author FNI18300
 *
 */
@Component
public class KalimHelper {

	@Getter
	@Value("${rudi.facet.kalim.endpoint.resources:/kalim/v1/resources}")
	private String kalimResourcesEndpointSearchURL;

	@Getter
	@Value("${rudi.facet.kalim.service.url:http://localhost:8082}")
	private String kalimServiceURL;

	@Autowired
	@Qualifier("node_oauth2")
	private WebClient loadBalancedWebClient;

	public void submit(Method method, Metadata metadata) {
		if (method == Method.POST) {
			loadBalancedWebClient.post().uri(buildPostPutURL()).body(BodyInserters.fromValue(metadata)).retrieve()
					.bodyToMono(UUID.class).block();
		} else if (method == Method.PUT) {
			loadBalancedWebClient.put().uri(buildPostPutURL()).body(BodyInserters.fromValue(metadata)).retrieve()
					.bodyToMono(UUID.class).block();
		} else if (method == Method.DELETE) {
			loadBalancedWebClient.delete().uri(buildDeleteURL(metadata.getGlobalId())).retrieve().bodyToMono(UUID.class)
					.block();
		}
	}

	protected String buildPostPutURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(kalimServiceURL).append(kalimResourcesEndpointSearchURL);
		return urlBuilder.toString();
	}

	protected String buildDeleteURL(UUID uuid) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(kalimServiceURL).append(kalimResourcesEndpointSearchURL).append('/').append(uuid);
		return urlBuilder.toString();
	}

}
