/**
 * RUDI Portail
 */
package org.rudi.wso2.mediation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.annotation.Nullable;
import javax.net.ssl.SSLHandshakeException;

import org.apache.axis2.Constants;
import org.apache.synapse.MessageContext;
import org.osgi.framework.FrameworkUtil;
import org.rudi.facet.crypto.KeyGeneratorFromPem;
import org.rudi.facet.crypto.MediaCipherOperator;
import org.rudi.facet.crypto.RudiAlgorithmSpec;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;

import static org.rudi.wso2.mediation.PublicKeyURLComparator.PUBLIC_KEY_URL_PROPERTY;

/**
 * @author FNI18300
 */
public class EncryptedMediaHandler extends AbstractRudiHandler {

	protected static final String ENCRYPTED_PROPERTY = "encrypted";
	protected static final String PRIVATE_KEY_PATH_PROPERTY = "privateKeyURL";
	private static final int READLIMIT = 128 * 1024;
	private static final String MIME_TYPE_CRYPT_SUFFIXE = "+crypt";
	private static final String MIME_TYPE_PROPERTY = "mime_type";
	/**
	 * Chemin (relatif au classpath) vers la clé privée par défaut
	 */
	private static final String DEFAULT_PRIVATE_KEY_PATH = "encryption_key.key";


	private final MediaCipherOperator mediaCipherOperator = new MediaCipherOperator(RudiAlgorithmSpec.DEFAULT);
	private final PublicKeyComparator publicKeyContentComparator = new PublicKeyContentComparator();
	private final PublicKeyComparator publicKeyURLComparator = new PublicKeyURLComparator();
	private final KeyGeneratorFromPem keyGeneratorFromPem = new KeyGeneratorFromPem();
	private PrivateKey privateKey;
	private PublicKey publicKey;

	@Override
	protected boolean engageResponse(MessageContext messageContext) throws APIManagementException {
		final var engagedApi = getEngagedApi(messageContext);
		return isEncryptedWithPortalPublicKey(engagedApi);
	}

	private boolean isEncryptedWithPortalPublicKey(API engagedApi) {
		if (!isEncrypted(engagedApi)) {
			return false;
		}
		return publicKeyContentComparator.usesSamePublicKey(this, engagedApi) ||
				publicKeyURLComparator.usesSamePublicKey(this, engagedApi);
	}

	private boolean isEncrypted(API engagedApi) {
		final var encryptedPropertyValue = AdditionalPropertiesUtil.getAdditionalProperty(ENCRYPTED_PROPERTY, engagedApi);
		return Boolean.TRUE.toString().equals(encryptedPropertyValue);
	}

	@Override
	protected void doHandleResponse(MessageContext messageContext) throws Exception {
		decrypt(messageContext);
	}

	private void decrypt(MessageContext messageContext) throws Exception {
		replaceBody(messageContext, this::replaceBodyInputStream, this::computeNewContentType);
	}

	private InputStream replaceBodyInputStream(InputStream originalBody) throws IOException, GeneralSecurityException {
		final var file = File.createTempFile("EncryptedMediaHandler", ".decrypt");
		try (final var fos = new FileOutputStream(file)) {
			decrypt(originalBody, fos);
		}
		final var fis = new FileInputStream(file);
		final var bufferedInputStream = new BufferedInputStream(fis);
		bufferedInputStream.mark(READLIMIT);

		return bufferedInputStream;
	}

	@Nullable
	private String computeNewContentType(MessageContext messageContext) {
		final API engagedApi;
		try {
			engagedApi = getEngagedApi(messageContext);
		} catch (APIManagementException e) {
			throw new RuntimeException("Cannot get engaged API", e);
		}

		final var mimeType = AdditionalPropertiesUtil.getAdditionalProperty(MIME_TYPE_PROPERTY, engagedApi);
		if (mimeType != null) {
			return mimeType;
		} else {
			final var axis2MC = getAxis2MessageContext(messageContext);
			final var contentType = axis2MC.getProperty(Constants.Configuration.CONTENT_TYPE);
			if (contentType != null && contentType.toString().endsWith(MIME_TYPE_CRYPT_SUFFIXE)) {
				final var contentTypeValue = contentType.toString();
				return contentTypeValue.substring(0, contentTypeValue.length() - MIME_TYPE_CRYPT_SUFFIXE.length());
			}
		}
		return null;
	}

	/**
	 * Effectue le déchiffrement
	 */
	protected void decrypt(InputStream encryptedStream, OutputStream decryptedStream) throws IOException, GeneralSecurityException {
		mediaCipherOperator.decrypt(encryptedStream, getPrivateKey(), decryptedStream);
	}

