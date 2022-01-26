package org.rudi.microservice.kalim.facade.config.monitoring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Description des méthodes qui doivent être surveillées (cf {@link MonitoringConfig})
 */
@Aspect
@Component
public class MonitoringAspect {
	/**
	 * Point de coupe utilisé par le {@link MonitoringConfig#performanceMonitorAdvisor(org.springframework.aop.interceptor.AbstractMonitoringInterceptor)} PerformanceMonitorAdvisor}
	 */
	@Pointcut("execution(public * org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI.*(..))")
	@SuppressWarnings("unused") // Méthode de déclaration des points de coupure
	public void monitoredMethod() {
		// Méthode de déclaration des points de coupure
	}
}
