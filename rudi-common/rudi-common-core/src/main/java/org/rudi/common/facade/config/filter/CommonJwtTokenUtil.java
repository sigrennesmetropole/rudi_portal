package org.rudi.common.facade.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.rudi.common.core.util.SecretKeyUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <C> Claims type
 */
@Slf4j
public abstract class CommonJwtTokenUtil<C extends Map<String, Object>> implements Serializable {

	public static final String HEADER_TOKEN_JWT_AUTHENT_KEY = "Authorization";
	public static final String HEADER_TOKEN_JWT_PREFIX = "Bearer ";
	protected static final String CONNECTED_USER = "connectedUser";

	@Value("${security.jwt.access.tokenKey:JwTRud1}")
	private String secret;

	private String secretKey;

	@Value("${security.jwt.validity:3600}")
	private int tokenValidity;

	@Value("${security.jwt.refresh.validity:3600}")
	private int refreshTokenValidity;

	private final transient Map<String, Tokens> refreshTokens = new HashMap<>();

	/**
	 * Retourne le nom d'utilisateur associé au token
	 */
	public String getSubjectFromToken(final String token) {
		return getClaimFromToken(token, getSubjectFunction());
	}

	protected abstract Function<C, String> getSubjectFunction();

	/**
	 * récupération une propriété d'un token
	 */
	@SuppressWarnings("unchecked")
	public <T> T getTokenProperty(final String token, final String propertyName) {
		return (T) getAllClaimsFromToken(token).get(propertyName);
	}

	/**
	 * Retourne la date d'expiration du token
	 */
	public Date getExpirationDateFromToken(final String token) {
		return getClaimFromToken(token, getExpirationFunction());
	}

	protected abstract Function<C, Date> getExpirationFunction();

	/**
	 * Genration du token JWT et du token de refresh
	 */
	public Tokens generateTokens(final String accountLogin, final Object connectedUser) {
		// Génération du token d'authentification et de refesh
		final Tokens tokens = new Tokens();
		tokens.setJwtToken(HEADER_TOKEN_JWT_PREFIX + generateJwtToken(accountLogin, connectedUser));
		tokens.setRefreshToken(HEADER_TOKEN_JWT_PREFIX + generateRefreshToken(accountLogin, connectedUser));
		refreshTokens.put(tokens.getRefreshToken(), tokens);
		return tokens;
	}

	/**
	 * Generation de nouveau token à partir du refresh token
	 */
	public Tokens generateNewJwtTokens(final String refreshToken) throws RefreshTokenExpiredException {
		// Récupération des données du token de refresh
		final JwtTokenData refreshJtd = validateToken(refreshToken);
		// Vérification si le token existe dans le référentiel de l'application (en bdd)
		final boolean refreshToKenExist = checkRefreshToken(refreshJtd.getToken());

		// Si le token de refresh existe et qu'il n'est pas expiré
		if (refreshToKenExist && !refreshJtd.isHasError() && !refreshJtd.isExpired()) {
			// Generation de nouveaux token (jwt et refresh)
			return generateTokens(refreshJtd.getSubject(), refreshJtd.getAccount());
		} else {
			// Exception car le tocken de refresh n'est pas valide
			throw new RefreshTokenExpiredException("Refresh token invalide");
		}
	}

	/**
	 * Récupération des données du token tout en le validant
	 *
	 * @param requestToken token
	 */
	public JwtTokenData validateToken(final String requestToken) {
		final JwtTokenData token = new JwtTokenData();

		// Contrôle du préfixe dans la requete
		if (requestToken == null || !requestToken.startsWith(HEADER_TOKEN_JWT_PREFIX)) {
			log.error("Le token ne commence pas avec la chaine Bearer");
			token.setHasError(true);
		} else {
			final String tokenJwt = requestToken.substring(HEADER_TOKEN_JWT_PREFIX.length());
			token.setToken(tokenJwt);

			handleTokenWithTryCatch(this::handleToken, tokenJwt, token);
		}

		return token;

	}

	protected abstract void handleTokenWithTryCatch(BiConsumer<String, JwtTokenData> tokenConsumer, String tokenJwt, JwtTokenData token);

	private void handleToken(String tokenJwt, JwtTokenData token) {
		token.setSubject(getSubjectFromToken(tokenJwt));
		handleAccount(token, tokenJwt);
		token.getProperties().putAll(getAllClaimsFromToken(tokenJwt));
	}

	private void handleAccount(JwtTokenData token, String tokenJwt) {
		token.setAccount(getTokenProperty(tokenJwt, CONNECTED_USER));
	}

	/**
	 * Generation d'un token pour un utilisateur
	 *
	 * @param accountUid id de l'iutilisateur
	 */
	private String generateJwtToken(final String accountUid, final Object connectedUser) {
		final Map<String, Object> claims = new HashMap<>();
		claims.put(CONNECTED_USER, connectedUser);
		return doGenerateToken(claims, accountUid, tokenValidity);
	}

	/**
	 * Generation d'un token de refresh pour un utilisateur un tokeb de refresh permet de redemander un token
	 */
	private String generateRefreshToken(final String accountUid, final Object connectedUser) {
		// Génération d'un token
		final Map<String, Object> claims = new HashMap<>();
		claims.put(CONNECTED_USER, connectedUser);
		return doGenerateToken(claims, accountUid, refreshTokenValidity);
	}

	/**
	 * Retourne les informations contenu dans un token
	 *
	 * @param token          token jwt
	 * @param claimsResolver Nom de la propriété
	 */
	private <T> T getClaimFromToken(final String token, final Function<C, T> claimsResolver) {
		final C claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * récupération de toutes les propriétes d'un token
	 */
	protected abstract C getAllClaimsFromToken(final String token);

	/**
	 * Détermine si le token est expiré
	 */
	protected abstract boolean isTokenExpired(final String token);

	/**
	 * Controle de l'existance du token de refresh dans la référentiel de l'application
	 */
	private boolean checkRefreshToken(final String token) {
		Tokens tokens = refreshTokens.remove(token);
		if (tokens != null) {
			return isTokenExpired(token);
		} else {
			return false;
		}
	}

	/**
	 * Generation d'un token
	 *
	 * @param claims   Issuer, Expiration, Subject, and the ID
	 * @param validity validity in seconde
	 */
	protected abstract String doGenerateToken(final Map<String, Object> claims, final String subject, final int validity);

	protected String getSecretKey() {
		if (secretKey == null) {
			secretKey = SecretKeyUtils.computeKeyFromPropery(secret);
		}
		return secretKey;
	}
}