	public URL getPrivateKeyURL() throws MalformedURLException {
		final var privateKeyPropertyValue = getProperties().get(PRIVATE_KEY_PATH_PROPERTY);
		if (privateKeyPropertyValue != null) {
			return new URL((String) privateKeyPropertyValue);
		} else {
			return getDefaultPrivateKeyURL();
		}
	}

	private URL getDefaultPrivateKeyURL() {
		// Source : https://stackoverflow.com/a/6528973/1655155
		return FrameworkUtil.getBundle(getClass()).getResource(DEFAULT_PRIVATE_KEY_PATH);
	}

	@SuppressWarnings({
			"unchecked", // getProperties() n'est pas modifiable
			"unused" // utilisé par le fichier XML de l'API généré par le velocity_template.xml
	})
	public void setPrivateKeyURL(String privatKeyPath) {
		getProperties().put(PRIVATE_KEY_PATH_PROPERTY, privatKeyPath);
	}

	@SuppressWarnings({
			"java:S100", // nom de la propriété généré automatiquement par WSO2
			"unused" // généré automatiquement par WSO2 lorsqu'on ajoute la propriété "privateKeyURL" dans le velocity_template.xml
	})
	public void setPrivate_key_url(String privatKeyPath) {
		setPrivateKeyURL(privatKeyPath);
	}

	private PrivateKey getPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (privateKey == null) {
			final var keyPairAlgorithm = RudiAlgorithmSpec.DEFAULT.firstBlockSpec.keyPairAlgorithm;
			final var keyInputStream = getPrivateKeyURL().openConnection().getInputStream();
			privateKey = keyGeneratorFromPem.generatePrivateKey(keyPairAlgorithm, keyInputStream);
		}
		return privateKey;
	}

	public URL getPublicKeyURL() throws MalformedURLException {
		return new URL((String) getProperties().get(PUBLIC_KEY_URL_PROPERTY));
	}

	@SuppressWarnings({
			"unchecked", // getProperties() n'est pas modifiable
			"unused" // utilisé par le fichier XML de l'API généré par le velocity_template.xml
	})
	public void setPublicKeyURL(String publicKeyURL) {
		getProperties().put(PUBLIC_KEY_URL_PROPERTY, publicKeyURL);
	}


	@SuppressWarnings({
			"java:S100", // nom de la propriété généré automatiquement par WSO2
			"unused" // généré automatiquement par WSO2 lorsqu'on ajoute la propriété "publicKeyURL" dans le velocity_template.xml
	})
	public void setPublic_key_url(String publicKeyURL) {
		setPublicKeyURL(publicKeyURL);
	}

	@Override
	protected String getErrorMessage(Exception e) {
		return "Failed to decrypt stream";
	}

	/**
	 * @return le contenu de la clé publique au format PEM sans la première ni la dernière ligne et sans retour à la ligne
	 */
	String getPublicKeyContent() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		try {
			final var encodedPublicKeyContent = getPublicKey().getEncoded();
			return Base64.getEncoder().encodeToString(encodedPublicKeyContent);
		} catch (SSLHandshakeException e) {
			// TODO en attendant la génération de certificats non auto-signés pour la dev/qualif/r7, on renvoie le contenu de la clé publique en dur dans le code => cf RUDI-2527
			return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvS3nTZOj01kq1V6wKpMenROzRDb8V/uBLS6Ey90sWdhbRhv8R1QIYSRs3YjMZ2HBdMOQzuMQvyUF5lJv0KDNjYIf74n3+rDNxICkTlm7cwggtks/JOdNw1o/fGbU83tlAnSr4QbLCCqThzslchiKmGVH5pCiV/aX2bl81iNKkGDiFpmyT/au8+OtZOZe910sDnBsyPHH+wCkh/bb4E+tkKHUGLKpi4T8cm9wrNXFySxP532zuPsJ5CsaDWyu3jXivfYvC5Q520B72F+eK6nIdozONJLMtd9lFegxuRtU3fsYMAZww77hiCsH5mgfLo+iwXicdTCWogkGl8n9ODYhUwIDAQAB";
		}
	}

	private PublicKey getPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		if (publicKey == null) {
			final var keyPairAlgorithm = RudiAlgorithmSpec.DEFAULT.firstBlockSpec.keyPairAlgorithm;
			final var keyInputStream = getPublicKeyURL().openConnection().getInputStream();
			publicKey = keyGeneratorFromPem.generatePublicKey(keyPairAlgorithm, keyInputStream);
		}
		return publicKey;
	}
}
