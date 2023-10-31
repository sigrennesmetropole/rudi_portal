/**
 * RUDI Portail
 */
package org.rudi.facet.providers.helper;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author FNI18300
 *
 */
@Configuration
@LoadBalancerClient(name = "strukture")
public class ProviderHelperConfiguration extends WebClientConfig {

	public ProviderHelperConfiguration(HttpClientHelper httpClientHelper) {
		super(httpClientHelper);
	}

}
