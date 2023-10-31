package org.rudi.facet.apimaccess.exception;

import org.springframework.http.HttpStatus;

/**
 * Provoquée lorsque l'endpoint d'une API déclarée par le portail dans WSO2 renvoie une erreur. L'erreur peut être observée dans l'application RUDI
 * lorsqu'on tente de télécharger un média d'un jeu de données.
 *
 * <p>
 * Lorsque cette erreur se produit, il peut être nécessaire de vérifier les points suivants :
 * </p>
 *
 * <ul>
 * <li>Noter l'identifiant du média dont le téléchargement échoue (par exemple dans l'URL
 * <code>/konsult/v1/datasets/e3218a63-e3c6-4a14-b276-43c8384dade3/media/84486a5a-2ec6-401a-b283-7bdddc6f4be2/download</code> l'identifiant du média
 * est <code>84486a5a-2ec6-401a-b283-7bdddc6f4be2</code></li>
 * <li>Retrouver l'API correspondant au média dans WSO2 :
 * <ul>
 * <li>Se connecter à l'application WSO2 Publisher et consulter la liste des API (URL : <code>/publisher/apis</code>)</li>
 * <li>Lancer une recherche sur l'identifiant du média précédent. Exemple : <code>media_uuid:84486a5a-2ec6-401a-b283-7bdddc6f4be2</code></li>
 * <li>Ouvrir l'API trouvée</li>
 * <li>Aller dans la section Endpoints</li>
 * <li>Vérifier si l'URL renseignée dans le champ <code>Production Endpoint</code> fonctionne bien avec n'importe quel navigateur</li>
 * <li>Si la même erreur apparaît que celle observée par l'application RUDI alors WSO2 n'est pas en cause. L'URL fournie par le nœud fournisseur n'est
 * pas/plus valide.</li>
 * <li>Si l'URL fonctionne bien alors les API WSO2 sont dans un état incohérent qu'on peut débloquer en exécutant le script re-deploy-all-apis.sh
 * directement sur la machine hébergeant WSO2 (cf HTTP 404 corrigée par la RUDI-1938). Ce script va en fait faire basculer temporairement chaque API
 * vers un autre état et la débloquer</li>
 * </ul>
 * </li>
 * </ul>
 */
public class APIEndpointException extends APIManagerException {

	private static final long serialVersionUID = -7591062696240717495L;

	public APIEndpointException(String apiAccessUrl) {
		super(String.format("Aucune réponse reçue du endpoint de l'API %s", apiAccessUrl));
	}

	public APIEndpointException(String apiAccessUrl, HttpStatus httpStatus) {
		super(String.format(
				"HTTP %s reçu du endpoint de l'API %s. Si l'erreur HTTP renvoyée par WSO2 n'est pas reproduite en interrogeant directement le endpoint alors WSO2 est la cause du problème. Il peut être nécessaire d'exécuter le script re-deploy-all-apis.sh sur la machine hébergeant WSO2 (cf RUDI-1938).",
				httpStatus, apiAccessUrl));
	}
}
