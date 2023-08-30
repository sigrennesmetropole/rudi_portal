package org.rudi.facet.apimaccess.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoUtils {

	private MonoUtils() {
	}

	/**
	 * @see #blockOrThrow(Mono, Function)
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Exception> T blockOrThrow(Mono<T> mono, Class<E> exceptionClass) throws E {
		try {
			return mono.block();
		} catch (RuntimeException reactorException) {
			final var reactorThrowable = Exceptions.unwrap(reactorException);
			final var exceptionIsAlreadyOfExpectedType = exceptionClass.isInstance(reactorThrowable)
					&& exceptionClass.isAssignableFrom(reactorThrowable.getClass());
			if (exceptionIsAlreadyOfExpectedType) {
				throw (E) reactorThrowable;
			}
			throw encapsulate(reactorThrowable, exceptionClass);
		}
	}

	private static <E extends Exception> E encapsulate(Throwable encapsulatedException,
			Class<E> encapsulatingExceptionClass) {
		try {
			final Constructor<E> constructor = encapsulatingExceptionClass.getConstructor(Throwable.class);
			return constructor.newInstance(encapsulatedException);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException
				| IllegalAccessException e) {
			log.error("Cannot create exception " + encapsulatingExceptionClass + ". By default, we will throw a "
					+ encapsulatedException.getClass() + " exception.", e);
			throw Exceptions.propagate(e);
		}
	}

	/**
	 * @param mono                  renvoie un résultat dans le cas nominal mais peut lancer une exception Reactor en cas d'erreur
	 * @param exceptionEncapsulator fonction qui encapsule l'exception Reactor
	 * @param <T>                   type de résultat du mono
	 * @param <E>                   type de l'exception à lancer
	 * @return le résultat du mono si aucune erreur
	 * @throws E l'exception créée en encapsulant l'exception Reactor en cas d'erreur avec le mono
	 * @see Mono#doOnError(Consumer)
	 * @see Mono#doOnError(Class, Consumer)
	 * @see Mono#onErrorContinue(Class, BiConsumer)
	 * @see Mono#onErrorResume(Function)
	 */
	public static <T, E extends Exception> T blockOrThrow(Mono<T> mono, Function<Throwable, E> exceptionEncapsulator)
			throws E {
		return blockOrCatchAndThrow(mono, Throwable.class, exceptionEncapsulator);
	}

	/**
	 * @param mono                  renvoie un résultat dans le cas nominal mais peut lancer une exception Reactor en cas d'erreur
	 * @param exceptionEncapsulator fonction qui encapsule l'exception Reactor
	 * @param <M>                   type de résultat du Mono
	 * @param <T>                   type de l'exception à catcher quand elle est lancée par le Mono
	 * @param <E>                   type de l'exception à lancer
	 * @return le résultat du mono si aucune erreur
	 * @throws E l'exception créée en encapsulant l'exception Reactor en cas d'erreur avec le Mono
	 * @see Mono#doOnError(Consumer)
	 * @see Mono#doOnError(Class, Consumer)
	 * @see Mono#onErrorContinue(Class, BiConsumer)
	 * @see Mono#onErrorResume(Function)
	 */
	public static <M, T, E extends Exception> M blockOrCatchAndThrow(Mono<M> mono, Class<T> throwableToCatchClass,
			Function<T, E> exceptionEncapsulator) throws E {
		try {
			return mono.block();
		} catch (RuntimeException reactorException) {
			final var reactorThrowable = Exceptions.unwrap(reactorException);
			if (throwableToCatchClass.isInstance(reactorThrowable)) {
				throw exceptionEncapsulator.apply(throwableToCatchClass.cast(reactorThrowable));
			} else {
				throw reactorException;
			}
		}
	}

}
