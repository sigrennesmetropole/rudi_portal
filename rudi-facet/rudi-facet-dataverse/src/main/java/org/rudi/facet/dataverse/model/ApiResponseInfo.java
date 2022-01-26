package org.rudi.facet.dataverse.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiResponseInfo implements Serializable {

    private String status;
    private String message;
}
