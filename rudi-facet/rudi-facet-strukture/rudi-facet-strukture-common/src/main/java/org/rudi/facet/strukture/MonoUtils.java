package org.rudi.facet.strukture;

import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.strukture.exceptions.StruktureApiException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class MonoUtils {

	private MonoUtils() {
	}

	public static <T, E extends StruktureApiException> T blockOrThrow(Mono<T> mono, Class<E> exceptionClass) throws E {
		try {
			return mono
					.onErrorMap(WebClientResponseException.class, error -> {
						try {
							final Constructor<E> constructor = exceptionClass.getConstructor(WebClientResponseException.class);
							return constructor.newInstance(error);
						} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
							log.error("Cannot create exception " + exceptionClass + ". By default, we will throw a " + error.getClass() + " exception.", e);
							throw Exceptions.propagate(e);
						}
					})
					.block();
		} catch (RuntimeException reactorException) {
			final Throwable reactorThrowable = Exceptions.unwrap(reactorException);
			if (exceptionClass.isInstance(reactorThrowable) && exceptionClass.isAssignableFrom(reactorThrowable.getClass())) {
				throw (E) reactorThrowable;
			}
			throw reactorException;
		}
	}

}
