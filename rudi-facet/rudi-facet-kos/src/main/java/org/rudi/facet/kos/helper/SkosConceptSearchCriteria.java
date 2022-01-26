package org.rudi.facet.kos.helper;

import lombok.Builder;
import lombok.Data;
import org.intellij.lang.annotations.Language;
import org.rudi.microservice.kos.core.bean.SkosConceptLabel;
import org.rudi.microservice.kos.core.bean.SkosRelationType;

import java.util.List;

@Data
@Builder
public class SkosConceptSearchCriteria {
	private final Integer limit;
	private final Integer offset;
	private final String order;
	private final Language lang;
	private final String text;
	private final List<SkosRelationType> types;
	private final List<String> roles;
	private final List<String> codes;
	private final List<String> schemes;
	private final List<SkosConceptLabel> labels;
}
