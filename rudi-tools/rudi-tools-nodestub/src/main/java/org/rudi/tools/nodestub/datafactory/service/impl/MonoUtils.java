package org.rudi.tools.nodestub.datafactory.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.rudi.tools.nodestub.datafactory.service.config.DataFactoryApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

@Slf4j
class MonoUtils {

	private MonoUtils() {
	}

	public static <T, E extends DataFactoryApiException> T blockOrThrow(Mono<T> mono, Class<E> exceptionClass)
			throws E {
		try {
			return mono.onErrorMap(WebClientResponseException.class, error -> {
				try {
					log.warn("Failed to call datafactory", error);
					final Constructor<E> constructor = exceptionClass.getConstructor(WebClientResponseException.class);
					return constructor.newInstance(error);
				} catch (InvocationTargetException | NoSuchMethodException | InstantiationException
						| IllegalAccessException e) {
					log.error("Cannot create exception " + exceptionClass + ". By default, we will throw a "
							+ error.getClass() + " exception.", e);
					throw Exceptions.propagate(e);
				}
			}).block();
		} catch (RuntimeException reactorException) {
			final Throwable reactorThrowable = Exceptions.unwrap(reactorException);
			if (exceptionClass.isInstance(reactorThrowable)
					&& exceptionClass.isAssignableFrom(reactorThrowable.getClass())) {
				throw (E) reactorThrowable;
			}
			throw reactorException;
		}
	}

}
