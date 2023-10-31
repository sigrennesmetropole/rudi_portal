package org.rudi.facet.dataverse.fields;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

class FieldSpecFromJavaField extends ChildFieldSpec {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldSpecFromJavaField.class);

	@Nonnull
	private final Field javaField;

	private Boolean required;

	private FieldSpecFromJavaField(@Nonnull FieldSpec parent, @Nonnull Field javaField) {
		super(parent);
		this.javaField = javaField;

		parent.getDirectChildren().add(this);
	}

	FieldSpecFromJavaField(@Nonnull FieldSpec parent, Class<?> javaFieldClass, @Nonnull String javaFieldName) {
		this(parent, getJavaField(javaFieldClass, javaFieldName));
	}

	FieldSpecFromJavaField(@Nonnull FieldSpec parent, @Nonnull String javaFieldName) {
		this(parent, parent.getValueType(), javaFieldName);
	}

	@Nonnull
	private static <T> Field getJavaField(Class<T> javaFieldClass, String javaFieldName) {
		try {
			return javaFieldClass.getDeclaredField(javaFieldName);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					String.format("La classe %s ne possède pas de champ %s", javaFieldClass, javaFieldName));
		}
	}

	@Nullable
	@Override
	public String getLocalName() {
		var jsonProperty = javaField.getAnnotation(JsonProperty.class);
		if (jsonProperty == null) {
			try {
				Method m = javaField.getDeclaringClass().getMethod("get" + StringUtils.capitalize(javaField.getName()));
				jsonProperty = m.getAnnotation(JsonProperty.class);
			} catch (Exception e) {
				// rien
			}
		}
		if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
			return jsonProperty.value();
		} else {
			return javaField.getName();
		}
	}

	@Override
	public Class<?> getJavaType() {
		return javaField.getType();
	}

	@Override
	public Class<?> getValueType() {
		final Class<?> type = getJavaType();
		if (List.class.isAssignableFrom(type)) {
			return (Class<?>) getListItemType(javaField);
		} else {
			return type;
		}
	}

	private static Type getListItemType(@Nonnull Field javaField) {
		final var parameterizedType = (ParameterizedType) javaField.getGenericType();
		return parameterizedType.getActualTypeArguments()[0];
	}

	@Override
	@Nullable
	public String getDescription() {
		final Class<?> javaFieldDeclaringClass = javaField.getDeclaringClass();
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(javaFieldDeclaringClass).getPropertyDescriptors()) {
				final var getter = pd.getReadMethod();
				if (getter != null && pd.getName().equals(javaField.getName())) {
					final var schema = getter.getAnnotation(Schema.class);
					if (schema != null && StringUtils.isNotEmpty(schema.description())) {
						return schema.description();
					} else {
						return null;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Impossible de trouver la description pour le champ {}", getName(), e);
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FieldSpecFromJavaField))
			return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean isRequired() {
		if (required == null) {
			required = isRequiredField(javaField);
		}
		return required;
	}

	private static boolean isRequiredField(Field javaField) {
		final Method getter = getGetter(javaField);
		if (getter != null) {
			final Boolean isRequiredThroughGetter = isRequiredFieldOrMethod(getter);
			if (isRequiredThroughGetter != null) {
				return isRequiredThroughGetter;
			}
		}

		final Boolean isRequiredThroughField = isRequiredFieldOrMethod(javaField);
		if (isRequiredThroughField != null) {
			return isRequiredThroughField;
		}

		return false;
	}

	@Nullable
	private static Boolean isRequiredFieldOrMethod(AccessibleObject fieldOrMethod) {
		final var notNull = fieldOrMethod.getAnnotation(NotNull.class);
		if (notNull != null) {
			return true;
		}

		final var schema = fieldOrMethod.getAnnotation(Schema.class);
		if (schema != null) {
			return schema.required();
		}

		return null;
	}

	@Nullable
	private static Method getGetter(Field javaField) {
		final Class<?> javaFieldClass = javaField.getDeclaringClass();
		try {
			final var propertyDescriptors = Introspector.getBeanInfo(javaFieldClass).getPropertyDescriptors();
			return Arrays.stream(propertyDescriptors)
					.filter(propertyDescriptor -> propertyDescriptor.getName().equals(javaField.getName()))
					.map(PropertyDescriptor::getReadMethod).filter(Objects::nonNull).findFirst().orElse(null);
		} catch (IntrospectionException e) {
			throw new RuntimeException("Cannot find getters for class " + javaFieldClass, e);
		}
	}
}
