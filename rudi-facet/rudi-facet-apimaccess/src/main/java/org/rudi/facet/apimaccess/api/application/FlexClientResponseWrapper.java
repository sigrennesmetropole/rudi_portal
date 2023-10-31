package org.rudi.facet.apimaccess.api.application;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.rudi.facet.apimaccess.api.ContentTypeUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.support.ClientResponseWrapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FlexClientResponseWrapper extends ClientResponseWrapper {

	private static final ResolvableType VOID_TYPE = ResolvableType.forClass(Void.class);

	private final ClientResponse delegate;

	public FlexClientResponseWrapper(ClientResponse delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
		return delegate.body(FlexClientResponseWrapper.toFlux(elementClass));
	}

	/**
	 * Extractor to decode the input content into {@code Flux<T>}.
	 * 
	 * @param elementClass the class of the element type to decode to
	 * @param <T>          the element type to decode to
	 * @return {@code BodyExtractor} for {@code Flux<T>}
	 */
	public static <T> BodyExtractor<Flux<T>, ReactiveHttpInputMessage> toFlux(Class<? extends T> elementClass) {
		return toFlux(ResolvableType.forClass(elementClass));
	}

	private static <T> BodyExtractor<Flux<T>, ReactiveHttpInputMessage> toFlux(ResolvableType elementType) {
		return (inputMessage, context) -> readWithMessageReaders(inputMessage, context, elementType,
				(HttpMessageReader<T> reader) -> readToFlux(inputMessage, context, elementType, reader),
				ex -> unsupportedErrorHandler(inputMessage, ex), skipBodyAsFlux(inputMessage));
	}

	private static <T, S extends Publisher<T>> S readWithMessageReaders(ReactiveHttpInputMessage message,
			BodyExtractor.Context context, ResolvableType elementType, Function<HttpMessageReader<T>, S> readerFunction,
			Function<UnsupportedMediaTypeException, S> errorFunction, Supplier<S> emptySupplier) {

		if (VOID_TYPE.equals(elementType)) {
			return emptySupplier.get();
		}
		// ReactorClientHttpResponse
		String contentTypeValue = message.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
		var contentType = ContentTypeUtils.normalize(contentTypeValue);

		return context.messageReaders().stream().filter(reader -> reader.canRead(elementType, contentType)).findFirst()
				.map(FlexClientResponseWrapper::<T>cast).map(readerFunction).orElseGet(() -> {
					List<MediaType> mediaTypes = context.messageReaders().stream()
							.flatMap(reader -> reader.getReadableMediaTypes().stream()).collect(Collectors.toList());
					return errorFunction.apply(new UnsupportedMediaTypeException(contentType, mediaTypes, elementType));
				});
	}

	private static <T> Flux<T> readToFlux(ReactiveHttpInputMessage message, BodyExtractor.Context context,
			ResolvableType type, HttpMessageReader<T> reader) {

		var wrappedMessage = new FlexClientHttpResponseDecorator((ClientHttpResponse) message);
		return context.serverResponse()
				.map(response -> reader.read(type, type, (ServerHttpRequest) wrappedMessage, response, context.hints()))
				.orElseGet(() -> reader.read(type, wrappedMessage, context.hints()));
	}

	@SuppressWarnings("unchecked")
	private static <T> HttpMessageReader<T> cast(HttpMessageReader<?> reader) {
		return (HttpMessageReader<T>) reader;
	}

	protected static <T> BodyExtractor<Mono<T>, ReactiveHttpInputMessage> toMono(ResolvableType elementType) {
		return (inputMessage, context) -> readWithMessageReaders(inputMessage, context, elementType,
				(HttpMessageReader<T> reader) -> readToMono(inputMessage, context, elementType, reader),
				ex -> Mono.from(unsupportedErrorHandler(inputMessage, ex)), skipBodyAsMono(inputMessage));
	}

	private static <T> Mono<T> readToMono(ReactiveHttpInputMessage message, BodyExtractor.Context context,
			ResolvableType type, HttpMessageReader<T> reader) {

		return context.serverResponse()
				.map(response -> reader.readMono(type, type, (ServerHttpRequest) message, response, context.hints()))
				.orElseGet(() -> reader.readMono(type, message, context.hints()));
	}

	private static <T> Flux<T> unsupportedErrorHandler(ReactiveHttpInputMessage message,
			UnsupportedMediaTypeException ex) {

		Flux<T> result;
		if (message.getHeaders().getContentType() == null) {
			// Maybe it's okay there is no content type, if there is no content..
			result = message.getBody().map(buffer -> {
				DataBufferUtils.release(buffer);
				throw ex;
			});
		} else {
			result = message instanceof ClientHttpResponse ? consumeAndCancel(message).thenMany(Flux.error(ex))
					: Flux.error(ex);
		}
		return result;
	}

	private static <T> Supplier<Mono<T>> skipBodyAsMono(ReactiveHttpInputMessage message) {
		return message instanceof ClientHttpResponse ? () -> consumeAndCancel(message).then(Mono.empty()) : Mono::empty;
	}

	private static <T> Supplier<Flux<T>> skipBodyAsFlux(ReactiveHttpInputMessage message) {
		return message instanceof ClientHttpResponse ? () -> consumeAndCancel(message).thenMany(Mono.empty())
				: Flux::empty;
	}

	private static Flux<DataBuffer> consumeAndCancel(ReactiveHttpInputMessage message) {
		return message.getBody().takeWhile(buffer -> {
			DataBufferUtils.release(buffer);
			return false;
		});
	}

}
