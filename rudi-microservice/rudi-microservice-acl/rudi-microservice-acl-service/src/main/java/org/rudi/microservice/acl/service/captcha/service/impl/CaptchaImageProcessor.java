package org.rudi.microservice.acl.service.captcha.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.acl.service.captcha.config.CaptchaProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import lombok.extern.slf4j.Slf4j;
import static java.nio.file.StandardOpenOption.WRITE;

@Component
@Slf4j
public class CaptchaImageProcessor extends AbstractCaptchaProcessor {
	private final ResourceHelper resourceHelper;

	public CaptchaImageProcessor(@Qualifier("captcha_webclient") WebClient captchaWebClient, CaptchaProperties captchaProperties, ResourceHelper resourceHelper) {
		super(captchaWebClient, captchaProperties);
		this.resourceHelper = resourceHelper;
	}

	@Override
	protected boolean hasToBeUsed(String typeCaptcha) {
		return StringUtils.equals(typeCaptcha, CAPTCHA_TYPE_IMAGE);
	}

	@Override
	protected DocumentContent generateCaptcha(String get, String c, String t, String cs, String d) throws ExternalServiceException {
		ClientResponse clientResponse = getClientResponse(get, c, t, cs, d);
		Flux<DataBuffer> captchaFlux = null;
		final List<MediaType> contentTypes = new ArrayList<>();
		if (clientResponse != null && clientResponse.statusCode().is2xxSuccessful()) {
			var contentType = clientResponse.headers().contentType();
			contentType.ifPresent(contentTypes::add);
			captchaFlux = clientResponse.bodyToFlux(DataBuffer.class);
		}

		final MediaType contentType = CollectionUtils.isNotEmpty(contentTypes) ? contentTypes.get(0) : null;
		if (contentType == null || captchaFlux == null) {
			return null;
		}

		try {
			File file = resourceHelper.createTemporaryFile();
			DataBufferUtils
					.write(captchaFlux, Path.of(file.getPath()), WRITE)
					.block();
			return new DocumentContent("imageFile", contentType.getType() + "/" + contentType.getSubtype(), file.length(), file);
		} catch (IOException ioException) {
			log.error("Exception lors des traitements sur le fichier temporaire (lecture ou écriture)");
			throw new ExternalServiceException("Exception lors des traitements sur le fichier temporaire (lecture ou écriture)", ioException);
		}
	}
}
