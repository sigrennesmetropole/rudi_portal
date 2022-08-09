package org.rudi.facet.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirstBlockCipherOperatorTest {

	@Test
	void test() throws GeneralSecurityException, IOException {
		final var spec = RudiAlgorithmSpec.DEFAULT;

		final SecretKey key = CryptoUtil.generateSecretKey(spec);
		final byte[] initialisationVector = CryptoUtil.generateRandomNonce(spec);
		final FirstBlock firstBlock = new FirstBlock(key, initialisationVector);

		final FirstBlockCipherOperator firstBlockCipher = new FirstBlockCipherOperator(spec);
		final var keyPair = CryptoUtil.generateKeyPair(spec);

		final var publicKey = keyPair.getPublic();
		final var encryptedFirstBlock = firstBlockCipher.encrypt(firstBlock, publicKey);

		final var privateKey = keyPair.getPrivate();
		final var decryptedFirstBlock = firstBlockCipher.decrypt(encryptedFirstBlock, privateKey);

		assertThat(decryptedFirstBlock).isEqualTo(firstBlock);
	}

}
