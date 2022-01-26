package org.rudi.common.facade.config.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Classe utilitaire des gestion de token JWT
 */
@Component
@Slf4j
public class JwtTokenUtil extends CommonJwtTokenUtil<Claims> implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	@Override
	protected Function<Claims, String> getSubjectFunction() {
		return Claims::getSubject;
	}

	/**
	 * récupération une propriété d'un token
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getTokenProperty(final String token, final String propertyName) {
		return (T) getAllClaimsFromToken(token).get(propertyName);
	}

	@Override
	protected Function<Claims, Date> getExpirationFunction() {
		return Claims::getExpiration;
	}

	@Override
	protected void handleTokenWithTryCatch(BiConsumer<String, JwtTokenData> tokenConsumer, String tokenJwt, JwtTokenData token) {
		try {
			tokenConsumer.accept(tokenJwt, token);
		} catch (final IllegalArgumentException ex) {
			log.error("impossible de récupérer le token JWT", ex);
			token.setHasError(true);
		} catch (final ExpiredJwtException ex) {
			log.error("Token JWT expiré", ex);
			token.setExpired(true);
		} catch (final MalformedJwtException ex) {
			log.error("Erreur de formatage du token JWT", ex);
			token.setHasError(true);
		}
	}

	/**
	 * récupération de toutes les propriétes d'un token
	 */
	@Override
	protected Claims getAllClaimsFromToken(final String token) {
		return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();
	}

	/**
	 * Détermine si le token est expiré
	 */
	protected boolean isTokenExpired(final String token) {
		try {
			// la récupération de la date retourne une exection si le token est déjà expiré
			getExpirationDateFromToken(token);
			return false;
		} catch (final ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * Generation d'un token
	 *
	 * @param claims   Issuer, Expiration, Subject, and the ID
	 * @param subject  sujet du token
	 * @param validity validity in seconde
	 * @return le token généré
	 */
	@Override
	protected String doGenerateToken(final Map<String, Object> claims, final String subject, final int validity) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + validity * 1000L))
				.signWith(SignatureAlgorithm.HS512, getSecretKey()).compact();
	}

}