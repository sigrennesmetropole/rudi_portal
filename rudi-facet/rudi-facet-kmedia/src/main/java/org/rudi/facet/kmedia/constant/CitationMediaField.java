package org.rudi.facet.kmedia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.rudi.facet.dataverse.constant.CitationMetadataField;
import org.rudi.facet.dataverse.fields.FieldSpec;

/**
 * Identifiants des champs du bloc citation du dataverse Rudi Media
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CitationMediaField {

	public static final FieldSpec KIND_OF_DATA = CitationMetadataField.ROOT.newChildFromJavaField("kindOfData");
}
