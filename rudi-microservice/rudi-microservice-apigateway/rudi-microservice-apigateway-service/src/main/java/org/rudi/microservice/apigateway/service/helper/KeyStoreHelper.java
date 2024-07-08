package org.rudi.microservice.apigateway.service.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.crypto.CryptoUtil;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyStoreHelper {

	private final ResourceHelper resourceHelper;

	private static final RudiAlgorithmSpec spec = RudiAlgorithmSpec.DEFAULT;

	// Duree en secondes, 10 ans par défaut
	@Value("#{${encryption-key.jks.certificate.duration:315360000}}")
	private Long certificateDuration;

	@Value("${encryption-key.jks.certificate.algorithm:SHA256WithRSAEncryption}")
	private String certificateAlgorithm;

	@Value("${encryption-key.jks.certificate.issuer:Trust Anchor}")
	private String certificateIssuerName;

	/**
	 * Ajout d'une nouvelle clé dans un keystore
	 * 
	 * @param ks               le keystore
	 * @param keyStoreFilename le nom du fichier keystore
	 * @param keystorePassword le mot de passe du keystore
	 * @param alias            l'alias de la nouvelle clé
	 * @param currentDate      date actuelle
	 * @throws ExternalServiceException en cas d'erreur
	 */
	public synchronized void addNewKeyPairToKeyStore(KeyStore ks, String keyStoreFilename, String keystorePassword,
			String alias, LocalDateTime currentDate) throws ExternalServiceException {

		Resource r = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(keyStoreFilename);
		try {
			// Ecriture dans un fichier temporaire pour conserver le fichier initial en cas d'exception
			File keyStoreTempFile = resourceHelper.copyResourceToTempFile(r);
			String tempFilePath = keyStoreTempFile.getAbsolutePath();
			try (FileOutputStream fos = new FileOutputStream(keyStoreTempFile)) {
				KeyPair keyPair = CryptoUtil.generateKeyPair(spec);

				Certificate[] chain = CryptoUtil.generateCertificateChain(keyPair, currentDate, certificateAlgorithm,
						"CN=" + certificateIssuerName, certificateDuration);

				ks.setKeyEntry(alias, keyPair.getPrivate(), keystorePassword.toCharArray(), chain);
				ks.store(fos, keystorePassword.toCharArray());
				log.info("Création de l'alias dans le keystore {} terminée", tempFilePath);
			}
			log.debug("Copie du fichier temporaire {} dans le keystore {} ...", tempFilePath, r.getFile());
			FileUtils.copyFile(keyStoreTempFile, r.getFile());
			log.debug("Copie du fichier temporaire {} dans le keystore {} terminée", tempFilePath, r.getFile());
			FileUtils.delete(keyStoreTempFile);
			log.debug("Suppression du fichier temporaire {} terminée", tempFilePath);

			log.info("Alias {} créé dans le fichier {}", alias, r.getFile());
		} catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException
				| OperatorCreationException e) {
			throw new ExternalServiceException(
					String.format("Exception lors de la création d'une nouvelle paire de clé d'alias %s", alias), e);
		}

	}

	public KeyStore loadKeyStore(String keyStoreFilename, String keystorePassword)
			throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

		Resource r = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(keyStoreFilename);
		log.info("Ficher keystore trouvé {}", r.getFile());
		try (FileInputStream fis = new FileInputStream(r.getFile())) {
			keyStore.load(fis, keystorePassword.toCharArray());
		}
		return keyStore;
	}
}
