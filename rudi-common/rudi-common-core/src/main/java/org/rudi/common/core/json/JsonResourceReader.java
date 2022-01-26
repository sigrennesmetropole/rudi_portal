package org.rudi.common.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Getter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Simplifie le chargement et le parsing de fichier JSON (depuis le classpath) notamment pour les tests unitaires.
 */
@Component
public class JsonResourceReader {
	@Getter
	private final ObjectMapper objectMapper;

	/**
	 * For unit testing
	 */
	public JsonResourceReader() {
		this(new DefaultJackson2ObjectMapperBuilder());
	}

	public JsonResourceReader(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
		this.objectMapper = jackson2ObjectMapperBuilder.build();
	}

	/**
	 * Exemple :
	 *
	 * <pre>read("metadata/agri.json", Metadata.class)</pre>
	 *
	 * @param path      chemin du fichier JSON à parser (relatif au classpath)
	 * @param valueType type de l'objet à parser (classe Java)
	 * @param <T>       type de l'objet à parser
	 * @return l'objet parsé à partir du fichier JSON
	 * @throws IOException si le fichier est introuvable ou si une erreur apparaît lors du parsing JSON
	 */
	public <T> T read(String path, Class<T> valueType) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(path);
		if (resource == null) {
			throw new FileNotFoundException("Ressource de type " + valueType.getSimpleName() + " introuvable dans le classpath à ce chemin : " + path);
		}
		return objectMapper.readValue(resource, valueType);
	}

	/**
	 * @param path chemin du fichier JSON à parser (relatif au classpath)
	 * @return l'objet parsé à partir du fichier JSON sous forme de {@link java.util.Map}
	 * @throws IOException si le fichier est introuvable ou si une erreur apparaît lors du parsing JSON
	 */
	public Map<String, Object> readMap(String path) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(path);
		if (resource == null) {
			throw new FileNotFoundException("Ressource introuvable dans le classpath à ce chemin : " + path);
		}
		final MapType valueType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		return objectMapper.readValue(resource, valueType);
	}

	/**
	 * @param path     chemin du fichier JSON à parser (relatif au classpath)
	 * @param itemType type des éléments de la liste à parser (classe Java)
	 * @param <T>      type des éléments de la liste à parser
	 * @return la liste des éléments parsés à partir du fichier JSON
	 * @throws IOException si le fichier est introuvable ou si une erreur apparaît lors du parsing JSON
	 */
	public <T> List<T> readList(String path, Class<T> itemType) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(path);
		if (resource == null) {
			throw new FileNotFoundException("Ressources de type " + itemType.getSimpleName() + " introuvable dans le classpath à ce chemin : " + path);
		}
		final CollectionType valueType = objectMapper.getTypeFactory().constructCollectionType(List.class, itemType);
		return objectMapper.readValue(resource, valueType);
	}
}
