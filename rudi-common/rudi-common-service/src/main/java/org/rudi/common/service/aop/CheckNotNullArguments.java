package org.rudi.common.service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Source : https://vlkan.com/blog/post/2015/01/30/java-null-check/
 */
@Aspect
@Component
public class CheckNotNullArguments {
	@Before("methodWithNotNullAnnotatedArguments() && inServiceClass()")
	public void nullCheck(JoinPoint joinPoint) {
		for (MethodArgument argument : MethodArgument.of(joinPoint)) {
			if (argument.hasAnnotation(NotNull.class) && argument.getValue() == null) {
				throw new IllegalArgumentException(argument.getName());
			}
		}
	}

	@Pointcut("within(org.rudi.microservice.*.service..*)")
	private void inServiceClass() {
		// Pointcut
	}

	@Pointcut("execution(* *(.., @javax.validation.constraints.NotNull (*), ..))")
	private void methodWithNotNullAnnotatedArguments() {
		// Pointcut
	}

	private static class MethodArgument {

		private final String name;
		private final List<Annotation> annotations;
		private final Object value;

		private MethodArgument(
				String name,
				List<Annotation> annotations,
				Object value) {
			this.name = name;
			this.annotations = Collections.unmodifiableList(annotations);
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public List<Annotation> getAnnotations() {
			return annotations;
		}

		public boolean hasAnnotation(Class<? extends Annotation> type) {
			for (Annotation annotation : annotations)
				if (annotation.annotationType().equals(type))
					return true;
			return false;
		}

		public Object getValue() {
			return value;
		}

		public static List<MethodArgument> of(JoinPoint joinPoint) {
			List<MethodArgument> arguments = new ArrayList<>();
			CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
			String[] names = codeSignature.getParameterNames();
			MethodSignature methodSignature =
					(MethodSignature) joinPoint.getStaticPart().getSignature();
			Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
			Object[] values = joinPoint.getArgs();
			for (int i = 0; i < values.length; i++) {
				arguments.add(new MethodArgument(names[i], Arrays.asList(annotations[i]), values[i]));
			}
			return Collections.unmodifiableList(arguments);
		}

	}
}
