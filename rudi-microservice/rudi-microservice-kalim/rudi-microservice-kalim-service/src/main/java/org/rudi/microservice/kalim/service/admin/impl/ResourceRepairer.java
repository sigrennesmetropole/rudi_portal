package org.rudi.microservice.kalim.service.admin.impl;

import org.rudi.facet.dataverse.bean.DatasetVersion;

/**
 * Répare des JDD qui ne sont plus lisibles par les mappers RUDI et dont il faut réparer manuellement les champs
 */
interface ResourceRepairer {
	String getQuery();

	void repairResource(DatasetVersion datasetVersion);
}
