package org.rudi.microservice.apigateway.facade.config.gateway.filters;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.rudi.facet.crypto.FirstBlock;
import org.rudi.facet.crypto.MediaCipherOperator;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.apigateway.facade.config.gateway.ApiGatewayConstants;
import org.rudi.microservice.apigateway.facade.config.gateway.exception.UnauthorizedException;
import org.rudi.microservice.apigateway.service.encryption.EncryptionService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class DatasetDecryptGatewayFilterFactory
		extends AbstractGatewayFilterFactory<DatasetDecryptGatewayFilterFactory.Context> {

	public static final String MIME_TYPE = "mimeType";

	public static final String ENCRYPTED_PROPERTY = "encrypted";

	public static final String MIME_TYPE_PROPERTY = "mime_type";

	public static final String PUBLIC_KEY_URL_PROPERTY = "public_key_url";

	public static final String PUBLIC_KEY_PARTIAL_CONTENT_PROPERTY = "pub_key_cut";

	public static final String PUBLIC_KEY_PARTIAL_CONTENT_CAMEL_CASE_PROPERTY = "pubKeyCut";

	public static final String MIME_TYPE_CRYPT_SUFFIXE = "+crypt";

	private final EncryptionService encryptionService;

	private final DatasetService datasetService;

	public DatasetDecryptGatewayFilterFactory(EncryptionService encryptionService, DatasetService datasetService) {
		super(Context.class);
		this.encryptionService = encryptionService;
		this.datasetService = datasetService;
	}

	/**
	 * Returns hints about the number of args and the order for shortcut parsing.
	 *
	 * @return the list of hints
	 */
	@Override
	public List<String> shortcutFieldOrder() {
		return List.of(MIME_TYPE);
	}

	/**
	 * @param context le context contenant les objets nécessaire au bon fonctionnement du filter
	 * @return le filter
	 */
	@Override
	public GatewayFilter apply(Context context) {
		DatasetDecryptFilter gatewayFilter = new DatasetDecryptFilter(context, encryptionService, datasetService);
		gatewayFilter.setGatewayFilterFactory(this);
		return gatewayFilter;
	}

	@Getter
	@Setter
	public static class Context {
		private String mimeType;
		private FirstBlock firstBlock;
		private Cipher cipher;
		private long totalLengthToRead;
		private long totalLengthWrited;
		private long totalRead;
		private int readCount;
		private byte[] buffer;

		public Context() {
			log.info("Create new context...");
		}

		public void ensureBuffer(int size) {
			if (buffer == null) {
				buffer = new byte[size];
			} else if (buffer.length < size) {
				buffer = Arrays.copyOf(buffer, size);
			}
		}

		public void clearBuffer() {
			readCount = 0;
		}

		public void incrementReadCount(int increment) {
			readCount += increment;
			totalRead += increment;
		}

		public void incrementWriteCount(int increment) {
			totalLengthWrited += increment;
		}
	}

	public class DatasetDecryptFilter implements GatewayFilter, Ordered {

		private final Context config;

		private final EncryptionService encryptionService;

		private final DatasetService datasetService;

		@Setter
		private GatewayFilterFactory<Context> gatewayFilterFactory;

		public DatasetDecryptFilter(Context config, EncryptionService encryptionService,
				DatasetService datasetService) {
			this.config = config;
			this.encryptionService = encryptionService;
			this.datasetService = datasetService;
		}

		@Override
		public int getOrder() {
			return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
		}

		@Override
		public String toString() {
			Object obj = (this.gatewayFilterFactory != null) ? this.gatewayFilterFactory : this;
			return filterToStringCreator(obj).append("New content type", config.getMimeType()).toString();
		}

		/**
		 * Process the Web request and (optionally) delegate to the next {@code WebFilter} through the given {@link GatewayFilterChain}.
		 *
		 * @param exchange the current server exchange
		 * @param chain    provides a way to delegate to the next filter
		 * @return {@code Mono<Void>} to indicate when request processing is complete
		 */
		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			return chain.filter(exchange.mutate()
					.response(new ModifiedServerHttpResponse(exchange, config, encryptionService, datasetService))
					.build());
		}

	}

	/**
	 * Classe décorator de la réponse
	 */
	protected class ModifiedServerHttpResponse extends ServerHttpResponseDecorator {
		private final ServerWebExchange exchange;
		private final Context context;
		private final EncryptionService encryptionService;
		private final DatasetService datasetService;

		public ModifiedServerHttpResponse(ServerWebExchange exchange, Context context,
				EncryptionService encryptionService, DatasetService datasetService) {
			super(exchange.getResponse());
			this.exchange = exchange;
			this.context = context;
			this.encryptionService = encryptionService;
			this.datasetService = datasetService;
		}

		/**
		 * @param body the body content publisher
		 * @return Mono<Void> après avoir modifié la response
		 */
		@Override
		public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
			HttpHeaders httpHeaders = exchange.getResponse().getHeaders();
			handleContentType(httpHeaders);
			handleContentDisposition(httpHeaders);
			int size = handleContentLength(httpHeaders);

			Pair<UUID, UUID> datasetIdentifiers = extractDatasetIdentifiers(exchange);
			LocalDateTime mediaUpdateDate = null;
			final PrivateKey privateKey;
			final MediaCipherOperator mediaCipherOperator;
			try {
				mediaUpdateDate = extractMadiaUpdatedDate(datasetIdentifiers);
				privateKey = encryptionService.getPrivateEncryptionKey(datasetIdentifiers.getRight(), mediaUpdateDate);
				mediaCipherOperator = new MediaCipherOperator(RudiAlgorithmSpec.DEFAULT);
			} catch (Exception e) {
				return exchange.getResponse().setComplete();
			}
			log.info("Updated date {}", mediaUpdateDate);

			// puis on traite le body
			if (body instanceof Flux) {
				// si le publisher est un flux
				log.info("Body is a flux...");
				return writeWith(body, size, privateKey, mediaCipherOperator);
			} else {
				log.info("Body is not a flux...");
				// le body n'est pas un flux - on laisse passer sans modification
				return super.writeWith(body);
			}
		}

		private int handleContentLength(HttpHeaders httpHeaders) {
			context.setTotalLengthToRead(httpHeaders.getContentLength());

			// on récupère un peu de context
			int size = RudiAlgorithmSpec.DEFAULT.getMaximumBlockSizeInBytes();
			long newContentLength = context.getTotalLengthToRead() - size - 16;
			httpHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(newContentLength));
			log.info("Change {} from {} header to {}", HttpHeaders.CONTENT_LENGTH, context.getTotalLengthToRead(),
					newContentLength);
			return size;
		}

		private void handleContentType(HttpHeaders httpHeaders) {
			String newMimeType = context.getMimeType();
			if (StringUtils.isEmpty(newMimeType)) {
				String contentTypeValue = httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
				newMimeType = StringUtils.remove(contentTypeValue, MIME_TYPE_CRYPT_SUFFIXE);
			}
			if (StringUtils.isEmpty(newMimeType)) {
				log.info("Change {} from {} header to {}", HttpHeaders.CONTENT_TYPE,
						httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE), newMimeType);
				httpHeaders.set(HttpHeaders.CONTENT_TYPE, newMimeType);
			}
		}

		private void handleContentDisposition(HttpHeaders httpHeaders) {
			String contentTypeValue = httpHeaders.getFirst(HttpHeaders.CONTENT_DISPOSITION);
			if (contentTypeValue != null) {
				contentTypeValue = StringUtils.remove(contentTypeValue, MIME_TYPE_CRYPT_SUFFIXE);
			}
			log.info("Change {} from {} header to {}", HttpHeaders.CONTENT_TYPE,
					httpHeaders.getFirst(HttpHeaders.CONTENT_DISPOSITION), contentTypeValue);
			httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, contentTypeValue);
		}

		private Mono<Void> writeWith(Publisher<? extends DataBuffer> body, int size, final PrivateKey privateKey,
				final MediaCipherOperator mediaCipherOperator) {
			ServerHttpResponse originalResponse = exchange.getResponse();
			DataBufferFactory bufferFactory = originalResponse.bufferFactory();
			Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

			return super.writeWith(fluxBody.map(dataBuffer -> {

				DataBuffer result = null;
				int restByteCount = -1;
				do {
					restByteCount = readAtMostABlock(dataBuffer, size);

					if (context.getReadCount() == size || context.getTotalRead() == context.getTotalLengthToRead()) {
						// j'ai la taille d'un block
						log.info("Block full and ready...");
						result = handleBlock(result, bufferFactory, mediaCipherOperator, privateKey);
					}
				} while (restByteCount > 0);

				if (context.getTotalRead() == context.getTotalLengthToRead()) {
					result = handleLastBlock(result, mediaCipherOperator, bufferFactory);
				}

				// si on a pas de résultat on créé un résultat vide
				if (result == null) {
					log.info("Send 0 byte...");
					result = bufferFactory.wrap(new byte[0]);
				}

				return result;
			}));
		}

		private DataBuffer handleBlock(DataBuffer result, DataBufferFactory bufferFactory,
				final MediaCipherOperator mediaCipherOperator, final PrivateKey privateKey) {
			try {
				ByteArrayInputStream encryptedInputStream = new ByteArrayInputStream(context.getBuffer(), 0,
						context.getReadCount());
				if (context.getFirstBlock() == null) {
					// c'est le premier block
					handleFirstBlock(privateKey, mediaCipherOperator, encryptedInputStream);
				} else {
					// c'est un bloc suivant
					result = handleNextBlock(result, bufferFactory, mediaCipherOperator, encryptedInputStream);
				}
				// reset du buffer
				context.clearBuffer();
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to decode block", e);
			}
			return result;
		}

		private DataBuffer handleLastBlock(DataBuffer result, final MediaCipherOperator mediaCipherOperator,
				DataBufferFactory bufferFactory) {
			try {
				// le problème c'est que le déchiffrement s'appuie sur tout le fichier
				// donc on recoit dans le byteArray toutes les données.
				// il faudrait chiffrer par partie (i.e. par block de 4096 par exemple)
				// et avoir un format de type <clé aes chiffré><taille du bloc><bloc1><bloc2>
				ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
				mediaCipherOperator.decryptFinalNextBlokcs(context.getCipher(), decryptedStream);
				if (result == null) {
					result = bufferFactory.wrap(decryptedStream.toByteArray());
				} else {
					result.write(decryptedStream.toByteArray());
				}
				context.incrementWriteCount(decryptedStream.size());
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to decode block", e);
			}
			return result;
		}

		private DataBuffer handleNextBlock(DataBuffer result, DataBufferFactory bufferFactory,
				final MediaCipherOperator mediaCipherOperator, ByteArrayInputStream encryptedInputStream)
				throws GeneralSecurityException, IOException {
			log.info("Decrypt next block");
			ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
			if (context.getCipher() == null) {
				context.setCipher(mediaCipherOperator.decryptUpdateNextBlocks(context.getFirstBlock().getSecretKey(),
						context.getFirstBlock().getInitialisationVector(), encryptedInputStream, decryptedStream));
			} else {
				mediaCipherOperator.decryptUpdateNextBlocks(context.getCipher(), encryptedInputStream, decryptedStream);
			}

			// écriture du bloc déchiffré
			if (result == null) {
				result = bufferFactory.wrap(decryptedStream.toByteArray());
			} else {
				result.write(decryptedStream.toByteArray());
			}
			context.incrementWriteCount(decryptedStream.size());
			return result;
		}

		private void handleFirstBlock(final PrivateKey privateKey, final MediaCipherOperator mediaCipherOperator,
				ByteArrayInputStream encryptedInputStream) throws GeneralSecurityException, IOException {
			log.info("Initialize first block");
			FirstBlock firstBlock = mediaCipherOperator.decryptFirstBlock(encryptedInputStream, privateKey,
					RudiAlgorithmSpec.DEFAULT);
			context.setFirstBlock(firstBlock);
		}

		private int readAtMostABlock(DataBuffer dataBuffer, int size) {
			// on lit la longueur pour avoir un block (ou moins)
			context.ensureBuffer(size);
			int dataBufferReadableByteCount = dataBuffer.readableByteCount();
			int dataBufferReadByteCount = context.getReadCount();
			int lengthToRead = Math.min(size - dataBufferReadByteCount, dataBufferReadableByteCount);
			dataBuffer.read(context.getBuffer(), dataBufferReadByteCount, lengthToRead);
			context.incrementReadCount(lengthToRead);
			log.info("Bytes alredy read {} => Read {} bytes of {} available. Rest {}", dataBufferReadByteCount,
					lengthToRead, dataBufferReadableByteCount, dataBufferReadableByteCount - lengthToRead);
			return dataBufferReadableByteCount - lengthToRead;
		}

		/**
		 * @param exchange ServerWebExchange de la requête
		 * @return Pair<UUID, UUID> : Pair.of(globalId, mediaId);
		 */
		private Pair<UUID, UUID> extractDatasetIdentifiers(ServerWebExchange exchange) {
			ServerHttpRequest request = exchange.getRequest();
			UUID globalId = UUID.fromString(request.getPath()
					.subPath(ApiGatewayConstants.GLOBAL_ID_INDEX, ApiGatewayConstants.GLOBAL_ID_INDEX + 1).toString());
			UUID mediaId = UUID.fromString(request.getPath()
					.subPath(ApiGatewayConstants.MEDIA_ID_INDEX, ApiGatewayConstants.MEDIA_ID_INDEX + 1).toString());
			return Pair.of(globalId, mediaId);
		}

		private LocalDateTime extractMadiaUpdatedDate(Pair<UUID, UUID> datasetIdentifiers)
				throws DataverseAPIException, UnauthorizedException {
			// récupératio du dataset
			Metadata metadata = datasetService.getDataset(datasetIdentifiers.getLeft());

			// Récupération du media concerné
			Optional<@Valid Media> media = metadata.getAvailableFormats().stream()
					.filter(m -> m.getMediaId().equals(datasetIdentifiers.getRight())).findFirst();

			if (media.isEmpty()) {
				throw new UnauthorizedException("Incoherent call");
			}

			if (media.get().getMediaDates() != null && media.get().getMediaDates().getUpdated() != null) {
				return media.get().getMediaDates().getUpdated().toLocalDateTime();
			} else {
				return LocalDateTime.now();
			}
		}
	}
}
