package org.rudi.facet.apimaccess.api.registration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientAccessKey implements ClientRegistrationResponse {
    private String clientId;
    private String clientSecret;
}
