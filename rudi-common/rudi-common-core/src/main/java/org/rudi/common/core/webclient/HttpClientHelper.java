/**
 * RUDI
 */
package org.rudi.common.core.webclient;

import javax.net.ssl.SSLException;

import org.springframework.stereotype.Component;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProtocols;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;

/**
 * Ce helper permet de créer des HttpClient reactor en gérant les aspects ssl et compression
 * 
 * @author FNI18300
 *
 */
@Component
public class HttpClientHelper {

	/**
	 * Création d'un client Reactor
	 * 
	 * @param trustAllCerts
	 * @param compress
	 * @return
	 * @throws SSLException
	 */
	public HttpClient createReactorHttpClient(boolean trustAllCerts, boolean compress, boolean wiretap)
			throws SSLException {
		HttpClient client = null;
		if (trustAllCerts) {
			SslContext sslContext = SslContextBuilder.forClient().protocols(SslProtocols.TLS_v1_2)
					.trustManager(InsecureTrustManagerFactory.INSTANCE).build();

			client = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
					.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
		} else {
			client = HttpClient.create();
		}
		if (compress) {
			client.compress(true);
		}
		if (wiretap) {
			client.wiretap(wiretap);
		}
		return client;
	}
}
