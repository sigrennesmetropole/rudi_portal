package org.rudi.microservice.kalim.service;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Type d'erreur lors d'une demande d'intégration.
 *
 * <ul>
 *     <li>ERR-1XX : Erreurs de base</li>
 *     <li>ERR-2XX : Erreurs sur le type du champ</li>
 *     <li>ERR-3XX : Erreur en lien avec les valeurs d’un champ</li>
 *     <li>ERR-5XX : Erreur technique (non fonctionnelle)</li>
 * </ul>
 */
public enum IntegrationError {

	ERR_101("ERR-101", "Le format du fichier transmis est incorrect"),
	ERR_102("ERR-102", "La balise '%s' est inconnue"), ERR_103("ERR-103", "Le paramètre '%s' est manquant"),
	ERR_104("ERR-104", "Le jeu de données '%s' n'existe pas"),
	ERR_105("ERR-105",
			"Erreur inconnue: l’erreur na pas été reconnue, veuillez contacter l’administrateur Rudi afin d’analyser l’erreur."),
	ERR_106("ERR-106", "La version de metadonnées %s n'est pas supportée. La version courante est %s."),
	ERR_107("ERR-107", "Le jeu de données '%s' n'est pas un selfData."),

	ERR_201("ERR-201", "Le type du champ '%s' n'est pas le bon (format attendu : '%s' / format reçu : '%s'} )"),
	ERR_202("ERR-202", "Le champ '%s' est manquant alors qu’il est obligatoire."),
	ERR_203("ERR-203",
			"La longueur du champ dépasse la limite autorisée: la taille attendue est  '%s' pour '%s' alors que la longueur de la valeur envoyée est '%s')."),

	ERR_301("ERR-301", "Des caractères ne sont pas acceptés dans le '%s'"),
	ERR_302("ERR-302",
			"La valeur saisie ne correspond pas au référentiel des valeurs attendues pour le champ '%s' (valeur saisie : "
					+ "'%s' / référentiel attendu : %s"),
	ERR_303("ERR-303", "La valeur saisie '%s' pour le champ '%s' ne correspond pas à %s"),
	ERR_304("ERR-304", "La valeur saisie '%s' pour le champ '%s' est déjà utilisée"),
	ERR_305("ERR-305", "Le jeu de données selfData est marqué comme ayant un accès API mais vous n'avez pas fourni exactement une API TPBC et une API GDATA"),
	ERR_306("ERR-306", "Si le champs '%s' est à vrai alors le champs '%s' doit l'être aussi  "),

	ERR_307("ERR-307", "La valeur saisie '%s' pour le champ '%s' ne correspond pas au format attendu"),
	ERR_308("ERR-308", "La valeur saisie 'chaine vide' pour le champ '%s' n'est pas accepté'"),

	ERR_403("ERR-403", "Le nœud fournisseur authentifié n'est pas le créateur du jeu de données"),
	ERR_405("ERR-405", "Des paramètres obligatoires pour les connecteurs ne sont pas renseignés."),

	ERR_500("ERR-500", "Une erreur technique est survenue. Veuillez contacter l'administrateur Rudi pour analyser l'erreur.");

	private final String message;
	private final String code;

	IntegrationError(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@JsonValue
	public String getMessage() {
		return message;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {

		return code.concat(" ").concat(message);
	}

}
