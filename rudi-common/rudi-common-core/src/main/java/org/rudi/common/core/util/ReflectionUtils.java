package org.rudi.common.core.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;

public class ReflectionUtils {

	// TODO ajouter le cache + TU
	private Map<Field, Map<Function<PropertyDescriptor, Method>, Method>> findMethodForCache = new HashMap<>();

	private ReflectionUtils() {
	}

	@Nullable
	public static Method findGetterFor(Field javaField) {
		return findMethodFor(javaField, PropertyDescriptor::getReadMethod);
	}

	@Nullable
	public static Method findSetterFor(Field javaField) {
		return findMethodFor(javaField, PropertyDescriptor::getWriteMethod);
	}

	@Nullable
	private static Method findMethodFor(Field javaField, Function<PropertyDescriptor, Method> methodGetter) {
		final Class<?> javaFieldClass = javaField.getDeclaringClass();
		try {
			final var propertyDescriptors = Introspector.getBeanInfo(javaFieldClass).getPropertyDescriptors();
			return Arrays.stream(propertyDescriptors)
					.filter(propertyDescriptor -> propertyDescriptor.getName().equals(javaField.getName()))
					.map(methodGetter)
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
		} catch (IntrospectionException e) {
			throw new ReflectionException("Failed to get BeanInfo of class " + javaFieldClass, e);
		}
	}
}
