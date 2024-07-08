package org.rudi.microservice.apigateway.service.encryption.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.apigateway.service.ApigatewaySpringBootTest;
import org.rudi.microservice.apigateway.service.helper.KeyStoreHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import lombok.RequiredArgsConstructor;

@ApigatewaySpringBootTest
@ActiveProfiles(profiles = { "test", "${spring.profiles.test:test-env}", "encryption" })
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JksEncryptionServiceImplUT {
	private static final String DEFAULT_PUBLIC_KEY = "encryption/encryption_default_key_public.key";
	private static final String DEFAULT_PRIVATE_KEY = "encryption/encryption_default_key_private.key";

	private String defaultJksFilename;
	private File keyStoreTempFile;

	private final JksEncryptionServiceImpl encryptionServiceImpl;
	private final ResourceHelper resourceHelper;
	private final KeyStoreHelper keystoreHelper;

	@Value("${encryption-key.jks.password:rudi12345}")
	private String jksPassword;

	@BeforeEach
	void copyDefaultJKSToTempDir() throws IOException {
		defaultJksFilename = encryptionServiceImpl.getJksFilename();
		// copie temporaire pour ne pas ecraser le fichier JKS par défaut
		Resource r = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(defaultJksFilename);
		keyStoreTempFile = resourceHelper.copyResourceToTempFile(r);
		encryptionServiceImpl.setJksFilename(keyStoreTempFile.getName());
	}

	@Test
	void getDefaultPublicKey() throws IOException, AppServiceException {
		var dc = encryptionServiceImpl.getPublicEncryptionKeyAsDocumentContent(null);

		assertThat(FileUtils.readFileToString(dc.getFile(), "utf-8")).isEqualTo(FileUtils.readFileToString(
				resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_PUBLIC_KEY).getFile(),
				"utf-8"));
	}

	@Test
	void getDefaultPrivateKey() throws Exception {
		var pk = encryptionServiceImpl.getPrivateEncryptionKey(null, null);

		assertThat(getKeyAsString(pk)).isEqualTo(FileUtils.readFileToString(
				resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_PRIVATE_KEY).getFile(),
				"utf-8"));
	}

	@Test
	void getNewKey() throws Exception {
		UUID uuid1 = UUID.randomUUID();
		KeyStore ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(1);

		// Ajout d'une nouvelle entrée, différente de la clé par défaut
		PublicKey publicKey = encryptionServiceImpl.getPublicEncryptionKey(uuid1);
		ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(2);
		assertThat(getKeyAsString(publicKey)).isNotEqualTo(FileUtils.readFileToString(
				resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_PUBLIC_KEY).getFile(),
				"utf-8"));

		// recherche de la même clé => pas de nouvelle entrée
		PublicKey publicKey12 = encryptionServiceImpl.getPublicEncryptionKey(uuid1);
		ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(2);

		// Ajout d'une nouvelle clé => nouvelle entrée
		UUID uuid2 = UUID.randomUUID();
		PublicKey publicKey22 = encryptionServiceImpl.getPublicEncryptionKey(uuid2);
		ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		// nouvelle entrée dans le JKS
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(3);
	}

	@Test
	void getPrivateKey_unknownUuid() throws Exception {
		KeyStore ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(1);

		UUID uuid1 = UUID.randomUUID();
		// Récupération de la clé privée d'un uuid pour lequel aucune clé spécifique n'a été préparée
		PrivateKey privateKey = encryptionServiceImpl.getPrivateEncryptionKey(uuid1, null);
		assertThat(getKeyAsString(privateKey)).isEqualTo(FileUtils.readFileToString(
				resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_PRIVATE_KEY).getFile(),
				"utf-8"));
		ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(1);
	}

	@Test
	void getPrivateKey_newUuid() throws Exception {
		KeyStore ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(1);

		UUID uuid1 = UUID.randomUUID();
		// ajout d'une entrée spécifique pour cet uuid
		PublicKey publicKey12 = encryptionServiceImpl.getPublicEncryptionKey(uuid1);

		// Récupération de la clé privée
		PrivateKey privateKey = encryptionServiceImpl.getPrivateEncryptionKey(uuid1, null);
		assertThat(getKeyAsString(privateKey)).isNotEqualTo(FileUtils.readFileToString(
				resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_PRIVATE_KEY).getFile(),
				"utf-8"));
		ks = keystoreHelper.loadKeyStore(encryptionServiceImpl.getJksFilename(), jksPassword);
		assertThat(Collections.list(ks.aliases()).size()).isEqualTo(2);
	}

	@AfterEach
	void deleteTempJksFile() throws IOException {
		encryptionServiceImpl.setJksFilename(defaultJksFilename);
		FileUtils.delete(keyStoreTempFile);
	}

	private String getKeyAsString(Key key) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(out);
				JcaPEMWriter jcaWriter = new JcaPEMWriter(writer);) {
			jcaWriter.writeObject(key);
			jcaWriter.flush();
			return out.toString();
		}
	}
}
