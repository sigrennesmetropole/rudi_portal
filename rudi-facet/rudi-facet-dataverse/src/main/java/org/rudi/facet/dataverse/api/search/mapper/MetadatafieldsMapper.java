package org.rudi.facet.dataverse.api.search.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.springframework.stereotype.Component;

/**
 * @see <a href="https://github.com/IQSS/dataverse/issues/7863">GitHub Merge Request</a>
 */
@Component
public class MetadatafieldsMapper {

	private static final String SEPARATOR = ":";
	private static final String ALL_FIELDS = "*";

	public Set<String> map(DatasetMetadataBlockElementSpec blockElementSpec, List<FieldSpec> metadatafields) {
		return metadatafields.stream()
				.map(fieldSpec -> getMetadatafield(blockElementSpec, fieldSpec))
				.collect(Collectors.toSet());
	}

	private String getMetadatafield(final DatasetMetadataBlockElementSpec blockElementSpec, final FieldSpec fieldSpec) {

		final FieldSpec rootSpec = blockElementSpec.getRoot();
		if (fieldSpec == rootSpec) {
			return rootSpec + SEPARATOR + ALL_FIELDS;
		}

		final var stringBuilder = new StringBuilder();
		stringBuilder.append(rootSpec).append(SEPARATOR);

		final FieldSpec parentSpec = blockElementSpec.getDirectParentOf(fieldSpec);
		if (parentSpec != null && parentSpec.isCompound()) {
			stringBuilder.append(parentSpec.getName());
		} else {
			stringBuilder.append(fieldSpec.getName());
		}

		return stringBuilder.toString();
	}
}
