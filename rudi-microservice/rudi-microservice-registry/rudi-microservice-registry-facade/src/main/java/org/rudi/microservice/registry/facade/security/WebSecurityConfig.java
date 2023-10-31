/**
 * 
 */
package org.rudi.microservice.registry.facade.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author FNI18300
 *
 */
@EnableWebSecurity
class WebSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().ignoringAntMatchers("/eureka/**");

		return http.build();
	}
}