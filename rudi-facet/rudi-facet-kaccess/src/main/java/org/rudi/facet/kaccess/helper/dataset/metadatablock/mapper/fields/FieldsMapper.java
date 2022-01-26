package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Une contrainte DataVerse oblige à ne définir qu'un seul niveau d'imbrication pour les champs de type compound.
 * Cette classe permet de séparer clairement la génération :
 *
 * <ol>
 *     <li>au niveau 1, d'un champ "compound" parent</li>
 *     <li>au niveau 2, de tous les champs primitifs, directement sous le champ précédent</li>
 *     <li>au niveau 1, de tous les autres champs qui ne peuvent pas se trouver au niveau 2 car de type "compound"</li>
 * </ol>
 *
 * @param <T> type de l'élément Metadata correspondant au champ parent DataVerse
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class FieldsMapper<T> {
	// Privé pour éviter que les classes concrètes n'utilisent di
	private final FieldGenerator fieldGenerator;
	final FieldSpec parentOfPrimitiveFieldsSpec;
	final PrimitiveFieldsMapper<T> primitiveFieldsMapper;

	/**
	 * CompoundFieldsMapper may be null if there are no compound fields other than the unique parent of primitive fields
	 */
	@Nullable
	final CompoundFieldsMapper<T> compoundFieldsMapper;

	FieldsMapper(FieldGenerator fieldGenerator, FieldSpec parentOfPrimitiveFieldsSpec, PrimitiveFieldsMapper<T> primitiveFieldsMapper) {
		this.fieldGenerator = fieldGenerator;
		this.parentOfPrimitiveFieldsSpec = parentOfPrimitiveFieldsSpec;
		this.primitiveFieldsMapper = primitiveFieldsMapper;
		this.compoundFieldsMapper = null;
	}

	public final void metadataToFields(Metadata metadata, List<DatasetMetadataBlockElementField> fields) throws DataverseMappingException {
		final T metadataElement = getMetadataElement(metadata);

		if (metadataElement != null) {
			// Niveau 1 => on peut choisir un type compound pour le parent mais pas pour les fils
			final DatasetMetadataBlockElementField parentOfPrimitiveFields = fieldGenerator.generateField(parentOfPrimitiveFieldsSpec, primitiveFieldsMapper.metadataToFields(metadataElement));
			fields.add(parentOfPrimitiveFields);

			// Niveau 2 => type compound interdit => on doit remonter les champs compound au niveau 1
			if (compoundFieldsMapper != null) {
				compoundFieldsMapper.metadataToFields(metadataElement, fields);
			}
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

	public final void fieldsToMetadata(RootFields rudiRootFields, Metadata metadata) throws DataverseMappingException {
		final Field primitiveField = rudiRootFields.get(parentOfPrimitiveFieldsSpec);

		final T childMetadata;
		if (primitiveField == null) {
			childMetadata = primitiveFieldsMapper.defaultMetadata();
		} else {
			childMetadata = primitiveFieldsMapper.fieldToMetadata(primitiveField);
		}
		setMetadataElement(metadata, childMetadata);

		if (compoundFieldsMapper != null && primitiveField != null) {
			compoundFieldsMapper.fieldsToMetadata(rudiRootFields, metadata);
		}
	}
}
