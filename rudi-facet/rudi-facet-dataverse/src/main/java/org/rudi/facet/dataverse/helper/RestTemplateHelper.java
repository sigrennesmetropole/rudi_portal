package org.rudi.facet.dataverse.helper;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.api.exceptions.DataverseResponseHandler;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Component
public class RestTemplateHelper {

    /**
     * @return un RestTemplate correctement initialisé
     * @throws DataverseAPIException Erreur lors de l'initialisation du restTemplate spring
     */
    public RestTemplate buildRestTemplate() throws DataverseAPIException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            restTemplate.setErrorHandler(new DataverseResponseHandler());
            return restTemplate;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new DataverseAPIException("Failed to load ssl", e);
        }
    }
}
