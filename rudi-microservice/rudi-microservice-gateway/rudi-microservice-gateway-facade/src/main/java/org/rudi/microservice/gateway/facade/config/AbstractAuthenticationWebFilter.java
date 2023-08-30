/**
 * RUDI Portail
 */
package org.rudi.microservice.gateway.facade.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * @author FNI18300
 */
public abstract class AbstractAuthenticationWebFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationWebFilter.class);

	private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository
			.getInstance();

	private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();

	// Liste des URL à exclure
	private Collection<String> excludeUrlPatterns;

	private ServerWebExchangeMatcher requiresAuthenticationMatcher;

	protected AbstractAuthenticationWebFilter(final String[] excludeUrlPatterns) {
		this.excludeUrlPatterns = Arrays.asList(excludeUrlPatterns);
		if (CollectionUtils.isNotEmpty(this.excludeUrlPatterns)) {
			List<ServerWebExchangeMatcher> pathPatternMatchers = new ArrayList<>();
			for (String excludeUrlPattern : excludeUrlPatterns) {
				pathPatternMatchers.add(new PathPatternParserServerWebExchangeMatcher(excludeUrlPattern));
			}
			requiresAuthenticationMatcher = new NegatedServerWebExchangeMatcher(
					new OrServerWebExchangeMatcher(pathPatternMatchers));
		}
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return requiresAuthenticationMatcher.matches(exchange).filter(matchResult -> matchResult.isMatch())
				.flatMap(matchResult -> authenticationConvert(exchange))
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.flatMap(token -> onAuthenticationSuccess(token, new WebFilterExchange(exchange, chain)));
	}

	protected Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		SecurityContextImpl securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(authentication);
		return securityContextRepository.save(exchange, securityContext)
				.then(authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication))
				.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
	}

	protected Mono<Authentication> authenticationConvert(ServerWebExchange exchange) {
		// Request token header
		String requestAuthentTokenHeader = exchange.getRequest().getHeaders()
				.getFirst(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_AUTHENT_KEY);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header: {}", requestAuthentTokenHeader);
		}
		if (requestAuthentTokenHeader == null) {
			return Mono.empty();
		} else if (!requestAuthentTokenHeader.startsWith(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX)) {
			LOGGER.error("Le token ne commence pas avec la chaine Bearer");
			return Mono.empty();
		} else {
			final String token = requestAuthentTokenHeader
					.substring(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX.length());

			return handleToken(token);
		}
	}

	protected Collection<SimpleGrantedAuthority> collectAuthorities(AuthenticatedUser user) {
		List<SimpleGrantedAuthority> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(user.getRoles())) {
			for (String role : user.getRoles()) {
				result.add(new SimpleGrantedAuthority(role));
			}
		}
		return result;
	}

	protected abstract Mono<Authentication> handleToken(final String token);

}
