/**
 * RUDI Portail
 */
package org.rudi.facet.providers.helper;

import org.rudi.facet.oauth2.config.WebClientConfig;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author FNI18300
 *
 */
@Configuration
@LoadBalancerClient(name = "strukture"/* , configuration = SayHelloConfiguration.class */)
public class ProviderHelperConfiguration extends WebClientConfig {

}
