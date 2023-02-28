package org.rudi.common.core.json;

import java.util.List;

import com.fasterxml.jackson.databind.jsontype.NamedType;

public class ObjectMapperUtils {
	private ObjectMapperUtils() {
	}

	public static NamedType[] namedTypesWithSimpleNames(List<Class<?>> subtypes) {
		return subtypes.stream()
				.map(subtype -> new NamedType(subtype, subtype.getSimpleName()))
				.toArray(NamedType[]::new);
	}
}
