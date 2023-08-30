package org.rudi.common.facade.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class FacadeAspect {

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Value("${rudi.facade.small-signatures:}")
	private List<String> smallSignatures;

	@Value("${rudi.facade.hidden-classes:}")
	private List<String> hiddenClasses;

	@Autowired(required = false)
	private List<FacadAspectFormater> aspectFormaters;

	private static Map<Class<?>, Method> UUID_METHODS = new HashMap<>();

	// Pour chaque entrée dans un controller
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
	public void businessMethods() {
		// Nothing to do
	}

	@Around("businessMethods()")
	public Object profile(final ProceedingJoinPoint pjp) throws Throwable {
		Object result = pjp.proceed();
		// récupération de la personne authentifiée
		final AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		// calcul de la personne à logguer (pour ne pas logguer le login sauf pour anonymous
		final String accountname = computeAccountName(authenticatedUser);

		try {
			if (log.isDebugEnabled()) {
				// en debut log avancé
				log.debug(longFormat(accountname, pjp, result));
			} else if (log.isInfoEnabled()) {
				// en info log simple
				log.info(format(accountname, pjp, result));
			}
		} catch (Exception e) {
			log.warn("Failed to log api...", e);
		}
		return result;
	}

	private String computeAccountName(AuthenticatedUser authenticatedUser) {
		return authenticatedUser != null && authenticatedUser.getLogin() != null
				? hashLogin(authenticatedUser.getLogin())
				: "ANONYMOUS";
	}

	private String hashLogin(String login) {
		return Integer.toString(login.hashCode());
	}

	private String format(final String accountname, final ProceedingJoinPoint pjp, Object result) {
		// on trace la méthode et le code de retour
		return String.format("by(%s) - %s.%s(...) = %s", accountname, pjp.getSignature().getDeclaringType().getName(),
				pjp.getSignature().getName(), convertSimpleResult(result));
	}

	private String longFormat(final String accountname, final ProceedingJoinPoint pjp, Object result) {
		String typeSignature = pjp.getSignature().getDeclaringType().getName() + "." + pjp.getSignature().getName();
		if (smallSignatures.contains(typeSignature)) {
			// cas des signatures qui doivent toujours etre en small
			return format(accountname, pjp, result);
		} else {
			// on trace les arguments et le retour
			return String.format("by(%s) - %s(%s) = %s", accountname, typeSignature,
					Arrays.toString(convertArgs(pjp.getArgs())), convertItem(result));
		}
	}

	private Object[] convertArgs(Object[] args) {
		List<Object> cargs = new ArrayList<>();
		for (Object arg : args) {
			cargs.add(convertItem(arg));
		}
		return cargs.toArray();
	}

	private String convertSimpleResult(Object arg) {
		String result = null;
		if (arg == null) {
			result = "null";
		} else if (arg instanceof ResponseEntity) {
			ResponseEntity<?> response = ((ResponseEntity<?>) arg);
			result = "<" + response.getStatusCode().toString() + ">";
		}
		return result;
	}

	private Object convertItem(Object arg) {
		Object result = null;
		if (arg == null) {
			result = "null";
		} else if (arg instanceof ResponseEntity) {
			ResponseEntity<?> response = ((ResponseEntity<?>) arg);
			result = "<" + response.getStatusCode().toString() + ">" + convertItem(response.getBody());
		} else {
			Class<?> clazz = arg.getClass();
			// on exclue les classes cachées
			result = handleHiddenClasses(arg, clazz);
			// on traite les classes adaptées
			if (result == null && CollectionUtils.isNotEmpty(aspectFormaters)) {
				result = convertItemWithAdapters(arg);
			}
			if (result == null) {
				result = convertItemStandard(arg);
			}
		}

		if (result == null) {
			result = "xxx";
		}
		return result;
	}

	private Object convertItemStandard(Object arg) {
		Object result = null;
		if (arg instanceof UUID || arg instanceof Long || arg instanceof Integer || arg instanceof Double
				|| arg instanceof Float || arg instanceof Boolean || arg instanceof String) {
			result = arg.toString();
		} else if (arg instanceof Resource) {
			result = "Resource@" + ((Resource) arg).getFilename();
		} else {
			Method m = getUUidMethod(arg.getClass());
			if(m!=null){
				result = callMethod(m, arg);
				result = arg.getClass().getSimpleName() + "(" + result + ")";
			}
			if (result == null) {
				result = arg.toString();
			}

		}
		return result;
	}

	private Object convertItemWithAdapters(Object arg) {
		Object result = null;
		for (FacadAspectFormater facadAspectFormater : aspectFormaters) {
			if (facadAspectFormater.accept(arg)) {
				result = facadAspectFormater.format(arg);
				break;
			}
		}
		return result;
	}

	private Method getUUidMethod(Class<?> clazz) {
		Method m = null;
		if (UUID_METHODS.containsKey(clazz)) {
			m = UUID_METHODS.get(clazz);
		} else {
			try {
				m = clazz.getMethod("getUuid", new Class<?>[0]);
			} catch (Exception e) {
				log.warn("Failed to get uuid method {}", clazz);
			}
			UUID_METHODS.put(clazz, m);
		}
		return m;
	}

	private Object callMethod(Method m, Object arg) {
		Object result = null;
		if (m != null) {
			try {
				result = m.invoke(arg, new Object[0]);
			} catch (Exception e) {
				log.warn("Failed to call uuid method {}", arg.getClass());
			}
		}
		return result;
	}

	private String handleHiddenClasses(Object arg, Class<?> clazz){
		String result = null;
		// on exclue les classes cachées
		if (hiddenClasses.contains(clazz.getName())) {
			result = clazz.getSimpleName() + "@xxx";
		}
		if(arg instanceof Collection && !((Collection<?>) arg).isEmpty() && ((Collection<?>) arg).stream().findFirst().isPresent()){
			String subClass = ((Collection<?>) arg).stream().findFirst().get().getClass().getName();
			int size = ((Collection<?>) arg).size();
			if(hiddenClasses.contains(subClass)){
				result =String.format("%s<%s>@xxx - Size %d",clazz.getName(), subClass, size);
			}
		}
		return result;
	}
}