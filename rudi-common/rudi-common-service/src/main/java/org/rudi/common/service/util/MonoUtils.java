package org.rudi.common.service.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonoUtils {

	private MonoUtils() {
	}

	/**
	 * @see #blockOrThrow(Mono, Function)
	 */
	public static <T, E extends Exception> T blockOrThrow(Mono<T> mono, Class<E> exceptionClass) throws E {
		try {
			return mono.block();
		} catch (RuntimeException reactorException) {
			final var reactorThrowable = Exceptions.unwrap(reactorException);
			final var exceptionIsAlreadyOfExpectedType = exceptionClass.isInstance(reactorThrowable) && exceptionClass.isAssignableFrom(reactorThrowable.getClass());
			if (exceptionIsAlreadyOfExpectedType) {
				throw (E) reactorThrowable;
			}
			throw encapsulate(reactorThrowable, exceptionClass);
		}
	}

	private static <E extends Exception> E encapsulate(Throwable encapsulatedException, Class<E> encapsulatingExceptionClass) {
		try {
			final Constructor<E> constructor = encapsulatingExceptionClass.getConstructor(Throwable.class);
			return constructor.newInstance(encapsulatedException);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
			log.error("Cannot create exception " + encapsulatingExceptionClass + ". By default, we will throw a " + encapsulatedException.getClass() + " exception.", e);
			throw Exceptions.propagate(e);
		}
	}
}