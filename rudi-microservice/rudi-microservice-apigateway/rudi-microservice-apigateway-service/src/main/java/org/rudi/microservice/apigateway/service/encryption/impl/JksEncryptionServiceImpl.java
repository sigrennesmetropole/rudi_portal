package org.rudi.microservice.apigateway.service.encryption.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.apigateway.service.encryption.EncryptionService;
import org.rudi.microservice.apigateway.service.helper.KeyStoreHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "encryption-key.implementation", havingValue = "jks")
@Slf4j
public class JksEncryptionServiceImpl implements EncryptionService {

	private static final String DATE_FORMAT_FOR_ALIAS = "yyyyMMddHHmmss";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_FOR_ALIAS);

	@Value("${encryption-key.jks.default-key-alias:defaultkey}")
	private String defaultKeyAlias;

	@Value("${encryption-key.jks.default-key-password:rudi123}")
	private String defaultKeyPassword;

	@Value("${encryption-key.jks.filename:rudi-apigateway.jks}")
	@Getter
	@Setter
	private String jksFilename;

	@Value("${encryption-key.jks.password:rudi12345}")
	private String jksPassword;

	// Duree en secondes, 5 ans par défaut
	@Value("#{${encryption-key.jks.key-duration:157680000}}")
	private Long keyDuration;

	private final KeyStoreHelper keystoreHelper;

	private final ResourceHelper resourceHelper;

	@Override
	public DocumentContent getPublicEncryptionKeyAsDocumentContent(UUID mediaId) throws AppServiceException {
		PublicKey publicKey = getPublicEncryptionKey(mediaId);
		return loadKeyAsDocumentContent(publicKey);
	}

	@Override
	public PublicKey getPublicEncryptionKey(UUID mediaId) throws AppServiceException {

		try {
			KeyStore ks = keystoreHelper.loadKeyStore(jksFilename, jksPassword);

			// rechercher une clé qui correspond au globalId et qui n'est pas périmée (< date initiale + duree max)
			LocalDateTime currentDate = LocalDateTime.now();
			String aliasFound = searchMatchingAlias(mediaId, ks.aliases(), currentDate, keyDuration);
			if (StringUtils.isEmpty(aliasFound)) {
				// ajout d'une nouvelle clé
				String newAlias = generateAlias(mediaId, currentDate);
				keystoreHelper.addNewKeyPairToKeyStore(ks, jksFilename, jksPassword, newAlias, currentDate);
				aliasFound = newAlias;
			}
			return getPublicKey(ks, aliasFound);
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
			throw new AppServiceException(
					String.format("Erreur lors de la récupération de la clé publique pour le globalId %s", mediaId), e);
		}

	}

	@Override
	public PrivateKey getPrivateEncryptionKey(UUID mediaId, LocalDateTime mediaUpdatedDate) throws AppServiceException {

		try {
			KeyStore ks = keystoreHelper.loadKeyStore(jksFilename, jksPassword);
			// rechercher une clé qui correspond au globalId et qui n'est pas périmée (< date initiale + duree max)
			// sinon clé par défaut
			String aliasFound = StringUtils.defaultIfEmpty(
					searchMatchingAlias(mediaId, ks.aliases(), mediaUpdatedDate, keyDuration), defaultKeyAlias);
			log.info("Utilisation de l'alias {} pour retourner la clé privée du mediaId {}", aliasFound, mediaId);
			return getPrivateKey(ks, aliasFound, jksPassword);
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException
				| UnrecoverableKeyException e) {
			throw new AppServiceException(
					String.format("Erreur lors de la récupération de la clé privée pour le globalId %s", mediaId), e);
		}

	}

	private String searchMatchingAlias(UUID mediaId, Enumeration<String> aliases, LocalDateTime date,
			Long keyDuration) {

		if (mediaId == null) {
			return defaultKeyAlias;
		}

		String uuidInAlias = getSimplifiedUuid(mediaId);
		if (date == null) {
			date = LocalDateTime.now();
		}

		List<String> matchingAliases = new ArrayList<>();
		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			if (org.apache.commons.lang3.StringUtils.startsWith(alias, uuidInAlias)) {
				matchingAliases.add(alias);
			}
		}
		if (CollectionUtils.isEmpty(matchingAliases)) {
			log.info("Aucun alias correspondant au globalId {} n'a été trouvé dans le keystore", mediaId);
			return null;
		}
		Collections.sort(matchingAliases);
		for (String alias : matchingAliases) {
			String dateInAlias = StringUtils.removeStart(alias, uuidInAlias);
			LocalDateTime aliasCreationDate = LocalDateTime.parse(dateInAlias, DATE_TIME_FORMATTER);
			if (date.isAfter(aliasCreationDate)
					&& date.isBefore(aliasCreationDate.plus(keyDuration, ChronoUnit.SECONDS))) {
				return alias;
			}
		}
		log.info("Aucun alias correspondant au globalId {} n'a été trouvé dans le keystore pour la date {}", mediaId,
				date);
		return null;
	}

	private DocumentContent loadKeyAsDocumentContent(Key key) throws ExternalServiceException {
		try {

			try (ByteArrayOutputStream out = new ByteArrayOutputStream();
					OutputStreamWriter writer = new OutputStreamWriter(out);
					JcaPEMWriter jcaWriter = new JcaPEMWriter(writer);) {
				jcaWriter.writeObject(key);
				jcaWriter.flush();

				InputStream is = new ByteArrayInputStream(out.toByteArray());
				Resource keyResource = new InputStreamResource(is);
				return resourceHelper.convertToDocumentContent(keyResource);
			}
		} catch (IOException e) {
			throw new ExternalServiceException(
					"Exception lors de la création d'un DocumentContent correspondant à une clé privée ou publique", e);
		}
	}

	private String generateAlias(UUID mediaUuid, LocalDateTime currentDate) {
		// les alias générés sont au format <uuid sans les tirets><date au format yyyyMMddHHmmss>
		return getSimplifiedUuid(mediaUuid).concat(DATE_TIME_FORMATTER.format(currentDate));
	}

	private String getSimplifiedUuid(UUID mediaUuid) {
		return mediaUuid.toString().replace("-", Strings.EMPTY);
	}

	private PrivateKey getPrivateKey(final KeyStore keystore, final String alias, final String keyPassword)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey) keystore.getKey(alias, keyPassword.toCharArray());
	}

	private PublicKey getPublicKey(final KeyStore keystore, final String alias) throws KeyStoreException {
		final Certificate cert = keystore.getCertificate(alias);
		return cert.getPublicKey();
	}

}
