/**
 * RUDI Portail
 */
package org.rudi.microservice.gateway.facade.config;

import java.util.Arrays;

import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 */
@Component
@RequiredArgsConstructor
public class WebSecurityConfig {

	private static final String[] SB_PERMIT_ALL_URL = {
			// URLs que la gateway laisse passer et les traitements de sécurité sont gérés plus bas dans les µservices
			"/authenticate", "/authenticate/**", "/refresh_token", "/oauth/**", "/acl/v1/kaptcha",
			"/konsult/v1/cms/**" };

	@Value("${application.role.administrateur.code}")
	private String administrateurRoleCode;

	@Value("${module.oauth2.check-token-uri}")
	private String checkTokenUri;

	@Value("${rudi.gateway.security.authentication.disabled:false}")
	private boolean disableAuthentification = false;

	private final AbstractJwtTokenUtil jwtTokenUtil;

	private final RestTemplate internalRestTemplate;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
		http.authorizeExchange((exchanges) -> {
			exchanges.matchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll();
			if (!disableAuthentification) {
				exchanges.pathMatchers(SB_PERMIT_ALL_URL).permitAll();
				exchanges.anyExchange().authenticated().and()
						.addFilterBefore(createOAuth2Filter(), SecurityWebFiltersOrder.AUTHENTICATION)
						.addFilterBefore(createJwtRequestFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
			} else {
				exchanges.anyExchange().permitAll();
			}
		});
		http.httpBasic(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.csrf().disable();
		http.exceptionHandling().authenticationEntryPoint(new HttpBearerServerAuthenticationEntryPoint());
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("Authorization");
		configuration.addExposedHeader("X-TOKEN");
		configuration.setAllowCredentials(true);

		// Url autorisées
		// 4200 pour les développement | 8080 pour le déploiement
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}

	private WebFilter createOAuth2Filter() {
		return new OAuth2WebFilter(SB_PERMIT_ALL_URL, checkTokenUri, internalRestTemplate);
	}

	public WebFilter createJwtRequestFilter() {
		return new JwtWebFilter(SB_PERMIT_ALL_URL, jwtTokenUtil);
	}

}
