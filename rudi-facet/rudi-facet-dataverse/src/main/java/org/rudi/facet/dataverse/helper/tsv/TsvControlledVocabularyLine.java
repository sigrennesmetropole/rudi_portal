package org.rudi.facet.dataverse.helper.tsv;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;

/**
 * @see <a href="https://guides.dataverse.org/en/latest/admin/metadatacustomization.html#controlledvocabulary-enumerated-properties">Documentation
 *      Dataverse</a>
 */
@Builder
class TsvControlledVocabularyLine extends TsvLine {
	public static final Comparator<TsvControlledVocabularyLine> COMPARATOR = Comparator
			.<TsvControlledVocabularyLine, String>comparing(line -> line.datasetField)
			.thenComparing(line -> line.value);

	/**
	 * Specifies the #datasetField to which #datasetField to which this entry applies.
	 * <p>
	 * Must reference an existing #datasetField. As a best practice, the value should reference a #datasetField in the current metadata block definition.
	 * (It is technically possible to reference an existing #datasetField from another metadata block.)
	 */
	final String datasetField;

	/**
	 * A short display string, representing an enumerated value for this field. If the identifier property is empty, this value is used as the identifier.
	 */
	final String value;

	/**
	 * A string used to encode the selected enumerated value of a field. If this property is empty, the value of the “Value” field is used as the
	 * identifier.
	 */
	final String identifier;

	/**
	 * Control the order in which the enumerated values are displayed for selection.
	 * <p>
	 * Non-negative integer.
	 */
	final int displayOrder;

	@Override
	public String toString() {
		return StringUtils.joinWith("\t", firstColumn, datasetField, value, identifier, displayOrder);
	}

	@Override
	protected String getHashString() {
		return datasetField + ":" + value;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return obj instanceof TsvControlledVocabularyLine;
	}
}
