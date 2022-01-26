package org.rudi.microservice.kalim.service;

import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.kaccess.bean.Metadata;

import java.io.IOException;
import java.util.UUID;

public class KalimTestConfigurer {

	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();

	/**
	 * Initialisation d'un JDD a partir du JSON de conf
	 * @return un JDD OK
	 * @throws IOException levée si KO lecture JSON de conf
	 */
	public static Metadata initMetadata() throws IOException {
		final Metadata metadata = JSON_RESOURCE_READER.read("metadata/create-ok.json", Metadata.class);
		randomizeUuids(metadata);
		return metadata;
	}

	/**
	 * Initialisation d'un JDD a partir de son NOM dans la conf, ne randomize pas les UUIds (copie direct du fichier)
	 * @return un JDD OK venant des ressources
	 * @throws IOException levée si KO lecture JSON de conf
	 */
	public static Metadata initMetadataWithName(String metadataName) throws IOException {
		return JSON_RESOURCE_READER.read("metadata/" + metadataName, Metadata.class);
	}

	/**
	 * Pour éviter des erreurs 409 avec WSO2, on remplace certains UUID par des UUID aléatoires
	 *
	 * @param metadata JDD à traiter
	 */
	private static void randomizeUuids(Metadata metadata) {
		if (metadata.getGlobalId() != null) {
			metadata.setGlobalId(UUID.randomUUID());
		}
		metadata.getAvailableFormats().forEach(media -> media.setMediaId(UUID.randomUUID()));
	}
}
