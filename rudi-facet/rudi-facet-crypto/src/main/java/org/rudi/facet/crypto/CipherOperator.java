package org.rudi.facet.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CipherOperator {

	private final String algorithm;
	private final int bufferSize;

	protected byte[] encryptWithKey(byte[] bytes, Key key, @Nullable AlgorithmParameterSpec params)
			throws GeneralSecurityException, IOException {
		return doWithKey(Cipher.ENCRYPT_MODE, bytes, key, params);
	}

	protected byte[] decryptWithKey(byte[] bytes, Key key, @Nullable AlgorithmParameterSpec params)
			throws GeneralSecurityException, IOException {
		return doWithKey(Cipher.DECRYPT_MODE, bytes, key, params);
	}

	private byte[] doWithKey(int opmode, byte[] bytes, Key key, @Nullable AlgorithmParameterSpec params)
			throws GeneralSecurityException, IOException {
		try (final var inputStream = new ByteArrayInputStream(bytes);
				final var outputStream = new ByteArrayOutputStream()) {
			doWithKey(opmode, inputStream, key, outputStream, params);
			return outputStream.toByteArray();
		}
	}

	protected void encryptWithKey(InputStream decryptedStream, Key key, OutputStream encryptedStream,
			AlgorithmParameterSpec params) throws GeneralSecurityException, IOException {
		doWithKey(Cipher.ENCRYPT_MODE, decryptedStream, key, encryptedStream, params);
	}

	protected void decryptWithKey(InputStream encryptedStream, Key key, OutputStream decryptedStream,
			AlgorithmParameterSpec params) throws GeneralSecurityException, IOException {
		doWithKey(Cipher.DECRYPT_MODE, encryptedStream, key, decryptedStream, params);
	}

	protected Cipher decryptUpdateWithKey(InputStream encryptedStream, Key key, OutputStream decryptedStream,
			AlgorithmParameterSpec params) throws GeneralSecurityException, IOException {
		return doUpdateWithKey(Cipher.DECRYPT_MODE, encryptedStream, key, decryptedStream, params);
	}

	protected void decryptUpdateWithKey(Cipher cipher, InputStream encryptedStream, OutputStream decryptedStream)
			throws IOException {
		update(cipher, encryptedStream, decryptedStream);
	}

	protected void decryptFinalWithKey(Cipher cipher, OutputStream decryptedStream)
			throws GeneralSecurityException, IOException {
		doFinalWithKey(cipher, decryptedStream);
	}

	private void doWithKey(int opmode, InputStream inputStream, Key key, OutputStream outputStream,
			@Nullable AlgorithmParameterSpec params) throws GeneralSecurityException, IOException {
		final var cipher = Cipher.getInstance(algorithm);
		cipher.init(opmode, key, params);
		updateAndDoFinal(cipher, inputStream, outputStream);
	}

	/**
	 * @param inputStream  ce stream n'est pas {@link InputStream#close() fermé} par cette méthode
	 * @param outputStream ce stream n'est pas {@link OutputStream#close() fermé} par cette méthode
	 */
	private void updateAndDoFinal(Cipher cipher, InputStream inputStream, OutputStream outputStream)
			throws IOException, IllegalBlockSizeException, BadPaddingException {
		final var buffer = new byte[bufferSize];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);
			}
		}
		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}
	}

	public void doFinalWithKey(Cipher cipher, OutputStream outputStream) throws GeneralSecurityException, IOException {
		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}
	}

	public Cipher doUpdateWithKey(int opmode, InputStream inputStream, Key key, OutputStream outputStream,
			@Nullable AlgorithmParameterSpec params) throws GeneralSecurityException, IOException {
		final var cipher = Cipher.getInstance(algorithm);
		cipher.init(opmode, key, params);
		update(cipher, inputStream, outputStream);
		return cipher;
	}

	protected void update(Cipher cipher, InputStream inputStream, OutputStream outputStream) throws IOException {
		final var buffer = new byte[bufferSize];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);
			}
		}
	}

}
