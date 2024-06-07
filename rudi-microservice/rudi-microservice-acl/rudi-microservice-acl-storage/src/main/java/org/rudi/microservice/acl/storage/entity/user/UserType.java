package org.rudi.microservice.acl.storage.entity.user;

/**
 * Le type des utilisateurs
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
