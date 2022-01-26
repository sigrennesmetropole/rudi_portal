package org.rudi.facet.dataverse.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchItemFacets {

    @JsonProperty("friendly")
    private String friendly;

    @JsonProperty("labels")
    private List<Map<String, Integer>> labels;
}
