package org.rudi.common.core.json;

import java.util.List;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtils {

	public static NamedType[] namedTypesWithSimpleNames(List<Class<?>> subtypes) {
		return subtypes.stream().map(subtype -> new NamedType(subtype, subtype.getSimpleName()))
				.toArray(NamedType[]::new);
	}
}
