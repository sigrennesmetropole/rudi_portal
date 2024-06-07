package org.rudi.facet.dataverse.helper.tsv;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.FieldType;

import lombok.Builder;
import lombok.Getter;

/**
 * @see <a href="https://guides.dataverse.org/en/latest/admin/metadatacustomization.html#datasetfield-field-properties">Documentation Dataverse</a>
 */
@Builder(toBuilder = true)
class TsvDatasetFieldLine extends TsvLine {
	/**
	 * A user-definable string used to identify a #datasetField. Maps directly to field name used by Solr.
	 *
	 * <ul>
	 *  <li>(from DatasetFieldType.java) The internal DDI-like name, no spaces, etc.</li>
	 * 	<li>(from Solr) Field names should consist of alphanumeric or underscore characters only and not start with a digit. This is not currently strictly enforced, but other field names will not have first class support from all components and back compatibility is not guaranteed. Names with both leading and trailing underscores (e.g. _version_) are reserved.</li>
	 * 	<li>Must not collide with a field of the same same name in another #metadataBlock definition or any name already included as a field in the Solr index.</li>
	 * </ul>
	 */
	final String name;

	/**
	 * Acts as a brief label for display related to this #datasetField.
	 * <p>
	 * Should be relatively brief.
	 */
	final String title;

	/**
	 * Used to provide a description of the field.
	 */
	final String description;

	/**
	 * A string to initially display in a field as a prompt for what the user should enter.
	 */
	final String watermark;

	/**
	 * Defines the type of content that the field, if not empty, is meant to contain.
	 */
	final FieldType fieldType;

	/**
	 * Controls the sequence in which the fields are displayed, both for input and presentation.
	 * <p>
	 * Non-negative integer.
	 */
	@Getter
	final Integer displayOrder;

	/**
	 * Controls how the content is displayed for presentation (not entry). The value of this field may contain one or more special variables (enumerated below). HTML tags, likely in conjunction with one or more of these values, may be used to control the display of content in the web UI.
	 */
	final String displayFormat;

	/**
	 * Specify whether this field is available in advanced search.
	 */
	final Boolean advancedSearchField;

	/**
	 * Specify whether the possible values of this field are determined by values in the #controlledVocabulary section.
	 */
	final Boolean allowControlledVocabulary;

	/**
	 * Specify whether this field is repeatable.
	 */
	final Boolean allowmultiples;

	/**
	 * Specify whether the field is facetable (i.e., if the expected values for this field are themselves useful search terms for this field). If a field is “facetable” (able to be faceted on), it appears under “Browse/Search Facets” when you edit “General Information” for a Dataverse collection. Setting this value to TRUE generally makes sense for enumerated or controlled vocabulary fields, fields representing identifiers (IDs, names, email addresses), and other fields that are likely to share values across entries. It is less likely to make sense for fields containing descriptions, floating point numbers, and other values that are likely to be unique.
	 */
	final Boolean facetable;

	/**
	 * Designate fields that should display during the creation of a new dataset, even before the dataset is saved. Fields not so designated will not be displayed until the dataset has been saved.
	 */
	final Boolean displayoncreate;

	/**
	 * For primitive fields, specify whether or not the field is required.
	 * <p>
	 * For compound fields, also specify if one or more subfields are required or conditionally required. At least one instance of a required field must be present. More than one instance of a field may be allowed, depending on the value of allowmultiples.
	 * <p>
	 * For primitive fields, TRUE (required) or FALSE (optional).
	 * <p>
	 * For compound fields:
	 *
	 * <ul>
	 *     <li>To make one or more subfields optional, the parent field and subfield(s) must be FALSE (optional).</li>
	 *     <li>To make one or more subfields required, the parent field and the required subfield(s) must be TRUE (required).</li>
	 *     <li>To make one or more subfields conditionally required, make the parent field FALSE (optional) and make TRUE (required) any subfield or subfields that are required if any other subfields are filled.</li>
	 * </ul>
	 */
	final Boolean required;

	/**
	 * For subfields, specify the name of the parent or containing field.
	 * <ul>
	 *     <li>Must not result in a cyclical reference.</li>
	 *     <li>Must reference an existing field in the same #metadataBlock.</li>
	 * </ul>
	 */
	final String parent;

	/**
	 * Specify the name of the #metadataBlock that contains this field.
	 * <ul>
	 * <li>Must reference an existing #metadataBlock.</li>
	 * <li>As a best practice, the value should reference the #metadataBlock in the current definition (it is technically possible to reference another existing metadata block.)</li>
	 * </ul>
	 */
	final String metadatablockId;

	/**
	 * Specify a global URI identifying this term in an external community vocabulary.
	 * <p>
	 * This value overrides the default (created by appending the property name to the blockURI defined for the #metadataBlock)
	 * <p>
	 * For example, the existing citation #metadataBlock defines the property named ‘title’ as <a href="http://purl.org/dc/terms/title">http://purl.org/dc/terms/title</a> - i.e. indicating that it can be interpreted as the Dublin Core term ‘title’
	 */
	final String termURI;

	@Override
	public String toString() {
		return StringUtils.joinWith("\t",
				firstColumn,
				name,
				title,
				description,
				ObjectUtils.firstNonNull(watermark, StringUtils.EMPTY),
				fieldType,
				displayOrder,
				displayFormat,
				format(advancedSearchField),
				format(allowControlledVocabulary),
				format(allowmultiples),
				format(facetable),
				format(displayoncreate),
				format(required),
				parent,
				metadatablockId,
				termURI);
	}

	@Override
	protected String getHashString() {
		return name;
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
		return obj instanceof TsvDatasetFieldLine;
	}

}
