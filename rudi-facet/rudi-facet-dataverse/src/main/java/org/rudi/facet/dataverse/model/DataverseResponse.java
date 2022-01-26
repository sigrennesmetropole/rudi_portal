package org.rudi.facet.dataverse.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataverseResponse <T> extends ApiResponseInfo {

    private T data;
}
