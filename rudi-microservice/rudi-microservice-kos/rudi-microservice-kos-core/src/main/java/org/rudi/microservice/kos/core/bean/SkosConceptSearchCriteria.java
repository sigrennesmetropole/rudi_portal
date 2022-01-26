package org.rudi.microservice.kos.core.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SkosConceptSearchCriteria {

    private Language lang;

    private String text;

    private List<SkosRelationType> types;

    private List<String> roles;

    private List<String> codes;

    private List<String> codesScheme;

    private List<SkosConceptLabel> labels;
}
