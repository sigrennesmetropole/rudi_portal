package org.rudi.microservice.selfdata.storage.dao.selfdatadataset;

import java.util.List;
import java.util.UUID;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfdataDatasetCustomSearchCriteria {
	List<UUID> datasetUuids;
	String login;
}
