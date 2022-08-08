package org.rudi.common.facade.config.filter;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class JwtTokenData {

	// Token JWT
	private String token;

	// Issuer du token
	private String issuer;

	// Nom unique contenu dans le token
	private String subject;

	// Nom d'affichage
	private Object account;

	// Le token est il expiré
	private boolean isExpired;

	// Le token contient t'il des erreurs
	private boolean hasError;

	private Map<String, Object> properties = new HashMap<>();

}
