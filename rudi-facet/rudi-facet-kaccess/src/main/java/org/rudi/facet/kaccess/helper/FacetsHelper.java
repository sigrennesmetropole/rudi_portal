package org.rudi.facet.kaccess.helper;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.MetadataFacetValues;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FacetsHelper {

	public List<MetadataFacetValues> getValues(FieldSpec requestedField, MetadataListFacets metadataListFacets) {
		final var requestedFacetPropertyName = requestedField.getFacet();
		final var facets = metadataListFacets.getFacets().getItems();
		for (final var facet : facets) {
			if (facet.getPropertyName().equals(requestedFacetPropertyName)) {
				return facet.getValues();
			}
		}
		return Collections.emptyList();
	}
}
