package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.EndpointKeyType;

/**
 * Provoqué lorsque WSO2 ne retrouve plus les clés associées aux applications (cf RUDI-1656).
 *
 * <p>
 * Pour chaque application WSO2, une clé de type "Production" doit être générée dans le DevPortal via :
 * </p>
 *
 * <ol>
 *     <li>Pour chaque utilisateur (RUDI/rudi et RUDI/anonymous), se connecter sur le DevPortal WSO2</li>
 *     <li>Bouton Applications</li>
 *     <li>Cliquer sur l'application "rudi_application"</li>
 *     <li>Cliquer sur le menu "Production Keys"</li>
 *     <li>Cliquer sur le bouton "Generate Keys"</li>
 * </ol>
 *
 * <p>
 *     Si une erreur WSO2 empêche la génération des clés, alors il faut :
 * </p>
 *
 * <ol>
 *     <li>Recréer une nouvelle application à partir de <code>rudi_application</code> : <code>rudi_application_new</code></li>
 *     <li>Transférer toutes les souscriptions directement dans la base WSO2 (cf script SQL Ansible RUDI-1656-switch_applications.sql, le script renomme également les applications)</li>
 *     <li>Supprimer l'ancienne application <code>rudi_application_old</code></li>
 *     <li>Mettre à jour toutes les API via le script <code>update-all-apis.sh</code> du répertoire scripts de WSO2 sur la machine hébergeant WSO2</li>
 *     <li>Retenter la procédure précédente dans le DevPortal WSO2</li>
 * </ol>
 */
public class ApplicationKeysNotFoundException extends APIManagerException {

	public ApplicationKeysNotFoundException(String applicationId, String username) {
		super(String.format("Aucune clé trouvée pour l'application %s du user %s",
				applicationId,
				username));
	}

	public ApplicationKeysNotFoundException(String applicationId, String username, EndpointKeyType keyType) {
		super(String.format("Aucune clé de type %s trouvée pour l'application %s du user %s",
				keyType,
				applicationId,
				username));
	}

}
