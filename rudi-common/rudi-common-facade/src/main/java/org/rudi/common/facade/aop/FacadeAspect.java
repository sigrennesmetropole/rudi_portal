package org.rudi.common.facade.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FacadeAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(FacadeAspect.class);

	@Autowired
	private UtilContextHelper utilContextHelper;

	// Pour chaque entrée dans un controller
	@Pointcut("execution(* org.rudi.**.facade.controller.*.*(..))")
	public void businessMethods() {
		// Nothing to do
	}

	@Around("businessMethods()")
	public Object profile(final ProceedingJoinPoint pjp) throws Throwable {
		final Object output = pjp.proceed();

		final AuthenticatedUser cnxAccount = utilContextHelper.getAuthenticatedUser();
		final String accountname = cnxAccount != null ? cnxAccount.getLogin() : "ANONYMOUS";
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("[Account] = %s - %s : %s", accountname,
					pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName()));
		}
		return output;
	}

}
