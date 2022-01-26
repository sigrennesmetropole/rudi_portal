package org.rudi.facet.providers.helper;

import lombok.Data;
import org.rudi.facet.providers.bean.NodeProvider;

import java.util.UUID;

@Data
public class NodeWithProviderUuid {
	private final UUID providerUuid;
	private final NodeProvider node;
}
