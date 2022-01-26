package org.rudi.microservice.konsult.facade.config.monitoring;

import org.rudi.common.facade.config.monitoring.PerformanceMonitorInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration pour loguer le temps d'exécution des méthodes décrites par l'aspect {@link MonitoringAspect}.
 * <b>Les méthodes surveillées doivent avoir un niveau de log égal à TRACE pour faire fonctionner le performanceMonitor.</b>
 *
 * @see <a href="https://www.baeldung.com/spring-performance-logging">Spring Performance Logging</a>
 */
@Configuration
@EnableAspectJAutoProxy
public class MonitoringConfig {

	/**
	 * Intercepteur utilisé pour loguer le temps d'exécution de méthodes si leur niveau de log est égal à TRACE
	 *
	 * @return l'intercepteur
	 * @see PerformanceMonitorInterceptor
	 */
	@Bean
	public AbstractMonitoringInterceptor monitorInterceptor() {
		return new PerformanceMonitorInterceptor(true);
	}

	/**
	 * Associe l'aspect {@link MonitoringAspect} à l'intercepteur {@link #monitorInterceptor PerformanceMonitorInterceptor}
	 *
	 * @param monitorInterceptor intercepteur qui logue les temps d'exécution
	 * @return l'advisor qui réalise cette association
	 */
	@Bean
	public Advisor performanceMonitorAdvisor(AbstractMonitoringInterceptor monitorInterceptor) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(MonitoringAspect.class.getName() + ".monitoredMethod()");
		return new DefaultPointcutAdvisor(pointcut, monitorInterceptor);
	}
}
