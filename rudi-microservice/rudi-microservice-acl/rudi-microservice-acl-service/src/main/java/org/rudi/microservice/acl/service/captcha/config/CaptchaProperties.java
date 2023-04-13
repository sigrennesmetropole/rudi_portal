package org.rudi.microservice.acl.service.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.captcha")
@Getter
@Setter
public class CaptchaProperties {

	public static final String REGISTRATION_ID = "captchetat";
	public static final String PISTE_ENDPOINT = "/api/simple-captcha-endpoint";
	public static final String RUDI_ENDPOINT = "/acl/v1/kaptcha";
	/**
	 * URL d'obtention d'un token pour accéder à l'API
	 */
	private String oauth2TokenUri = "https://oauth.piste.gouv.fr/api/oauth/token";

	/**
	 * Url de base
	 */
	private String captchaBaseUrl = "https://api.piste.gouv.fr/piste/captcha";

	/**
	 * Chemin pour d'accès au captcha
	 */
	private String captchaEndpoint = "/simple-captcha-endpoint";

	/**
	 * Chemin de validation d'un captcha
	 */
	private String validateCaptchaEndpoint = "/valider-captcha";

	/**
	 * Infos de l'application ayant souscrite
	 */
	private String clientId;
	private String clientSecret;
	private String clientName;

	private String[] scopes = new String[]{ "WRITE" };
}
