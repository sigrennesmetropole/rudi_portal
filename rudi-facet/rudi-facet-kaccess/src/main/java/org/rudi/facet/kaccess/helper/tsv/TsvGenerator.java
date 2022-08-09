package org.rudi.facet.kaccess.helper.tsv;

import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.Language;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.RUDI_ELEMENT_SPEC;

public class TsvGenerator {
	public static void main(String[] args) {
		final var tsvGenerator = new TsvGenerator();
		tsvGenerator.generate(RUDI_ELEMENT_SPEC);
	}

	private void generate(DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec) {
		final var root = datasetMetadataBlockElementSpec.getRoot();
		datasetMetadataBlockElementSpec.stream()
				.flatMap(parent -> Stream.concat(Stream.of(parent), datasetMetadataBlockElementSpec.streamChildrenOf(parent)))
				.map(spec -> TsvLine.builder()
						.name(spec.getName())
						.title(spec.getName())
						.description(spec.getDescription())
						.fieldType(spec.getChildren().isEmpty() ? "text" : "none")
						.displayFormat(spec.getChildren().isEmpty() ? "#VALUE" : ";")
						.advancedSearchField(spec.getChildren().isEmpty())
						.allowControlledVocabulary(allowControlledVocabulary(spec))
						.allowmultiples(spec.isMultiple())
						.facetable(spec.getType().isEnum())
						.displayoncreate(true)
						.required(spec.isRequired())
						.parent(getParent(datasetMetadataBlockElementSpec, root, spec))
						.metadatablock_id(root.getName())
						.build())
				.map(TsvLine::toString)
				.forEach(System.out::println);
	}

	private static boolean allowControlledVocabulary(FieldSpec spec) {
		final Class<?> type = spec.getType();
		return type.isEnum() && type != Language.class;
	}

	@Nullable
	private String getParent(DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec, FieldSpec root, FieldSpec spec) {
		final var mappedParent = getMappedParent(spec, datasetMetadataBlockElementSpec);
		return mappedParent != null && mappedParent != root ? mappedParent.getName() : null;
	}

	// Tous les champs ne sont pas mappés en particulier les champs intermédiaire (qui ne sont ni racine, ni feuille)
	@Nullable
	private static FieldSpec getMappedParent(FieldSpec spec, DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec) {
		FieldSpec parent = datasetMetadataBlockElementSpec.getLevel1ParentOf(spec);
		if (parent == null) {
			return null;
		}

		final var root = datasetMetadataBlockElementSpec.getRoot();
		if (parent != root) {
			FieldSpec nextParent;
			while ((nextParent = datasetMetadataBlockElementSpec.getParentOf(parent)) != null && nextParent != root) {
				parent = nextParent;
			}
		}
		return parent;
	}
}
