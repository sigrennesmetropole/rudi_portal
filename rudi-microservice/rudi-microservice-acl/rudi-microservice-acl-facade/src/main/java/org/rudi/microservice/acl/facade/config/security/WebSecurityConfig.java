package org.rudi.microservice.acl.facade.config.security;

import java.util.Arrays;

import javax.servlet.Filter;

import org.apache.commons.lang3.ArrayUtils;
import org.rudi.common.facade.config.filter.JwtRequestFilter;
import org.rudi.common.facade.config.filter.OAuth2RequestFilter;
import org.rudi.common.facade.config.filter.PreAuthenticationFilter;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.microservice.acl.facade.config.security.anonymous.AnonymousAuthenticationLoginSuccessHandler;
import org.rudi.microservice.acl.facade.config.security.anonymous.AnonymousAuthenticationProcessingFilter;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationEntryPoint;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationLoginFailureHandler;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationLoginSuccessHandler;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationProcessingFilter;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String ACTUATOR_URL = "/actuator/**";

	private static final String AUTHENTICATE_URL = "/authenticate";

	private static final String LOGOUT_URL = "/acl/v1/account/logout";

	private static final String CHECK_CREDENTIAL_URL = "/check_credential";

	private static final String[] AUTHENTICATION_PERMIT_URL = { AUTHENTICATE_URL, CHECK_CREDENTIAL_URL };

	// En autorisant une URL ici, il faut l'autoriser dans le WebSecuConfig de la gateway pour les appels front
	private static final String[] SB_PERMIT_ALL_URL = {
			// URL public
			"/acl/v1/application-information", "/acl/v1/healthCheck", "/oauth/*token*", "/oauth/logout", "oauth/jwks",
			AUTHENTICATE_URL,
			// swagger ui / openapi
			"/acl/v3/api-docs/**", "/acl/swagger-ui/**", "/acl/swagger-ui.html", "/acl/swagger-resources/**",
			"/configuration/ui", "/configuration/security", "/webjars/**", "/error", "/acl/v1/kaptcha" };

	@Value("${module.oauth2.check-token-uri}")
	private String checkTokenUri;

	@Value("${application.role.administrateur.code}")
	private String administrateurRoleCode;

	@Value("${rudi.acl.security.authentication.disabled:false}")
	private boolean disableAuthentification = false;

	@Autowired
	@Qualifier("clientPasswordEncoder")
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtAuthenticationProvider userAuthenticationProvider;

	@Autowired
	private JwtAuthenticationLoginSuccessHandler loginSuccessHandler;

	@Autowired
	private JwtAuthenticationLoginFailureHandler loginFailureHandler;

	@Autowired
	private AnonymousAuthenticationLoginSuccessHandler anonymousSuccessHandler;

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		if (!disableAuthentification) {
			http.cors().and().csrf().disable()
					// starts authorizing configurations
					.authorizeRequests().antMatchers(SB_PERMIT_ALL_URL).permitAll()
					// autorisatio des actuators aux seuls role admin
					.antMatchers(ACTUATOR_URL).access("hasRole('" + administrateurRoleCode + "')")
					// authenticate all remaining URLS
					.anyRequest().fullyAuthenticated().and().authorizeRequests().and().exceptionHandling()
					.authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().logout()
					// installation des filtres
					.and().addFilterBefore(createOAuth2Filter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(createJwtProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(createJwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterAfter(createPreAuthenticationFilter(), BasicAuthenticationFilter.class)
					.addFilterBefore(createAnonymousProcessingFilter(), AnonymousAuthenticationFilter.class);
		} else {
			http.cors().and().csrf().disable().authorizeRequests().anyRequest().permitAll();
		}
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder);
		auth.authenticationProvider(userAuthenticationProvider);
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

	@Override
	@Bean(BeanIds.USER_DETAILS_SERVICE)
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return new UserDetailServiceImpl();
	}

	@Override
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public JwtRequestFilter createJwtRequestFilter() {
		return new JwtRequestFilter(ArrayUtils.addAll(SB_PERMIT_ALL_URL, AUTHENTICATION_PERMIT_URL), LOGOUT_URL,
				utilContextHelper);
	}

	@Bean
	public JwtAuthenticationProcessingFilter createJwtProcessingFilter() throws Exception {
		return new JwtAuthenticationProcessingFilter(AUTHENTICATE_URL, CHECK_CREDENTIAL_URL, userAuthenticationProvider,
				loginSuccessHandler, loginFailureHandler, authenticationManager());
	}

	@Bean
	public AnonymousAuthenticationProcessingFilter createAnonymousProcessingFilter() throws Exception {
		return new AnonymousAuthenticationProcessingFilter(anonymousSuccessHandler, loginFailureHandler,
				authenticationManager());
	}

	private Filter createOAuth2Filter() {
		return new OAuth2RequestFilter(SB_PERMIT_ALL_URL, checkTokenUri, utilContextHelper);
	}

	protected Filter createPreAuthenticationFilter() {
		return new PreAuthenticationFilter();
	}

}
