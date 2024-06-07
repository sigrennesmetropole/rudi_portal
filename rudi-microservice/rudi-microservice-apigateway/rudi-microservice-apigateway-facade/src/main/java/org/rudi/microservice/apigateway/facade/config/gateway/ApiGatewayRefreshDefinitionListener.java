/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.gateway;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.rudi.common.service.util.ApplicationContext;
import org.rudi.microservice.apigateway.service.api.ApiEvent;
import org.rudi.microservice.apigateway.service.api.ApiEventMode;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class ApiGatewayRefreshDefinitionListener implements ApplicationListener<ApplicationEvent> {

	private ApiPathRouteDefinitionLocator apiPathRouteDefinitionLocator;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApiEvent) {
			ApiEvent e = (ApiEvent) event;
			if (e.getMode() == ApiEventMode.CREATE || e.getMode() == ApiEventMode.UPDATE) {
				apiPathRouteDefinitionLocator.publish((UUID) e.getSource());
			} else {
				apiPathRouteDefinitionLocator.unpublish((UUID) e.getSource());
			}
		}
	}

	@PostConstruct
	protected void initializeRouteDefinitionLocator() {
		apiPathRouteDefinitionLocator = (ApiPathRouteDefinitionLocator) ApplicationContext
				.getBean(RouteDefinitionRepository.class);
	}

}
