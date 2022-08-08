package org.rudi.tools.nodestub.service;

import org.rudi.facet.kaccess.bean.Metadata;

import java.time.OffsetDateTime;
import java.util.List;

public interface ResourcesService {
	List<Metadata> getMetadataList(int limit, int offset, OffsetDateTime updateAfter);
}
