package org.rudi.facet.apimaccess.helper.rest;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClientAccessPayload {

    private String callbackUrl;
    private String clientName;
    private String owner;
    private String grantType;
    private boolean saasApp;
}
