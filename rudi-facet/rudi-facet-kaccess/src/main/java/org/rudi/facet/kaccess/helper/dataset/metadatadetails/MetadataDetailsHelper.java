package org.rudi.facet.kaccess.helper.dataset.metadatadetails;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.springframework.stereotype.Component;

@Component
public class MetadataDetailsHelper {

    public boolean isRestricted(Metadata metadata) {
        final MetadataAccessConditionConfidentiality confidentiality = metadata.getAccessCondition().getConfidentiality();
        return confidentiality != null && Boolean.TRUE.equals(confidentiality.getRestrictedAccess());
    }
}
