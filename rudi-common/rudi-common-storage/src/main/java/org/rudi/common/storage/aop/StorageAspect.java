package org.rudi.common.storage.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StorageAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageAspect.class);

	@Pointcut("execution(* org.rudi.common.storage.dao.impl.*.*(..))")
	public void businessMethods() {
		// Nothing to do
	}

	@Around("businessMethods()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object output = pjp.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} - {} {}", elapsedTime, pjp.getSignature().getDeclaringTypeName(),
					pjp.getSignature().getName());
		}
		return output;
	}

}
