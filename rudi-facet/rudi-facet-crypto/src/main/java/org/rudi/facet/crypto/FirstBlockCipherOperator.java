package org.rudi.facet.crypto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

class FirstBlockCipherOperator extends CipherOperator {
	private final RudiAlgorithmSpec spec;

	FirstBlockCipherOperator(RudiAlgorithmSpec spec) {
		super(spec.firstBlockSpec.cipherAlgorithm, spec.firstBlockSpec.getFirstBlockSizeInBytes());
		this.spec = spec;
	}

	byte[] encrypt(FirstBlock firstBlock, Key publicKey) throws GeneralSecurityException, IOException {
		final var firstBlockBytes = ByteBuffer
				.allocate(spec.getSecretKeySizeInBytes() + spec.initializationVectorLength)
				.put(firstBlock.getSecretKey().getEncoded()).put(firstBlock.getInitialisationVector()).array();
		return encryptWithKey(firstBlockBytes, publicKey, spec.firstBlockSpec.cipherAlgorithmParams);
	}

	FirstBlock decrypt(byte[] encryptedFirstBlock, Key privateKey) throws GeneralSecurityException, IOException {

		byte[] decryptedFirstBlock = decryptWithKey(encryptedFirstBlock, privateKey,
				spec.firstBlockSpec.cipherAlgorithmParams);

		final var secretKeySizeInBytes = spec.getSecretKeySizeInBytes();
		final var secretKeyBytes = ByteBuffer.allocate(secretKeySizeInBytes)
				.put(decryptedFirstBlock, 0, secretKeySizeInBytes).array();
		final var secretKey = new SecretKeySpec(secretKeyBytes, spec.secretKeyAlgorithm);

		final var initializationVectorBytes = ByteBuffer.allocate(spec.initializationVectorLength)
				.put(decryptedFirstBlock, secretKeySizeInBytes, spec.initializationVectorLength).array();

		return new FirstBlock(secretKey, initializationVectorBytes);
	}

}
