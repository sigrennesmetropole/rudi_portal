package org.rudi.facet.doks.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.crypto.KeyGeneratorFromPem;
import org.rudi.facet.crypto.MediaCipherOperator;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.rudi.facet.doks.properties.DoksProperties;
import org.springframework.stereotype.Component;

@Component
public
class DocumentCipherOperator {

	private final MediaCipherOperator mediaCipherOperator;
	private final KeyGeneratorFromPem keyGeneratorFromPem;
	private final ResourceHelper resourceHelper;
	private final DoksProperties doxProperties;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public DocumentCipherOperator(KeyGeneratorFromPem keyGeneratorFromPem, ResourceHelper resourceHelper, DoksProperties doxProperties) {
		mediaCipherOperator = new MediaCipherOperator(RudiAlgorithmSpec.DEFAULT);
		this.keyGeneratorFromPem = keyGeneratorFromPem;
		this.resourceHelper = resourceHelper;
		this.doxProperties = doxProperties;
	}

	/**
	 * @return InputStream sur le fichier chiffré, stocké dans un dossier temporaire
	 */
	public Path encrypt(InputStream decryptedStream) throws GeneralSecurityException, IOException {
		final var tmpFile = File.createTempFile("encrypted", ".bin");
		try (final var encryptedOutputStream = Files.newOutputStream(tmpFile.toPath())) {
			encrypt(decryptedStream, encryptedOutputStream);
			return tmpFile.toPath();
		}
	}

	public void encrypt(InputStream decryptedStream, OutputStream encryptedStream) throws GeneralSecurityException, IOException {
		mediaCipherOperator.encrypt(decryptedStream, getPublicKey(), encryptedStream);
	}

	private PublicKey getPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (publicKey == null) {
			final var keyPairAlgorithm = RudiAlgorithmSpec.DEFAULT.firstBlockSpec.keyPairAlgorithm;
			// TODO RUDI-2841 Générer une nouvelle clé, différente de celle des médias chiffrés de la RUDI-2387
			final var keyResource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(doxProperties.getPublicKeyPath());
			final var keyInputStream = keyResource.getInputStream();
			publicKey = keyGeneratorFromPem.generatePublicKey(keyPairAlgorithm, keyInputStream);
		}
		return publicKey;
	}

	public void decrypt(InputStream encryptedStream, OutputStream decryptedStream) throws GeneralSecurityException, IOException {
		mediaCipherOperator.decrypt(encryptedStream, getPrivateKey(), decryptedStream);
	}

	private PrivateKey getPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (privateKey == null) {
			final var keyPairAlgorithm = RudiAlgorithmSpec.DEFAULT.firstBlockSpec.keyPairAlgorithm;
			// TODO RUDI-2841 Générer une nouvelle clé, différente de celle des médias chiffrés de la RUDI-2387
			final var keyResource = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(doxProperties.getPrivateKeyPath());
			final var keyInputStream = keyResource.getInputStream();
			privateKey = keyGeneratorFromPem.generatePrivateKey(keyPairAlgorithm, keyInputStream);
		}
		return privateKey;
	}

}
