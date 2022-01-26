package org.rudi.facet.dataverse.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

class FieldSpecFromJavaField extends ChildFieldSpec {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldSpecFromJavaField.class);

	@NotNull
	private final Field javaField;

	private FieldSpecFromJavaField(@NotNull FieldSpec parent, @NotNull Field javaField) {
		super(parent);
		this.javaField = javaField;

		parent.getChildren().add(this);
	}

	FieldSpecFromJavaField(@NotNull FieldSpec parent, Class<?> javaFieldClass, @NotNull String javaFieldName) {
		this(parent, getJavaField(javaFieldClass, javaFieldName));
	}

	FieldSpecFromJavaField(@NotNull FieldSpec parent, @NotNull String javaFieldName) {
		this(parent, parent.getValueType(), javaFieldName);
	}

	@NotNull
	private static <T> Field getJavaField(Class<T> javaFieldClass, String javaFieldName) {
		try {
			return javaFieldClass.getDeclaredField(javaFieldName);
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("La classe %s ne possède pas de champ %s", javaFieldClass, javaFieldName));
		}
	}

	@Nullable
	@Override
	public String getLocalName() {
		final JsonProperty jsonProperty = javaField.getAnnotation(JsonProperty.class);
		if (jsonProperty != null && StringUtils.isNotEmpty(jsonProperty.value())) {
			return jsonProperty.value();
		} else {
			return javaField.getName();
		}
	}

	@Override
	public Class<?> getType() {
		return javaField.getType();
	}

	@Override
	public Class<?> getValueType() {
		final Class<?> type = getType();
		if (List.class.isAssignableFrom(type)) {
			return (Class<?>) getListItemType(javaField);
		} else {
			return type;
		}
	}

	private static Type getListItemType(@NotNull Field javaField) {
		final ParameterizedType parameterizedType = (ParameterizedType) javaField.getGenericType();
		return parameterizedType.getActualTypeArguments()[0];
	}

	@Override
	@Nullable
	public String getDescription() {
		final Class<?> javaFieldDeclaringClass = javaField.getDeclaringClass();
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(javaFieldDeclaringClass).getPropertyDescriptors()) {
				final Method getter = pd.getReadMethod();
				if (getter != null && pd.getName().equals(javaField.getName())) {
					final Schema schema = getter.getAnnotation(Schema.class);
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
		if (this == o) return true;
		if (!(o instanceof FieldSpecFromJavaField)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}

















