package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

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
public abstract class AbstractFieldsMapper<T> {
	protected final FieldGenerator fieldGenerator;
	static final int DEFAULT_RANK = 0;
	public final FieldSpec parentOfPrimitiveFieldsSpec;
	final PrimitiveFieldsMapper<T> primitiveFieldsMapper;

	/**
	 * CompoundFieldsMapper may be null if there are no compound fields other than the unique parent of primitive fields
	 */
	@Nullable
	final CompoundFieldsMapper<T> compoundFieldsMapper;

	AbstractFieldsMapper(FieldGenerator fieldGenerator, FieldSpec parentOfPrimitiveFieldsSpec, PrimitiveFieldsMapper<T> primitiveFieldsMapper) {
		this.fieldGenerator = fieldGenerator;
		this.parentOfPrimitiveFieldsSpec = parentOfPrimitiveFieldsSpec;
		this.primitiveFieldsMapper = primitiveFieldsMapper;
		this.compoundFieldsMapper = null;
	}

	public abstract void metadataToFields(Metadata metadata, List<DatasetMetadataBlockElementField> fields) throws DataverseMappingException;

	public abstract void fieldsToMetadata(RootFields rudiRootFields, Metadata metadata) throws DataverseMappingException;

	/**
	 * @return rang permettant de trier les mappers pour qu'ils s'exécutent dans un ordre croissant de rangs.
	 */
	public int getRank() {
		return DEFAULT_RANK;
	}
}
