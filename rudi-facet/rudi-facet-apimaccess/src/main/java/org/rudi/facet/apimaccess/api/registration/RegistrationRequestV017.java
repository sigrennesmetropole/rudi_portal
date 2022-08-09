package org.rudi.facet.apimaccess.api.registration;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegistrationRequestV017 implements RegistrationRequest {

    private String callbackUrl;
    private String clientName;
    private String owner;
    private String grantType;
    private boolean saasApp;
}
