package org.rudi.facet.crypto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CryptoUtil {

	private static final String CERTIFICATE_PROVIDER_NAME = "BC";

	private CryptoUtil() {
	}

	public static KeyPair generateKeyPair(RudiAlgorithmSpec spec) throws NoSuchAlgorithmException {
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

	public static Certificate[] generateCertificateChain(KeyPair keyPair, LocalDateTime currentDate,
			String certificateAlgorithm, String certificateIssuerName, long certificateDuration)
			throws OperatorCreationException, CertificateException {
		// générer certificat auto signé pour chaque nouvelle clé
		return new Certificate[] { createTrustAnchor(keyPair, certificateAlgorithm, currentDate, certificateIssuerName,
				certificateDuration) };
	}

	public static X509Certificate createTrustAnchor(KeyPair keyPair, String sigAlg, LocalDateTime currentDate,
			String certificateIssuerName, long certificateDuration)
			throws OperatorCreationException, CertificateException {
		enableBouncyCastleProvider();
		X500Name name = new X500Name(certificateIssuerName);

		X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(name, generateSerialNumber(currentDate),
				convertLocalDateTimeToDate(currentDate),
				convertLocalDateTimeToDate(currentDate.plus(certificateDuration, ChronoUnit.SECONDS)), name,
				keyPair.getPublic());
		ContentSigner signer = new JcaContentSignerBuilder(sigAlg).setProvider(CERTIFICATE_PROVIDER_NAME)
				.build(keyPair.getPrivate());
		JcaX509CertificateConverter converter = new JcaX509CertificateConverter()
				.setProvider(CERTIFICATE_PROVIDER_NAME);
		return converter.getCertificate(certBldr.build(signer));
	}

	private static BigInteger generateSerialNumber(LocalDateTime currentDate) {
		return BigInteger.valueOf(currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	private static Date convertLocalDateTimeToDate(LocalDateTime currentDate) {
		return Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static void enableBouncyCastleProvider() {
		Provider provider = Security.getProvider(CERTIFICATE_PROVIDER_NAME);

		if (provider == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

	}
}
