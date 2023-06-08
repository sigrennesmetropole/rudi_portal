package org.rudi.facet.apimaccess.api;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

public class ContentTypeUtils {

	public static MediaType normalize(String contentTypeValue) {
		final MediaType contentType;
		if (contentTypeValue == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		} else {
			String fullType = extractType(contentTypeValue);
			Map<String, String> parameters = extractParameters(contentTypeValue);
			Map<String, String> outputParameters = new HashMap<String, String>();
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				if (parameter.getValue().contains("/") && !parameter.getValue().startsWith("\"")) {
					outputParameters.put(parameter.getKey(), "\"" + parameter.getValue() + "\"");
				} else {
					outputParameters.put(parameter.getKey(), parameter.getValue());
				}
			}
			// join
			contentType = MediaType.parseMediaType(fullType + "; " + outputParameters.entrySet().stream()
					.map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(" ")));
		}
		return contentType;
	}

	private static String extractType(String mimeType) {
		int index = mimeType.indexOf(';');
		return (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
	}

	private static Map<String, String> extractParameters(String mimeType) {
		int index = mimeType.indexOf(';');

		Map<String, String> parameters = null;
		do {
			int nextIndex = index + 1;
			boolean quoted = false;
			while (nextIndex < mimeType.length()) {
				char ch = mimeType.charAt(nextIndex);
				if (ch == ';') {
					if (!quoted) {
						break;
					}
				} else if (ch == '"') {
					quoted = !quoted;
				}
				nextIndex++;
			}
			String parameter = mimeType.substring(index + 1, nextIndex).trim();
			if (parameter.length() > 0) {
				if (parameters == null) {
					parameters = new LinkedHashMap<>(4);
				}
				int eqIndex = parameter.indexOf('=');
				if (eqIndex >= 0) {
					String attribute = parameter.substring(0, eqIndex).trim();
					String value = parameter.substring(eqIndex + 1).trim();
					parameters.put(attribute, value);
				}
			}
			index = nextIndex;
		} while (index < mimeType.length());

		return parameters;
	}

}
