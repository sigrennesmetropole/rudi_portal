package org.rudi.facet.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoUtil {

	private CryptoUtil() {
	}

	static KeyPair generateKeyPair(RudiAlgorithmSpec spec) throws NoSuchAlgorithmException {
		return generateKeyPair(spec.firstBlockSpec.keyPairAlgorithm, spec.firstBlockSpec.keyPairKeySize);
	}

	static KeyPair generateKeyPair(String algorithm, int keysize) throws NoSuchAlgorithmException {
		final var generator = KeyPairGenerator.getInstance(algorithm);
		generator.initialize(keysize);
		return generator.generateKeyPair();
	}

	static SecretKey generateSecretKey(RudiAlgorithmSpec spec) throws NoSuchAlgorithmException {
		return generateSecretKey(spec.secretKeyAlgorithm, spec.secretKeySize);
	}

	static SecretKey generateSecretKey(String algorithm, int keysize) throws NoSuchAlgorithmException {
		final var keyGenerator = KeyGenerator.getInstance(algorithm);
		keyGenerator.init(keysize);
		return keyGenerator.generateKey();
	}

	static byte[] generateRandomNonce(RudiAlgorithmSpec spec) {
		return generateRandomNonce(spec.initializationVectorLength);
	}

	/**
	 * Génère une séquence aléatoire d'octets, par exemple pour un vecteur d'initialisation.
	 *
	 * @see <a href="https://www.hypr.com/nonce/">https://www.hypr.com/nonce/</a>
	 */
	public static byte[] generateRandomNonce(int length) {
		final var nonce = new byte[length];
		new SecureRandom().nextBytes(nonce);
		return nonce;
	}

}
