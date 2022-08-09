package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class ObjectsUtils {

	private static final Map<Class<?>, Object> EMPTY_INSTANCES = new HashMap<>();

	private ObjectsUtils() {
	}

	@Nullable
	public static <T> T nullIfEmpty(T instance) {
		if (instance == null) {
			return null;
		}
		final var instanceClass = instance.getClass();
		final var emptyInstance = getEmptyInstanceOf(instanceClass);
		return emptyInstance.equals(instance) ? null : instance;
	}

	@Nonnull
	private static <T> T getEmptyInstanceOf(Class<?> instanceClass) {
		//noinspection unchecked
		return (T) EMPTY_INSTANCES.computeIfAbsent(instanceClass, key -> newEmptyInstanceOf(instanceClass));
	}

	@Nonnull
	private static <T> T newEmptyInstanceOf(Class<T> instanceClass) {
		try {
			return instanceClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new NotImplementedException("Cannot create new empty instance of " + instanceClass, e);
		}
	}

}
