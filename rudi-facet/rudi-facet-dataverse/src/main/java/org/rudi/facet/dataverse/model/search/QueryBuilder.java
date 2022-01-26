package org.rudi.facet.dataverse.model.search;

import org.rudi.facet.dataverse.fields.FieldSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class QueryBuilder {

	private final StringBuilder query = new StringBuilder();

	public QueryBuilder add(FieldSpec fieldSpec, @Nullable Object value) {
		if (value != null) {
			query.append(fieldSpec.getName()).append(":").append(mapValue(value));
		}
		return this;
	}

	private static <T> String mapValue(@Nonnull T object) {
		if (object instanceof UUID) {
			return mapValue((UUID) object);
		}
		return object.toString();
	}

	private static String mapValue(@Nonnull UUID uuid) {
		return "\"" + uuid + "\"";
	}

	public String build() {
		// q est obligatoire dans la requete solr
		if (query.length() == 0) {
			query.append("*");
		}
		return query.toString();
	}
}
