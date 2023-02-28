package org.rudi.common.facade.config.filter;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.rudi.common.core.util.SecretKeyUtils;
import org.springframework.beans.factory.annotation.Value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public abstract class AbstractJwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -7253285508907149002L;

	public static final String HEADER_TOKEN_JWT_AUTHENT_KEY = "Authorization";

	public static final String HEADER_X_TOKEN_KEY = "X-TOKEN";

	public static final String HEADER_TOKEN_JWT_PREFIX = "Bearer ";

	protected static final String CONNECTED_USER = "connectedUser";

	protected static final String ISSUER_RUDI = "Rudi#";

	@Value("${security.jwt.access.tokenKey}")
	private String secret;

	@Value("${security.jwt.access.jti:Rudi2021}")
	private String jwtId;

	private String secretKey;

	/**
	 * Validité du token par défaut low : 10 minutes
	 */
	@Value("${security.jwt.validity:600}")
	private int tokenValidity;

	/**
	 * Validité du refresh + haut par défaut : 30 minutes
	 */
	@Value("${security.jwt.refresh.validity:1800}")
	private int refreshTokenValidity;

	@Value("${security.jwt.apim.url:https://rudi.bzh}")
	private String apimUrl;

	@Value("${security.jwt.apim.hostVerifier:false}")
	private boolean apimHostVerifier;

	@Value("${security.jwt.apim.url.jwks:/oauth2/jwks}")
	private String apimJwks;

	@Value("${security.jwt.apim.algorithm:RS256}")
	private String wso2TokenAlgorithm;

	private final transient Map<String, Tokens> refreshTokens = new HashMap<>();

	@Getter(value = AccessLevel.PROTECTED)
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * récupération une propriété d'un token
	 *
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getTokenProperty(final String token, final String propertyName) throws ParseException {
		return (T) getAllClaimsFromToken(token).getClaim(propertyName);
	}

	/**
	 * Retourne sujet associé au token
	 *
	 * @throws ParseException
	 */
	public String getSubjectFromToken(final String token) throws ParseException {
		return getClaimFromToken(token, getSubjectFunction());
	}

	/**
	 * Retourne la date d'expiration du token
	 *
	 * @throws ParseException
	 */
	public Date getExpirationDateFromToken(final String token) throws ParseException {
		return getClaimFromToken(token, getExpirationFunction());
	}

	/**
	 * Genration du token JWT et du token de refresh
	 */
	public Tokens generateTokens(final String accountLogin, final Object connectedUser)
			throws JOSEException, JsonProcessingException {
		// Génération du token d'authentification et de refesh
		final Tokens tokens = new Tokens();
		tokens.setJwtToken(HEADER_TOKEN_JWT_PREFIX + generateJwtToken(accountLogin, connectedUser));
		tokens.setRefreshToken(HEADER_TOKEN_JWT_PREFIX + generateRefreshToken(accountLogin, connectedUser));
		refreshTokens.put(tokens.getRefreshToken(), tokens);
		return tokens;
	}

	/**
	 * Generation de nouveau token à partir du refresh token
	 *
	 * @param refreshToken
	 * @return
	 * @throws RefreshTokenExpiredException
	 * @throws JsonProcessingException
	 * @throws JOSEException
	 */
	public Tokens generateNewJwtTokens(final String refreshToken)
			throws RefreshTokenExpiredException, JsonProcessingException, JOSEException {
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

			try {
				SignedJWT jwt = getJWS(tokenJwt);
				JWTClaimsSet claims = jwt.getJWTClaimsSet();

				handleSubject(token, claims);
				handleIssuer(token, claims);
				handleExpired(token, claims);
				handleAccount(token, claims);
				token.getProperties().putAll(claims.getClaims());

				verify(token, jwt);
			} catch (final Exception e) {
				log.error("impossible de récupérer le token JWT", e);
				token.setHasError(true);
			}
		}

		return token;
	}

	protected void handleIssuer(JwtTokenData token, JWTClaimsSet claims) {
		token.setIssuer(claims.getIssuer());
	}

	protected void handleAccount(JwtTokenData token, JWTClaimsSet claims) throws JsonProcessingException {
		if (isPortailIssuer(token)) {
			handlePortailAccount(token, claims);
		} else {
			handleExternalAccount(token, claims);
		}
	}

	protected abstract void handleExternalAccount(JwtTokenData token, JWTClaimsSet claims)
			throws JsonProcessingException;

	protected abstract void handlePortailAccount(JwtTokenData token, JWTClaimsSet claims)
			throws JsonProcessingException;

	protected void handleExpired(JwtTokenData token, JWTClaimsSet claims) {
		token.setExpired(isTokenExpired(claims));
	}

	/**
	 * Extraction du sujet
	 *
	 * @param token
	 * @param claims
	 */
	protected void handleSubject(JwtTokenData token, JWTClaimsSet claims) {
		token.setSubject(getSubjectFromClaims(claims));
	}

	/**
	 * Generation d'un token pour un utilisateur
	 *
	 * @param accountUid id de l'iutilisateur
	 * @throws JsonProcessingException
	 */
	protected String generateJwtToken(final String accountUid, final Object connectedUser)
			throws JOSEException, JsonProcessingException {
		return doGenerateToken(prepareClaims(connectedUser), accountUid, tokenValidity);
	}

	/**
	 * Prépare la map contenant les claims
	 *
	 * @param connectedUser
	 * @return
	 * @throws JsonProcessingException
	 */
	protected Map<String, Object> prepareClaims(final Object connectedUser) throws JsonProcessingException {
		Map<String, Object> claims = new HashMap<>();
		String serialiedConnectedUser = mapper.writeValueAsString(connectedUser);
		claims.put(CONNECTED_USER, serialiedConnectedUser);
		return claims;
	}

	/**
	 * https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature
	 *
	 * @param token
	 * @param jwt
	 * @throws JOSEException
	 * @throws MalformedURLException
	 * @throws BadJOSEException
	 */
	@SuppressWarnings("unchecked")
	protected void verify(JwtTokenData token, SignedJWT jwt) throws JOSEException, MalformedURLException {
		if (isPortailIssuer(token)) {
			JWSVerifier signer = new MACVerifier(getSecretKey());
			token.setHasError(!jwt.verify(signer));
			log.debug("Verify Rudi issuer {}", token.isHasError());
		} else {
			// Create a JWT processor for the access tokens
			ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

			// Set the required "typ" header "at+jwt" for access tokens issued by the
			// Connect2id server, may not be set by other servers
			jwtProcessor.setJWSTypeVerifier(DefaultJOSEObjectTypeVerifier.JWT);

			// The public RSA keys to validate the signatures will be sourced from the
			// OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
			// object caches the retrieved keys to speed up subsequent look-ups and can
			// also handle key-rollover
			ResourceRetriever resourceRetriever = new JwkResourceRetriever(RemoteJWKSet.DEFAULT_HTTP_CONNECT_TIMEOUT,
					RemoteJWKSet.DEFAULT_HTTP_READ_TIMEOUT, RemoteJWKSet.DEFAULT_HTTP_SIZE_LIMIT, true,
					apimHostVerifier);
			JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(getJWKURL()), resourceRetriever);

			// Configure the JWT processor with a key selector to feed matching public
			// RSA keys sourced from the JWK set URL
			JWSAlgorithm algorithm = JWSAlgorithm.parse(wso2TokenAlgorithm);
			JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(algorithm, keySource);

			jwtProcessor.setJWSKeySelector(keySelector);
			try {
				JWTClaimsSet claimsSet = jwtProcessor.process(jwt, null);
				log.debug("Token processed {}", claimsSet);
			} catch (BadJOSEException e) {
				log.debug("Failed to verify token", e);
				token.setHasError(true);
			}
			log.debug("Verify APIM issuer as error?{}", token.isHasError());
		}
	}

	/**
	 * Generation d'un token de refresh pour un utilisateur un tokeb de refresh permet de redemander un token
	 *
	 * @throws JOSEException
	 * @throws JsonProcessingException
	 */
	protected String generateRefreshToken(final String accountUid, final Object connectedUser)
			throws JOSEException, JsonProcessingException {
		return doGenerateToken(prepareClaims(connectedUser), accountUid, refreshTokenValidity);
	}

	/**
	 * Retourne les informations contenu dans un token
	 *
	 * @param token          token jwt
	 * @param claimsResolver Nom de la propriété
	 * @throws ParseException
	 */
	protected <T> T getClaimFromToken(final String token, final Function<JWTClaimsSet, T> claimsResolver)
			throws ParseException {
		final JWTClaimsSet claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * récupération de toutes les propriétes d'un token
	 *
	 * @param token
	 * @return
	 * @throws ParseException
	 */
	protected JWTClaimsSet getAllClaimsFromToken(final String token) throws ParseException {
		return getJWS(token).getJWTClaimsSet();
	}

	/**
	 * Détermine si le token est expiré
	 *
	 * @param token
	 * @return
	 */
	protected boolean isTokenExpired(final String token) {
		boolean result = false;
		try {
			Date d = getExpirationDateFromToken(token);
			Date now = new Date();
			if (d.after(now)) {
				result = true;
			}
		} catch (Exception e) {
			result = true;
		}
		return result;
	}

	/**
	 * Controle de l'existance du token de refresh dans la référentiel de l'application
	 */
	protected boolean checkRefreshToken(final String token) {
		// on regarde si le token fourni est géré par l'appli
		String tokenAndPrefix = HEADER_TOKEN_JWT_PREFIX + token;
		if (refreshTokens.containsKey(tokenAndPrefix)) {

			// Si oui on va regarder s'il est expiré, de toute façon le token sera utilisé
			// donc on le sort de la gestion de l'appli
			refreshTokens.remove(tokenAndPrefix);

			// On regarde si le token de refresh a expiré ou non
			return isTokenExpired(token);
		}
		// Le token de refresh n'est pas géré par l'appli donc il est invalide
		else {
			return false;
		}
	}

	/**
	 * Parsing d'un token (pas de validation)
	 *
	 * @param token
	 * @return
	 * @throws ParseException
	 */
	protected SignedJWT getJWS(final String token) throws ParseException {
		return SignedJWT.parse(token);
	}

	/**
	 * Récupération une propriété d'un claims
	 *
	 * @param <T>
	 * @param claims
	 * @param propertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getTokenProperty(final JWTClaimsSet claims, final String propertyName) {
		return (T) claims.getClaim(propertyName);
	}

	/**
	 * Retourne les informations contenu dans un token
	 *
	 * @param <T>
	 * @param claims         la liste des claims
	 * @param claimsResolver le resolver
	 * @return
	 */
	protected <T> T getClaimFromClaims(final JWTClaimsSet claims, final Function<JWTClaimsSet, T> claimsResolver) {
		return claimsResolver.apply(claims);
	}

	/**
	 * Retourne la date d'expiration du token
	 *
	 * @param claims
	 * @return
	 * @throws ParseException
	 */
	protected Date getExpirationDateFromToken(final JWTClaimsSet claims) {
		return claims.getExpirationTime();
	}

	/**
	 * Détermine si le token est expiré
	 *
	 * @param claims
	 * @return vrai si le token est expéré
	 */
	protected boolean isTokenExpired(final JWTClaimsSet claims) {
		boolean result = false;
		Date d = getExpirationDateFromToken(claims);
		Date now = new Date();
		if (d.before(now)) {
			result = true;
		}
		return result;
	}

	/**
	 * @param claims
	 * @return le subject
	 */
	protected String getSubjectFromClaims(JWTClaimsSet claims) {
		return getClaimFromClaims(claims, getSubjectFunction());
	}

	protected Function<JWTClaimsSet, String> getSubjectFunction() {
		return JWTClaimsSet::getSubject;
	}

	protected Function<JWTClaimsSet, Date> getExpirationFunction() {
		return JWTClaimsSet::getExpirationTime;
	}

	/**
	 * Generation d'un token
	 *
	 * @param claims   Issuer, Expiration, Subject, and the ID
	 * @param validity validity in seconde
	 */
	protected abstract String doGenerateToken(final Map<String, Object> claims, final String subject,
			final int validity) throws JOSEException;

	/**
	 * @return la clef de signature
	 */
	protected String getSecretKey() {
		if (secretKey == null) {
			secretKey = SecretKeyUtils.computeKeyFromPropery(secret);
		}
		return secretKey;
	}

	/**
	 * L'url du JWK de controle externe
	 *
	 * @return
	 */
	protected String getJWKURL() {
		return apimUrl + apimJwks;
	}

	/**
	 * Retoure si token parsé a été produit par le portail ou pas
	 *
	 * @param token
	 * @return
	 */
	protected boolean isPortailIssuer(JwtTokenData token) {
		return ISSUER_RUDI.equals(token.getIssuer());
	}

	/**
	 * @return le JWTId
	 */
	protected String getJWTId() {
		return jwtId;
	}

	/**
	 * Supprime le refreshToken en vue d'une déconnexion
	 *
	 * @param token le refreshToken à supprimer
	 */
	public void deleteRefreshToken(String token) {
		refreshTokens.remove(token);
	}
}
