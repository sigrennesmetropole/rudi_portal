/**
 * RUDI Portail
 */
package org.rudi.microservice.gateway.facade.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author FNI18300
 *
 */
@Component
public class RestTemplateConfiguration {

	@Value("${trust.trust-all-certs:false}")
	private boolean trustAllCerts;

	@Value("${trust.store:}")
	private Resource trustStore;

	@Value("${trust.store.password:}")
	private String trustStorePassword;

	@Bean
	public RestTemplate internalRestTemplate() throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, IOException {
		RestTemplate result = null;
		CloseableHttpClient httpClient = null;
		if (trustAllCerts) {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
			httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		} else if (trustStore != null) {
			SSLContext sslContext = new SSLContextBuilder()
					.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
			SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext);

			httpClient = HttpClients.custom().setSSLSocketFactory(sslConFactory).build();
		}
		if (httpClient != null) {
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			result = new RestTemplate(requestFactory);
		} else {
			result = new RestTemplate();
		}
		return result;
	}
}
