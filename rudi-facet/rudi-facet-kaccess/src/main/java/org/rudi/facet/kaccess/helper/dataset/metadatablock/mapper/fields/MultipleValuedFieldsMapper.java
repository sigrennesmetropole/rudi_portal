package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mapper pour un champ multi valué (c'est-à-dire avec isMultiple à true)
 */
abstract class MultipleValuedFieldsMapper<T> extends AbstractFieldsMapper<T> {

	MultipleValuedFieldsMapper(FieldGenerator fieldGenerator, FieldSpec parentOfPrimitiveFieldsSpec, PrimitiveFieldsMapper<T> primitiveFieldsMapper) {
		super(fieldGenerator, parentOfPrimitiveFieldsSpec, primitiveFieldsMapper);
		check(parentOfPrimitiveFieldsSpec);
	}

	private void check(FieldSpec parentOfPrimitiveFieldsSpec) {
		if (!parentOfPrimitiveFieldsSpec.isMultiple()) {
			throw new IllegalArgumentException("Cannot use MultipleValuedFieldsMapper on a non multiple valued field");
		}
	}

	public final void metadataToFields(Metadata metadata, List<DatasetMetadataBlockElementField> fields) throws DataverseMappingException {
		final var metadataElements = getMetadataElements(metadata);

		if (metadataElements != null) {
			final List<Map<String, Object>> parentOfPrimitiveFieldsValue = new ArrayList<>();

			for (final var metadataElement : metadataElements) {
				// Niveau 1 => on peut choisir un type compound pour le parent mais pas pour les fils
				final var metadataElementValue = primitiveFieldsMapper.metadataToFields(metadataElement);
				parentOfPrimitiveFieldsValue.add(metadataElementValue);
			}

			final DatasetMetadataBlockElementField parentOfPrimitiveFields = fieldGenerator.generateField(parentOfPrimitiveFieldsSpec, parentOfPrimitiveFieldsValue);
			if (parentOfPrimitiveFields != null) {
				fields.add(parentOfPrimitiveFields);
			}
		}
	}

	public final void fieldsToMetadata(RootFields rudiRootFields, Metadata metadata) throws DataverseMappingException {
		final var parentOfPrimitiveFields = rudiRootFields.get(parentOfPrimitiveFieldsSpec);
		if (parentOfPrimitiveFields != null) {
			final var multipleFieldValue = parentOfPrimitiveFields.getValueAsMultipleFieldValue();

			final List<T> childrenMetadata = new ArrayList<>();
			for (final var fields : multipleFieldValue) {
				final var childMetadata = primitiveFieldsMapper.fieldsToMetadata(fields);
				childrenMetadata.add(childMetadata);
			}

			setMetadataElements(metadata, childrenMetadata);
		}
	}

	@Nullable
	abstract List<T> getMetadataElements(Metadata metadata);

	/**
	 * Ajoute les métadonnées qui viennent d'être mappées par ce mapper aux métadonnées précédemment mappées.
	 *
	 * @param childrenMetadata métadonnées qui viennent d'être mappées par ce mapper
	 * @param metadata         métadonnées précédemment mappées
	 */
	abstract void setMetadataElements(@Nonnull Metadata metadata, @Nonnull List<T> childrenMetadata);

}
