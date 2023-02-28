package org.rudi.facet.rva;

import org.rudi.facet.rva.exception.ExternalApiRvaException;
import org.rudi.facet.rva.exception.TooManyAddressesException;
import org.rudi.rva.core.bean.FullAddressesResponse;
import org.springframework.core.io.buffer.DataBufferLimitException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonoUtils {
	private MonoUtils() {
	}

	public static FullAddressesResponse blockOrThrow(Mono<FullAddressesResponse> mono) throws ExternalApiRvaException, TooManyAddressesException {
		var response = MonoUtils.internalBlockOrThrow(mono);
		if (response.getRva().getAnswer().getAddresses() == null) { // Si pas d'entrée addresses dans la reponse (et non pas d'éléments dans addresses)
			throw new ExternalApiRvaException(new Throwable("Un problème est survenu lors de l'appel (paramètres manquants ou incorrects)"));
		}
		return response;
	}

	private static <T> T internalBlockOrThrow(Mono<T> mono) throws ExternalApiRvaException, TooManyAddressesException {
		try {
			return mono
					.onErrorMap(DataBufferLimitException.class, TooManyAddressesException::new) // DataBufferLimitException (occurs when buffer limited error), cast to BusinessException
					.block();
		} catch (RuntimeException reactorException) {
			final Throwable reactorThrowable = Exceptions.unwrap(reactorException);
			if (reactorThrowable instanceof TooManyAddressesException) {
				throw (TooManyAddressesException) reactorThrowable;
			}
			throw new ExternalApiRvaException(reactorException); // others exceptions encapsulated as ExternalApiRvaException
		}
	}
}
