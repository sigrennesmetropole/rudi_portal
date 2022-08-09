package org.rudi.facet.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.spec.GCMParameterSpec;

/**
 * Chiffrement et déchiffrement de média chiffré par la clé publique RUDI.
 */
// Sources initiales : https://www.baeldung.com/java-aes-encryption-decryption + https://mkyong.com/java/java-aes-encryption-and-decryption/
public class MediaCipherOperator extends CipherOperator {

	private final RudiAlgorithmSpec spec;
	private final FirstBlockCipherOperator firstBlockCipherOperator;

	public MediaCipherOperator(RudiAlgorithmSpec spec) {
		super(spec.cipherAlgorithm, spec.getMaximumBlockSizeInBytes());
		this.spec = spec;
		this.firstBlockCipherOperator = new FirstBlockCipherOperator(spec);
	}

	public void encrypt(InputStream decryptedStream, Key publicKey, OutputStream encryptedStream) throws GeneralSecurityException, IOException {
		final var secretKey = CryptoUtil.generateSecretKey(spec.secretKeyAlgorithm, spec.secretKeySize);
		final var initialisationVector = CryptoUtil.generateRandomNonce(spec.initializationVectorLength);
		final var firstBlock = new FirstBlock(secretKey, initialisationVector);

		final var encryptedFirstBlock = firstBlockCipherOperator.encrypt(firstBlock, publicKey);
		encryptedStream.write(encryptedFirstBlock);

		final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(spec.authenticationTagLength, initialisationVector);
		encryptWithKey(decryptedStream, secretKey, encryptedStream, parameterSpec);
	}

	public void decrypt(InputStream encryptedStream, Key privateKey, OutputStream decryptedStream) throws GeneralSecurityException, IOException {
		final var firstBlock = decryptFirstBlock(encryptedStream, privateKey, spec);
		final var secretKey = firstBlock.getSecretKey();
		final var initialisationVector = firstBlock.getInitialisationVector();

		final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(spec.authenticationTagLength, initialisationVector);
		decryptWithKey(encryptedStream, secretKey, decryptedStream, parameterSpec);
	}

	private FirstBlock decryptFirstBlock(InputStream encryptedStream, Key privateKey, RudiAlgorithmSpec rudiSpec) throws GeneralSecurityException, IOException {
		final var encryptedFirstBlock = new byte[rudiSpec.firstBlockSpec.getFirstBlockSizeInBytes()];
		encryptedStream.read(encryptedFirstBlock);
		return firstBlockCipherOperator.decrypt(encryptedFirstBlock, privateKey);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage : encrypt  PUBLIC_KEY_PATH FILE");
			System.err.println("   or : decrypt PRIVATE_KEY_PATH FILE");
			System.err.println("Encrypt or decrypt a file.");
			System.err.println("");
			System.err.println("MODE : \"encrypt\" or \"decrypt\"");
			System.exit(1);
		}

		final var modeString = args[0];
		final var keyPath = Paths.get(args[1]);
		final var inputFilePath = Paths.get(args[2]);

		final var outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName() + "." + modeString + "ed");

		try (
				final var inputFileStream = Files.newInputStream(inputFilePath);
				final var outputFileStream = Files.newOutputStream(outputFilePath)
		) {
			final var mediaCipherOperator = new MediaCipherOperator(RudiAlgorithmSpec.DEFAULT);
			final var keyGeneratorFromPem = new KeyGeneratorFromPem();
			final var keyAlgorithm = mediaCipherOperator.spec.firstBlockSpec.keyPairAlgorithm;

			if (modeString.equals("encrypt")) {
				final var publicKey = keyGeneratorFromPem.generatePublicKey(keyAlgorithm, Files.newInputStream(keyPath));
				mediaCipherOperator.encrypt(inputFileStream, publicKey, outputFileStream);
			}
			if (modeString.equals("decrypt")) {
				final var privateKey = keyGeneratorFromPem.generatePrivateKey(keyAlgorithm, Files.newInputStream(keyPath));
				mediaCipherOperator.decrypt(inputFileStream, privateKey, outputFileStream);
			}
		}

		System.out.println("Output file path : " + outputFilePath);
	}

}
