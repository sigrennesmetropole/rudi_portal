package org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.spec.GCMParameterSpec;

import org.rudi.facet.crypto.CipherOperator;
import org.rudi.facet.crypto.CryptoUtil;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MatchingDataCipherOperator extends CipherOperator {

	private final MatchingDataKeystoreProperties matchingDataKeystoreProperties;
	private final RudiAlgorithmSpec spec = RudiAlgorithmSpec.AES_DEFAULT;

	private KeyStore keyStore;

	public MatchingDataCipherOperator(MatchingDataKeystoreProperties matchingDataKeystoreProperties) {
		super(RudiAlgorithmSpec.AES_DEFAULT.cipherAlgorithm,
				RudiAlgorithmSpec.AES_DEFAULT.getMaximumBlockSizeInBytes());
		this.matchingDataKeystoreProperties = matchingDataKeystoreProperties;
	}

	public String encrypt(String matchingData) throws GeneralSecurityException, IOException {
		return encrypt(matchingData, matchingDataKeystoreProperties.getKeyAlias());
	}

	public String encrypt(String matchingData, String alias) throws GeneralSecurityException, IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(matchingData.getBytes(StandardCharsets.UTF_8));
		Key publicKey = getKey(alias);
		log.info("encrypt {} {}", alias, publicKey);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		final var initialisationVector = CryptoUtil.generateRandomNonce(spec.initializationVectorLength);
		final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(spec.authenticationTagLength,
				initialisationVector);

		encryptWithKey(input, publicKey, output, parameterSpec);

		return bytesToHex(initialisationVector) + bytesToHex(output.toByteArray());
	}

	public String decrypt(String data) throws GeneralSecurityException, IOException {
		return decrypt(data, matchingDataKeystoreProperties.getKeyAlias());
	}

	public String decrypt(String data, String alias) throws GeneralSecurityException, IOException {
		byte[] byteData = hexStringToByteArray(data);
		byte[] initialisationVector = Arrays.copyOfRange(byteData, 0, spec.initializationVectorLength);
		byte[] inputData = Arrays.copyOfRange(byteData, spec.initializationVectorLength, byteData.length);

		ByteArrayInputStream input = new ByteArrayInputStream(inputData);
		Key privateKey = getKey(alias);
		log.info("decrypt {} {}", alias, privateKey);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(spec.authenticationTagLength,
				initialisationVector);
		decryptWithKey(input, privateKey, output, parameterSpec);

		return output.toString();
	}

	private Key getKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException {
		if (keyStore == null) {
			keyStore = loadKeyStore();
		}
		return keyStore.getKey(alias, matchingDataKeystoreProperties.getKeystorePasswordChars());
	}

	private KeyStore loadKeyStore()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks;
		File keystoreFile = new File(matchingDataKeystoreProperties.getKeystorePath());
		boolean isFile = keystoreFile.exists() && keystoreFile.isFile();
		try (InputStream keyStoreStream = isFile ? new FileInputStream(matchingDataKeystoreProperties.getKeystorePath())
				: Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(matchingDataKeystoreProperties.getKeystorePath());) {

			ks = KeyStore.getInstance(matchingDataKeystoreProperties.getKeystoreType());
			ks.load(keyStoreStream, matchingDataKeystoreProperties.getKeystorePasswordChars());
		} catch (Exception e) {
			log.warn("Failed to load keyStore", e);
			throw e;
		}
		return ks;
	}

	private String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
