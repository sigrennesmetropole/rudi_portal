/**
 * 
 */
package org.rudi.common.core.security;

/**
 * @author FNI18300
 *
 */
public enum UserType {

	/** utilisateur humain ... */
	PERSON,
	/** utilisateur de type API (associé à un projet) */
	API,
	/** utilisateur de type µservice */
	MICROSERVICE,
	/** utilisateur robot autre que les précédents */
	ROBOT;
}
