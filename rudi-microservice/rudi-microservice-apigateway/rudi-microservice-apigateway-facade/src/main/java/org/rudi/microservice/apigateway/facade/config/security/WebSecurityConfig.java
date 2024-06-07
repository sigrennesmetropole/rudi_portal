package org.rudi.microservice.apigateway.facade.config.security;

import java.util.Arrays;

import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
//@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	public static final String REGISTRATION_ID = "rudi_module";

	private static final String ACTUATOR_URL = "/actuator/**";

	private static final String[] SB_PERMIT_ALL_URL = {
			// URL public
			"/apigateway/v1/application-information", "/apigateway/v1/healthCheck",
			// OAuth2
			"/oauth/**",
			// swagger ui / openapi
			"favicon.ico", "/apigateway/v3/api-docs/**", "/apigateway/swagger-ui/**", "/apigateway/swagger-ui.html",
			"/apigateway/swagger-resources/**", "/apigateway/webjars/**",
			// configuration ?
			"/configuration/ui", "/configuration/security" };

	@Value("${application.role.administrateur.code}")
	private String administrateurRoleCode;

	@Value("${module.oauth2.check-token-uri}")
	private String checkTokenUri;

	@Getter
	@Value("${module.oauth2.client-id}")
	private String clientId;

	@Getter
	@Value("${module.oauth2.client-secret}")
	private String clientSecret;

	@Getter
	@Value("${module.oauth2.provider-uri}")
	private String tokenUri;

	@Value("${module.oauth2.scope}")
	private String[] scopes;

	@Value("${rudi.apigateway.security.authentication.disabled:false}")
	private boolean disableAuthentification = false;

	private final RestTemplate internalRestTemplate;

	private final AbstractJwtTokenUtil jwtTokenUtil;

	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http.authorizeExchange(exchanges -> {
			exchanges.matchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll();
			if (!disableAuthentification) {
				exchanges.pathMatchers(SB_PERMIT_ALL_URL).permitAll();
				exchanges.pathMatchers(ACTUATOR_URL).authenticated();
				exchanges.anyExchange().authenticated().and()
						.addFilterBefore(createOAuth2Filter(), SecurityWebFiltersOrder.AUTHENTICATION)
						.addFilterBefore(createJwtRequestFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
			} else {
				exchanges.anyExchange().permitAll();
			}
		});
		http.httpBasic(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.cors().and().csrf().disable();
		http.exceptionHandling().authenticationEntryPoint(new HttpBearerServerAuthenticationEntryPoint());
		return http.build();
	}

	@Bean(name = "rudi_oauth2_repository")
	public ReactiveClientRegistrationRepository getRegistration() {
		ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
				.tokenUri(tokenUri).clientId(clientId).clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).scope(scopes).build();
		return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
	}

	@Bean(name = "rudi_oauth2_client_service")
	public ReactiveOAuth2AuthorizedClientService clientService(
			@Qualifier("rudi_oauth2_repository") ReactiveClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
	}

	@Bean(name = "rudi_oauth2_client_repository")
	public ServerOAuth2AuthorizedClientRepository clientRepository(
			@Qualifier("rudi_oauth2_client_service") ReactiveOAuth2AuthorizedClientService clientService) {
		return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(clientService);
	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		final var configuration = new CorsConfiguration();
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("Authorization");
		configuration.addExposedHeader("X-TOKEN");
		configuration.setAllowCredentials(true);

		// Url autorisées
		// 4200 pour les développement | 8080 pour le déploiement
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));

		final var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}

	public WebFilter createJwtRequestFilter() {
		return new JwtWebFilter(SB_PERMIT_ALL_URL, jwtTokenUtil);
	}

	private WebFilter createOAuth2Filter() {
		return new OAuth2WebFilter(SB_PERMIT_ALL_URL, checkTokenUri, internalRestTemplate);
	}

}
