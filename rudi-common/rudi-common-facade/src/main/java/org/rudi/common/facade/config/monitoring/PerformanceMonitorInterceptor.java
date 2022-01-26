package org.rudi.common.facade.config.monitoring;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.StopWatch;

import java.util.Locale;

public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {
	public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
		setUseDynamicLogger(useDynamicLogger);
	}

	@Override
	protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
		final String name = invocation.getMethod().getName();
		StopWatch stopWatch = new StopWatch(name);
		stopWatch.start(name);
		try {
			return invocation.proceed();
		} finally {
			stopWatch.stop();
			writeToLog(logger, shortSummary(stopWatch));
		}
	}

	public String shortSummary(StopWatch stopWatch) {
		return "StopWatch : running time = " + String.format(Locale.FRANCE, "%,6d", stopWatch.getTotalTimeMillis()) + " ms for method '" + stopWatch.getId() + "'";
	}
}
