package org.rudi.microservice.acl.facade.config.security;

import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;

import org.apache.commons.lang3.ArrayUtils;
import org.rudi.common.facade.config.filter.JwtRequestFilter;
import org.rudi.common.facade.config.filter.OAuth2RequestFilter;
import org.rudi.common.facade.config.filter.PreAuthenticationFilter;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.microservice.acl.facade.config.security.anonymous.AnonymousAuthenticationProcessingFilterConfigurer;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationEntryPoint;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationLoginFailureHandler;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationLoginSuccessHandler;
import org.rudi.microservice.acl.facade.config.security.jwt.JwtAuthenticationProcessingFilterConfigurer;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${module.oauth2.check-token-uri}")
	private String checkTokenUri;

	@Value("${application.role.administrateur.code}")
	private String administrateurRoleCode;

	@Value("${rudi.acl.security.authentication.disabled:false}")
	private boolean disableAuthentification = false;

	@Value("${rudi.acl.security.pre-authentication.disabled:true}")
	private boolean disablePreAuthentification = true;

	@Value("${security.anonymous.login:anonymous}")
	private String loginAnonymous;

	@Value("${security.jwt.parameter.login:login}")
	private String loginParameter;

	@Value("${security.jwt.parameter.password:password}")
	private String passwordParameter;

	@Autowired
	@Qualifier("clientPasswordEncoder")
	private PasswordEncoder passwordEncoder;

	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private final JwtAuthenticationProvider userAuthenticationProvider;

	private final JwtAuthenticationLoginSuccessHandler loginSuccessHandler;

	private final JwtAuthenticationLoginFailureHandler loginFailureHandler;

	private final UtilContextHelper utilContextHelper;

	private final RestTemplate oAuth2RestTemplate;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		if (!disableAuthentification) {
			http.cors().and().csrf().disable()
					// starts authorizing configurations
					.authorizeRequests().antMatchers(SecurityConstants.SB_PERMIT_ALL_URL).permitAll()
					// autorisatio des actuators aux seuls role admin
					.antMatchers(SecurityConstants.ACTUATOR_URL).access("hasRole('" + administrateurRoleCode + "')")
					// authenticate all remaining URLS
					.anyRequest().fullyAuthenticated().and().authorizeRequests().and().exceptionHandling()
					.authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().logout()
					// et puis
					.and().anonymous(anonymous -> {
						anonymous.authorities(List.of(new SimpleGrantedAuthority("USER")));
					})
					// installation des filtres
					.apply(JwtAuthenticationProcessingFilterConfigurer.jwtAuthenticationProcessingConfigurer()
							.loginFailureHandler(loginFailureHandler).loginSuccessHandler(loginSuccessHandler)
							.userAuthenticationProvider(userAuthenticationProvider).loginParameter(loginParameter)
							.passwordParameter(passwordParameter))
					.and()
					.apply(AnonymousAuthenticationProcessingFilterConfigurer
							.anonymousAuthenticationProcessingConfigurer().loginFailureHandler(loginFailureHandler)
							.loginSuccessHandler(loginSuccessHandler).loginAnonymous(loginAnonymous))
					.and().addFilterBefore(createOAuth2Filter(), UsernamePasswordAuthenticationFilter.class)
					.addFilterBefore(createJwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
			if (!disablePreAuthentification) {
				http.addFilterAfter(createPreAuthenticationFilter(), BasicAuthenticationFilter.class);
			}
		} else {
			http.cors().and().csrf().disable().authorizeRequests().anyRequest().permitAll();
		}
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsServiceBean())
				.passwordEncoder(passwordEncoder).and().authenticationProvider(userAuthenticationProvider).build();
	}

//	@Override
//	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
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
	protected GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}

	@Bean(BeanIds.USER_DETAILS_SERVICE)
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return new UserDetailServiceImpl();
	}

	@Bean
	public JwtRequestFilter createJwtRequestFilter() {
		return new JwtRequestFilter(
				ArrayUtils.addAll(SecurityConstants.SB_PERMIT_ALL_URL, SecurityConstants.AUTHENTICATION_PERMIT_URL),
				SecurityConstants.LOGOUT_URL, utilContextHelper, oAuth2RestTemplate);
	}

	private Filter createOAuth2Filter() {
		return new OAuth2RequestFilter(SecurityConstants.SB_PERMIT_ALL_URL, checkTokenUri, utilContextHelper,
				oAuth2RestTemplate);
	}

	protected Filter createPreAuthenticationFilter() {
		return new PreAuthenticationFilter();
	}
}
