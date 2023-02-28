package org.rudi.facet.dataverse.helper.tsv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.rudi.facet.dataverse.bean.FieldType;
import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TsvGenerator {
	protected final DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec;
	protected final String metadataBlockDisplayName;

	public Tsv generate() {

		return Tsv.builder()
				.metadataBlock(new TsvPart<>(
						Collections.singletonList(
								TsvMetadataBlockLine.builder()
										.name(datasetMetadataBlockElementSpec.getRoot().getName())
										.displayName(metadataBlockDisplayName)
										.build()
						))
				)
				.datasetField(new TsvPart<>(
						generateDatasetFieldLines()
				))
				.controlledVocabulary(new TsvPart<>(
						generateControlledVocabularyLines()
				))
				.build();

	}

	private List<TsvDatasetFieldLine> generateDatasetFieldLines() {
		final var root = datasetMetadataBlockElementSpec.getRoot();
		return datasetMetadataBlockElementSpec.streamLevel1Fields()
				// Tous les champs ne sont pas mappés en particulier les champs intermédiaires (qui ne sont ni racine, ni feuille)
				.flatMap(parent -> Stream.concat(Stream.of(parent), datasetMetadataBlockElementSpec.streamChildrenOf(parent)))
				.map(spec -> {
					final var parentSpec = getParentSpec(datasetMetadataBlockElementSpec, root, spec);
					return TsvDatasetFieldLine.builder()
							.name(spec.getName())
							.title(spec.getName())
							.description(spec.getDescription())
							.fieldType(getFieldType(spec))
							.displayFormat(spec.getDirectChildren().isEmpty() ? "#VALUE" : ";")
							.advancedSearchField(spec.getDirectChildren().isEmpty())
							.allowControlledVocabulary(spec.allowControlledVocabulary())
							.allowmultiples(spec.isMultiple())
							.facetable(spec.getJavaType().isEnum())
							.displayoncreate(true)
							/*
							 * Un champ obligatoire dans le JSON ne peut pas l'être dans Dataverse dans les cas suivants :
							 *
							 * - Son parent dans le JSON est obligatoire, mais pas son parent dans Dataverse (exemple : <code>rudi_access_condition_usage_constraint_lang</code>)
							 * - Un de ses parents est polymorphe (plusieurs types concrets possibles héritant d'un même type abstrait)
							 *
							 * Pour simplifier on ne met aucun champ required côté Dataverse.
							 */
							.required(false)
							.parent(parentSpec != null ? parentSpec.getName() : null)
							.metadatablockId(root.getName())
							.build();
				})
				.collect(Collectors.toList());
	}

	@Nonnull
	private FieldType getFieldType(FieldSpec spec) {
		if (!spec.getDirectChildren().isEmpty()) {
			return FieldType.NONE;
		}
		if (spec.getValueType().isAssignableFrom(Integer.class) || spec.getValueType().isAssignableFrom(Long.class)) {
			return FieldType.INT;
		}
		if (spec.getValueType().isAssignableFrom(BigDecimal.class) ||
				spec.getValueType().isAssignableFrom(java.time.OffsetDateTime.class)) {
			return FieldType.FLOAT;
		}
		if (spec.getLocalName().endsWith("email")) {
			return FieldType.EMAIL;
		}
		if (spec.getLocalName().endsWith("uri")) {
			return FieldType.URL;
		}
		if (spec.getLocalName().endsWith("text") || spec.getLocalName().endsWith("address")) {
			return FieldType.TEXTBOX;
		}
		return FieldType.TEXT;
	}

	private List<TsvControlledVocabularyLine> generateControlledVocabularyLines() {
		return datasetMetadataBlockElementSpec.streamAllFields()
				.filter(FieldSpec::allowControlledVocabulary)
				.flatMap(fieldSpec -> {
					final Object[] enumConstants = fieldSpec.getValueType().getEnumConstants();
					final List<TsvControlledVocabularyLine> fieldLines = new ArrayList<>(enumConstants.length);
					for (var displayOrder = 1; displayOrder <= enumConstants.length; displayOrder++) {
						final var enumConstant = enumConstants[displayOrder - 1].toString();
						final var fieldLine = TsvControlledVocabularyLine.builder()
								.datasetField(fieldSpec.getName())
								.value(enumConstant)
								.displayOrder(displayOrder)
								.build();
						fieldLines.add(fieldLine);
					}
					return fieldLines.stream();
				})
				.collect(Collectors.toList());
	}

	@Nullable
	private FieldSpec getParentSpec(DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec, FieldSpec root, FieldSpec spec) {
		final var mappedParent = getMappedParent(spec, datasetMetadataBlockElementSpec);
		return mappedParent != null && mappedParent != root ? mappedParent : null;
	}

	@Nullable
	private static FieldSpec getMappedParent(FieldSpec spec, DatasetMetadataBlockElementSpec datasetMetadataBlockElementSpec) {
		return datasetMetadataBlockElementSpec.getLevel1ParentOf(spec);
	}

}
