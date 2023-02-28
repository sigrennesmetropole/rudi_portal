package org.rudi.facet.dataverse.helper.tsv;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
class Tsv {
	final TsvPart<TsvMetadataBlockLine> metadataBlock;
	final TsvPart<TsvDatasetFieldLine> datasetField;
	final TsvPart<TsvControlledVocabularyLine> controlledVocabulary;
}
