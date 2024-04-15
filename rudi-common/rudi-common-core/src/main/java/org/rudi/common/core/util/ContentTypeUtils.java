package org.rudi.common.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;


public class ContentTypeUtils {

	ContentTypeUtils(){}
	public static MediaType normalize(String contentTypeValue) {
		final MediaType contentType;
		if (contentTypeValue == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		} else {
			String fullType = extractType(contentTypeValue);
			Map<String, String> parameters = extractParameters(contentTypeValue);
			// Vérifie s'il y avait des paramètres.
			if(MapUtils.isNotEmpty(parameters)){
				for (Map.Entry<String, String> parameter : parameters.entrySet()) {
					if (parameter.getValue().contains("/")) {
						// Rajout des quotes si elles ne sont pas présentes
						parameters.put(parameter.getKey(), StringUtils.wrapIfMissing(parameter.getValue(),'"'));
					} else {
						parameters.put(parameter.getKey(), parameter.getValue());
					}
				}
				// join
				contentType = MediaType.parseMediaType(fullType + "; " + parameters.entrySet().stream()
						.map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(" ")));
			}
			else {
				contentType = MediaType.parseMediaType(fullType);
			}
		}
		return contentType;
	}

	private static String extractType(String mimeType) {
		int index = mimeType.indexOf(';');
		return (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
	}

	/**
	 * Permet de retourner les paramètres du mimeType
	 *
	 * @param mimeType exemple application/json;name="myParams"
	 * @return s'il y a des paramètres : Map<clé,"valeur">, retourne NULL sinon
	 *
	 */
	private static Map<String, String> extractParameters(String mimeType) {
		Map<String, String> parameters = new HashMap<>();
		String[] substr = StringUtils.split(mimeType, ";");

		//Si le mimeType contient des paramètres
		if(substr != null && substr.length > 1){

			// On parcourt l'ensemble des paramètres séparés par un ";"
			// Les parameters commencent à l'index 1, l'index 0 est occupé par le mimeType
			for (int i = 1; i < substr.length; i++) {
				// On sépare la clé et la valeur du paramètre clé=param
				String[] stringParams = StringUtils.split(substr[i], "=");

				//Si le paramètre est bien composé d'une clé et d'une valeur
				if(stringParams!= null && stringParams.length == 2) {
					String key = stringParams[0].trim();
					String value = stringParams[1].trim();

					parameters.put(key, value);
				}
			}
		}

		return parameters;
	}

	/**
	 * @param contentType contentType du file à tester
	 * @param allowedContentTypes liste des contentType autorisés
	 * @throws IllegalArgumentException si le content type n'est pas dans la liste des contentTypes autorisés
	 */
	public static void checkMediaType(String contentType, List<String> allowedContentTypes) {
		List<MediaType> mediaTypes = MediaType.parseMediaTypes(allowedContentTypes);
		if(!mediaTypes
				.contains(ContentTypeUtils.normalize(contentType))){
			throw new IllegalArgumentException(
					String.format("Not allowed content type for attachment : %s", contentType));
		}
	}
}
