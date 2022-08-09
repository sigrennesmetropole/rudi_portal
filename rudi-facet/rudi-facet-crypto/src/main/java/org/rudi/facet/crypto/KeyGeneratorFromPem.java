package org.rudi.facet.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.io.pem.PemReader;

// source initiale : https://www.baeldung.com/java-read-pem-file-keys#1-read-public-key
public class KeyGeneratorFromPem {

	// source : https://stackoverflow.com/a/55339208/1655155
	private static byte[] convertPkcs1ToPkcs8PrivateKeyBytes(byte[] pkcs1Bytes) {
		// We can't use Java internal APIs to parse ASN.1 structures, so we build a PKCS#8 key Java can understand
		final var pkcs1Length = pkcs1Bytes.length;
		final var totalLength = pkcs1Length + 22;
		final var pkcs8Header = new byte[]{
				0x30, (byte) 0x82, (byte) ((totalLength >> 8) & 0xff), (byte) (totalLength & 0xff), // Sequence + total length
				0x2, 0x1, 0x0, // Integer (0)
				0x30, 0xD, 0x6, 0x9, 0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0xD, 0x1, 0x1, 0x1, 0x5, 0x0, // Sequence: 1.2.840.113549.1.1.1, NULL
				0x4, (byte) 0x82, (byte) ((pkcs1Length >> 8) & 0xff), (byte) (pkcs1Length & 0xff) // Octet string + length
		};
		return ByteBuffer.allocate(pkcs8Header.length + pkcs1Bytes.length)
				.put(pkcs8Header)
				.put(pkcs1Bytes)
				.array();
	}

	public PublicKey generatePublicKey(String keyAlgorithm, InputStream keyInputStream) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		return readPemKey(keyAlgorithm, keyInputStream, (factory, pemContent) -> {
			final var pubKeySpec = new X509EncodedKeySpec(pemContent);
			return factory.generatePublic(pubKeySpec);
		});
	}

	public PrivateKey generatePrivateKey(String keyAlgorithm, InputStream keyInputStream) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		return readPemKey(keyAlgorithm, keyInputStream, (factory, pkcs1PrivateKeyBytes) -> {
			final var pkcs8PrivateKeyBytes = convertPkcs1ToPkcs8PrivateKeyBytes(pkcs1PrivateKeyBytes);
			final var privKeySpec = new PKCS8EncodedKeySpec(pkcs8PrivateKeyBytes);
			return factory.generatePrivate(privKeySpec);
		});
	}

	private <T> T readPemKey(String keyAlgorithm, InputStream keyInputStream, KeyGenerator<T> keyGenerator) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		try (
				final var keyReader = new InputStreamReader(keyInputStream);
				final var pemReader = new PemReader(keyReader)
		) {
			final var pemObject = pemReader.readPemObject();
			final var pemContent = pemObject.getContent();
			final var factory = KeyFactory.getInstance(keyAlgorithm);
			return keyGenerator.generate(factory, pemContent);
		}
	}

	private interface KeyGenerator<T> {
		T generate(KeyFactory factory, byte[] pemContent) throws InvalidKeySpecException;
	}
}
