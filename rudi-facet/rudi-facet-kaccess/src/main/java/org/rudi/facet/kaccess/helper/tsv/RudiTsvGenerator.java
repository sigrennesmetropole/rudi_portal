package org.rudi.facet.kaccess.helper.tsv;

import org.rudi.facet.dataverse.helper.tsv.TsvGenerator;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.RUDI_ELEMENT_SPEC;

public class RudiTsvGenerator extends TsvGenerator {

	RudiTsvGenerator() {
		super(RUDI_ELEMENT_SPEC, "Rudi Metadata");
	}

}
