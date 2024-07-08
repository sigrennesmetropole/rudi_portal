/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.crypto.MediaCipherOperator;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.rudi.microservice.apigateway.service.ApigatewaySpringBootTest;
import org.rudi.microservice.apigateway.service.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@ApigatewaySpringBootTest
@ActiveProfiles(profiles = { "test", "${spring.profiles.test:test-env}", "encryption" })
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EncryptDecryptUtil {

	@Autowired
	private EncryptionService encryptionService;

	@Autowired
	private ResourceHelper resourceHelper;

	@Test
	@Disabled("This test is build to produce data to test decryption dataset")
	void encrypt_ispum() throws AppServiceException, IOException, GeneralSecurityException {
		final var spec = RudiAlgorithmSpec.DEFAULT;

		final var basefile = "ipsum.json";
		final Resource resource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(basefile);
		final InputStream ipsum = resource.getInputStream();

		final var mediaCipherOperator = new MediaCipherOperator(spec);
		final var publicKey = encryptionService.getPublicEncryptionKey(null);

		final var encryptedOutput = new ByteArrayOutputStream();
		mediaCipherOperator.encrypt(ipsum, publicKey, encryptedOutput);

		assertThat(encryptedOutput.size() > 0);

		final var encryptedInput = new ByteArrayInputStream(encryptedOutput.toByteArray());
		PrivateKey privateKey = encryptionService.getPrivateEncryptionKey(null, LocalDateTime.now());
		final var decryptedOutput = new ByteArrayOutputStream();
		mediaCipherOperator.decrypt(encryptedInput, privateKey, decryptedOutput);

		assertThat(decryptedOutput.size() > 0);

		try (FileOutputStream fout = new FileOutputStream("ipsum_crypted.json")) {
			fout.write(encryptedOutput.toByteArray());
		}

	}

	@Test
	@Disabled("This test is build to produce data to test decryption dataset")
	void encrypt_ispum2() throws AppServiceException, IOException, GeneralSecurityException {
		final var spec = RudiAlgorithmSpec.DEFAULT;

		final var basefile = "ipsum.txt";
		final Resource resource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(basefile);
		final InputStream ipsum = resource.getInputStream();

		final var mediaCipherOperator = new MediaCipherOperator(spec);
		final var publicKey = encryptionService.getPublicEncryptionKey(null);

		assertNotNull(publicKey);

		try (FileOutputStream fout = new FileOutputStream("ipsum_crypted.txt")) {
			mediaCipherOperator.encrypt(ipsum, publicKey, fout);
		}

	}

}
