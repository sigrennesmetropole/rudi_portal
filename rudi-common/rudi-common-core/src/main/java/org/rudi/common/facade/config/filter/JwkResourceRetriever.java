/**
 * RUDI
 */
package org.rudi.common.facade.config.filter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * Cette extension de nimbus permet de gérer des connections à des API-M utilisant des certificats auto-signés Utilisé notamment pour les tests
 * 
 * @author FNI18300
 *
 */
@Slf4j
public class JwkResourceRetriever extends DefaultResourceRetriever {

	private static final String SSL_PROTOCOL = "TLSv1.2";

	private static boolean initialized = false;

	private boolean hostVerifier = false;

	/**
	 * Constructeur
	 */
	public JwkResourceRetriever() {
		super();
	}

	/**
	 * Constructeur
	 * 
	 * @param connectTimeout
	 * @param readTimeout
	 * @param sizeLimit
	 * @param disconnectAfterUse
	 * @param hostVerifier
	 */
	public JwkResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit, boolean disconnectAfterUse,
			boolean hostVerifier) {
		super(connectTimeout, readTimeout, sizeLimit, disconnectAfterUse);
		this.hostVerifier = hostVerifier;
	}

	@Override
	protected HttpURLConnection openConnection(URL url) throws IOException {
		initHostVerifier();

		HttpURLConnection connection = super.openConnection(url);
		log.debug("JwkResourceRetriever.openConnection:{}", connection.getURL());

		return connection;
	}

	@Override
	public Resource retrieveResource(URL url) throws IOException {
		log.debug("JwkResourceRetriever.retrieveResource:{}", url);
		return super.retrieveResource(url);
	}

	@SuppressWarnings("java:S5527")
	private void initHostVerifier() {
		if (!initialized && !hostVerifier) {
			try {
				SSLContext sc = SSLContext.getInstance(SSL_PROTOCOL);
				sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

				// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = (hostname, session) -> true;
				// Install the all-trusting host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

				log.debug("JwkResourceRetriever.initHostVerifier {} done", allHostsValid);

			} catch (Exception e) {
				log.warn("Failed to add host verifier", e);
			}
		}
	}

	@SuppressWarnings({ "java:S4830", "java:S1168" })
	private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
			// Surcharge de checkClientTrusted
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
			// Surcharge de checkServerTrusted
		}

	} };

}
