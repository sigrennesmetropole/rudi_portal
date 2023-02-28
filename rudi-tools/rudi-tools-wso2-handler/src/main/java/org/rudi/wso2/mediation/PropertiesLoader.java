package org.rudi.wso2.mediation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.util.ReflectionUtils;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
class PropertiesLoader {

	/**
	 * Charge un fichier de properties à la manière de Spring : on cherche dans le fichier les properties correspondant
	 * aux propriétés de la classe propertiesClass, préfixées par le nom de la classe sans le suffixe "Properties".
	 *
	 * <p>Cf TU pour un exemple d'utilisation.</p>
	 */
	public <T> T loadProperties(Path path, Class<T> propertiesClass) throws IOException {
		log.info("Loading properties file : " + path);

		final var prefix = getPrefix(propertiesClass);

		final T properties = createProperties(propertiesClass);

		final var fileProperties = new Properties();
		try (final var inputStream = Files.newInputStream(path)) {
			fileProperties.load(inputStream);
		}

		for (final var field : propertiesClass.getDeclaredFields()) {
			loadProperty(prefix, field, fileProperties, properties);
		}

		log.info("Properties file loaded.");
		return properties;
	}

	@Nonnull
	private static <T> T createProperties(Class<T> propertiesClass) {
		try {
			final var constructor = propertiesClass.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
				 IllegalAccessException e) {
			throw new CannotCreatePropertiesException(e);
		}
	}

	private <T> void loadProperty(String prefix, Field field, Properties fileProperties, T properties) {
		final var nameInFile = prefix + "." + propertyNameInFileWithoutPrefix(field);

		try {
			final var valueInFile = fileProperties.get(nameInFile);

			if (valueInFile == null) {
				checkPropertyHasDefaultValue(field, properties, nameInFile);
			} else {
				final var setter = ReflectionUtils.findSetterFor(field);
				if (setter == null) {
					log.debug("No setter found for field " + field);
				} else {
					setter.invoke(properties, valueInFile);
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new CannotLoadPropertyException(nameInFile, e);
		}
	}

	private static <T> void checkPropertyHasDefaultValue(Field field, T properties, String nameInFile) throws IllegalAccessException, InvocationTargetException {
		final var getter = ReflectionUtils.findGetterFor(field);
		if (getter == null) {
			log.debug("No getter found for field " + field);
		} else {
			final var defaultValue = getter.invoke(properties);
			if (defaultValue == null) {
				throw new MissingRequiredPropertyException(nameInFile);
			}
		}
	}

	@Nonnull
	private static <T> String getPrefix(Class<T> propertiesClass) {
		final var annotation = propertiesClass.getAnnotation(WSO2HandlerProperties.class);
		if (annotation == null) {
			throw new IllegalClassException(String.format("%s class must be annotated with @WSO2HandlerProperties.", propertiesClass.getSimpleName()));
		}
		return annotation.prefix();
	}

	private String propertyNameInFileWithoutPrefix(Field field) {
		return splitWordsWith(field.getName(), "-");
	}

	private static String splitWordsWith(final String string, final String separator) {
		return StringUtils.uncapitalize(string).replaceAll("([A-Z])", separator + "$1").toLowerCase();
	}
}
