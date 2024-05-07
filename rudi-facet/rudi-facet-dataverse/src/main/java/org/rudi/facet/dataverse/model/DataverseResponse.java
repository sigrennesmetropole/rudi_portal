package org.rudi.facet.dataverse.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataverseResponse <T extends Serializable> extends ApiResponseInfo {

    private T data;
}
