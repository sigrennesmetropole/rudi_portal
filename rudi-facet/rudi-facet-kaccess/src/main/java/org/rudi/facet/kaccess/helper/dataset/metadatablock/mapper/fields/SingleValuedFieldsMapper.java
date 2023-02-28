package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

/**
 * Mapper pour un champ non multi valué (c'est-à-dire avec isMultiple à false)
 */
abstract class SingleValuedFieldsMapper<T> extends AbstractFieldsMapper<T> {
	SingleValuedFieldsMapper(FieldGenerator fieldGenerator, FieldSpec parentOfPrimitiveFieldsSpec, PrimitiveFieldsMapper<T> primitiveFieldsMapper) {
		this(fieldGenerator, parentOfPrimitiveFieldsSpec, primitiveFieldsMapper, null);
	}

	SingleValuedFieldsMapper(FieldGenerator fieldGenerator, FieldSpec parentOfPrimitiveFieldsSpec, PrimitiveFieldsMapper<T> primitiveFieldsMapper, @Nullable CompoundFieldsMapper<T> compoundFieldsMapper) {
		super(fieldGenerator, parentOfPrimitiveFieldsSpec, primitiveFieldsMapper, compoundFieldsMapper);
		check(parentOfPrimitiveFieldsSpec);
	}

	private void check(FieldSpec parentOfPrimitiveFieldsSpec) {
		if (parentOfPrimitiveFieldsSpec.isMultiple()) {
			throw new IllegalArgumentException("Cannot use SingleValuedFieldsMapper on a multiple valued field");
		}
	}

	public final void metadataToFields(Metadata metadata, List<DatasetMetadataBlockElementField> fields) throws DataverseMappingException {
		final var metadataElement = getMetadataElement(metadata);

		if (metadataElement != null) {
			// Niveau 1 => on peut choisir un type compound pour le parent mais pas pour les fils
			final var parentOfPrimitiveFieldsValue = primitiveFieldsMapper.metadataToFields(metadataElement);
			final DatasetMetadataBlockElementField parentOfPrimitiveFields = fieldGenerator.generateField(parentOfPrimitiveFieldsSpec, parentOfPrimitiveFieldsValue);
			fields.add(parentOfPrimitiveFields);

			// Niveau 2 => type compound interdit => on doit remonter les champs compound au niveau 1
			if (compoundFieldsMapper != null) {
				compoundFieldsMapper.metadataToFields(metadataElement, fields);
			}
		}
	}

	public final void fieldsToMetadata(RootFields rudiRootFields, Metadata metadata) throws DataverseMappingException {
		final var primitiveField = rudiRootFields.get(parentOfPrimitiveFieldsSpec);

		if (primitiveField == null) {
			final var defaultChildMetadata = primitiveFieldsMapper.defaultMetadata();
			if (defaultChildMetadata != null) {
				setMetadataElement(metadata, defaultChildMetadata);
			}
		} else {
			final var childMetadata = primitiveFieldsMapper.fieldsToMetadata(primitiveField.getValueAsMapOfFields());
			setMetadataElement(metadata, childMetadata);
		}

		if (compoundFieldsMapper != null && primitiveField != null) {
			compoundFieldsMapper.fieldsToMetadata(rudiRootFields, metadata);
		}
	}

	@Nullable
	abstract T getMetadataElement(Metadata metadata);

	/**
	 * Ajoute les métadonnées qui viennent d'être mappées par ce mapper aux métadonnées précédemment mappées.
	 *
	 * @param childMetadata métadonnées qui viennent d'être mappées par ce mapper
	 * @param metadata      métadonnées précédemment mappées
	 */
	abstract void setMetadataElement(@Nonnull Metadata metadata, @Nonnull T childMetadata);

}
