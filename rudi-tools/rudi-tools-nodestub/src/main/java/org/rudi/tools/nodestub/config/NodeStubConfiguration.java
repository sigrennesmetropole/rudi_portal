package org.rudi.tools.nodestub.config;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.nodestub")
@Getter
@Setter
public class NodeStubConfiguration {
	private File reportsDirectory;
	private File resourcesDirectory;
	private File endpointsDirectory;

	/**
	 * Répertoire contenant les tokens générés suite à un appariement Selfdata (cf RUDI-2395).
	 */
	private Path matchingTokensDirectory;

	/**
	 * la propriété utilisé pour être authentifié en tant que nodestub définit l'UUID de moi-même donc je la prend ici
	 */
	@Getter
	@Value("${module.oauth2.client-id}")
	private UUID nodestubUuid;

	/**
	 * UUID du JDD déchets.
	 */
	private UUID wasteDatasetUuid;

	/**
	 * UUID du producer du JDD déchets.
	 */
	private UUID wasteDatasetProducerUuid;

	/**
	 * nombre de jours pendant lesquels le token est valide
	 */
	private Long matchingTokenValidityDays;

	/**
	 * Souhaite-t-on utiliser un mock de l'API déchets de la DataFactory ? (false par défaut).
	 */
	private boolean mockWasteApi = false;

	/**
	 * Login de l'utilsateur dont la demande d'appariement est toujours "Traitée absent"
	 */
	private String blacklistedUserLogin;
	/**
	 * Dossier contenant les réponses mockées de l'API déchet.
	 */
	private Path wasteApiMockedResponseDirectory = Path.of("datafactory/api-recette/mocked-responses");

	@Value("#{'${rudi.nodestub.errors429}'.split(',')}")
	private List<String> errors429;

	/**
	 * {1} : Report UUID
	 */
	private MessageFormat reportsNameFormat = new MessageFormat("{0}.rpt");

	/**
	 * {1} : Resource UUID
	 */
	private MessageFormat resourcesNameFormat = new MessageFormat("{0}.json");

}
