package org.rudi.common.facade.config.monitoring;

import java.util.Locale;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.StopWatch;

public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

	private static final long serialVersionUID = 7280252060660534265L;

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
		return "StopWatch : running time = " + String.format(Locale.FRANCE, "%,6d", stopWatch.getTotalTimeMillis())
				+ " ms for method '" + stopWatch.getId() + "'";
	}
}
