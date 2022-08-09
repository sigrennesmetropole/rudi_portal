package org.rudi.microservice.konsult.service.encryption.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.konsult.service.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {

	@Value("${media.encryption.public-key:encryption_key_public.key}")
	private String secret;

	@Value("${spring.config.additional-location:}")
	private String additionalLocation;

	private final ResourceHelper resourceHelper;

	@Override
	public DocumentContent getPublicEncryptionKey() throws AppServiceException {
		Resource publicKeyResource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(secret);

		if(!publicKeyResource.exists()) {
			String errorMessage = "Impossible de récupérer le fichier de clé publique : \"" + secret + "\"";
			if(StringUtils.isNotEmpty(additionalLocation)) {
				errorMessage += " dans le répertoire additionalLocation : \"" + additionalLocation + "\" ou";
			}
			errorMessage += " dans le classpath";
			throw new AppServiceException(errorMessage);
		}

		try {
			return resourceHelper.convertToDocumentContent(publicKeyResource);
		} catch (IOException e) {
			throw new AppServiceException("Erreur I/O lors de la récupération du fichier de clé publique", e);
		}
	}
}
