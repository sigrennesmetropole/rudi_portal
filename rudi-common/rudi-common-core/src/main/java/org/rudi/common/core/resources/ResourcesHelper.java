package org.rudi.common.core.resources;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.rudi.common.core.DocumentContent;

public abstract class ResourcesHelper {
	protected abstract String getBasePackage();

	protected abstract String getBaseDirectory();

	protected abstract Cache<String, DocumentContent> getCache();

	private final BidiMap<String, String> resourceMapping = new DualHashBidiMap<>();

	public DocumentContent loadResources(String resourceUuid) throws IOException {
		DocumentContent result = null;

		String resourceName = resourceMapping.get(resourceUuid);

		// Si la ressourceMapping ne contient pas de ressource de ce nom, on ne retourne rien.
		if (StringUtils.isBlank(resourceName)) {
			return result;
		}

		//Si la ressource est déjà dans le cache, on la retourne directement.
		if (getCache().containsKey(resourceUuid)) {
			// Récupération de la ressource dans le cache
			result = getCache().get(resourceUuid);

			// Réinitialisation du stream permettant la lecture du fichier.
			result.closeStream();

			// Vérification de la présence du fichier temporaire avant de le retourner
			if (result.getFile().exists() && result.getFile().isFile()) {
				return result;
			}
		}

		String uri = resourceName.replace("../", "/");
		File f = new File(getBaseDirectory(), uri);
		if (f.exists() && f.isFile()) {
			String mimeType = URLConnection.guessContentTypeFromName(f.getName());
			result = new DocumentContent(f.getName(), mimeType, f);
		} else {
			result = getDocumentContent(resourceName);
		}
		if (result != null) {
			getCache().put(resourceUuid, result);
		}

		return result;
	}

	protected DocumentContent getDocumentContent(String resourceName) throws IOException {
		return DocumentContent.fromResourcePath(getBasePackage() + resourceName);
	}

	public String fillResourceMapping(String value, String key) {
		// Si la valeur n'est pas déjà présente dans la Map on la rajoute à l'aide de la clé passée en paramètre.
		if (!resourceMapping.containsValue(value)) {
			// Map Bidirectionelle : key->value && value->key
			resourceMapping.put(key, value);
		}
		// On retourne la clé présente dans le ressourceMapping
		// Soit la clé insérée précédemment, soit celle déjà présente.
		return resourceMapping.getKey(value);
	}


}
