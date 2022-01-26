package org.rudi.microservice.providers.facade.config.security;

import java.util.Arrays;

import javax.servlet.Filter;

import org.rudi.common.facade.config.filter.JwtRequestFilter;
import org.rudi.common.facade.config.filter.OAuth2RequestFilter;
import org.rudi.common.facade.config.filter.PreAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String ACTUATOR_URL = "/actuator/**";

	private static final String[] SB_PERMIT_ALL_URL = {
			// URL public
			"/providers/v1/application-information", "/providers/v1/healthCheck",
			// swagger ui / openapi
			"/providers/v3/api-docs/**", "/providers/swagger-ui/**", "/providers/swagger-ui.html",
			"/providers/swagger-resources/**", "/configuration/ui", "/configuration/security", "/webjars/**" };

	@Value("${application.role.administrateur.code}")
	private String administrateurRoleCode;

	@Value("${module.oauth2.check-token-uri}")
	private String checkTokenUri;

	@Value("${rudi.providers.security.authentication.disabled:false}")
	private boolean disableAuthentification = false;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		if (!disableAuthentification) {
			http.cors().and().csrf().disable()
					// starts authorizing configurations
					.authorizeRequests().antMatchers(SB_PERMIT_ALL_URL).permitAll()
					// autorisatio des actuators aux seuls role admin
					.antMatchers(ACTUATOR_URL).access("hasRole('" + administrateurRoleCode + "')")
					// authenticate all remaining URLS
					.anyRequest().fullyAuthenticated().and().authorizeRequests().and().exceptionHandling().and()
					// installation du filtre de type header
					.addFilterBefore(createOAuth2Filter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(createJwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterAfter(createPreAuthenticationFilter(), BasicAuthenticationFilter.class)
					// configuring the session on the server
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
		} else {
			http.cors().and().csrf().disable().authorizeRequests().anyRequest().permitAll();
		}
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
		configuration.setAllowedOrigins(Arrays.asList("*"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}

	private Filter createPreAuthenticationFilter() {
		return new PreAuthenticationFilter();
	}

	private Filter createOAuth2Filter() {
		return new OAuth2RequestFilter(SB_PERMIT_ALL_URL, checkTokenUri);
	}

	@Bean
	public JwtRequestFilter createJwtRequestFilter() {
		return new JwtRequestFilter(SB_PERMIT_ALL_URL);
	}
}
