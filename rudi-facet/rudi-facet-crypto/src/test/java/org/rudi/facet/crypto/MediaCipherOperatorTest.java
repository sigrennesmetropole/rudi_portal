package org.rudi.facet.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

class MediaCipherOperatorTest {

	@Test
	void test() throws GeneralSecurityException, IOException {
		final var spec = RudiAlgorithmSpec.DEFAULT;

		final var basefile = "files/payload.csv";
		final Resource payload = new ClassPathResource(basefile);

		final var mediaCipherOperator = new MediaCipherOperator(spec);
		final var keyPair = CryptoUtil.generateKeyPair(spec);

		final var encryptedOutput = new ByteArrayOutputStream();
		final var publicKey = keyPair.getPublic();
		mediaCipherOperator.encrypt(payload.getInputStream(), publicKey, encryptedOutput);

		final var encryptedInput = new ByteArrayInputStream(encryptedOutput.toByteArray());
		final var privateKey = keyPair.getPrivate();
		final var decryptedOutput = new ByteArrayOutputStream();
		mediaCipherOperator.decrypt(encryptedInput, privateKey, decryptedOutput);

		final var decryptedInput = new ByteArrayInputStream(decryptedOutput.toByteArray());
		assertThat(decryptedInput).hasSameContentAs(payload.getInputStream());
	}

	@Test
	void decrypt_irisa() throws GeneralSecurityException, IOException {
		final var spec = RudiAlgorithmSpec.DEFAULT;

		final Resource payload = new ClassPathResource("files/irisa/testDechiffrement.crypt");

		final var mediaCipherOperator = new MediaCipherOperator(spec);

		final var encryptedInput = payload.getInputStream();
		final var privateKeyPath = Paths.get("../../rudi-tools/rudi-tools-wso2-handler/src/main/resources/encryption_key.key");
		try (
				final var privateKeyInputStream = Files.newInputStream(privateKeyPath)
		) {
			final var privateKey = new KeyGeneratorFromPem().generatePrivateKey(spec.firstBlockSpec.keyPairAlgorithm, privateKeyInputStream);
			final var decryptedOutput = new ByteArrayOutputStream();
			mediaCipherOperator.decrypt(encryptedInput, privateKey, decryptedOutput);

			final var decryptedInput = new ByteArrayInputStream(decryptedOutput.toByteArray());
			final Resource expectedDecrypt = new ClassPathResource("files/irisa/testDechiffrement.decrypt");
			assertThat(decryptedInput).hasSameContentAs(expectedDecrypt.getInputStream());
		}
	}

}
