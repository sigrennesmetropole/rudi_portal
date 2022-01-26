package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.GeoJsonObject;
import org.rudi.facet.kaccess.bean.MetadataGeography;
import org.rudi.facet.kaccess.bean.MetadataGeographyBoundingBox;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_BOUNDING_BOX_EAST_LONGITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_BOUNDING_BOX_NORTH_LATITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_BOUNDING_BOX_SOUTH_LATITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_BOUNDING_BOX_WEST_LONGITUDE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_GEOGRAPHIC_DISTRIBUTION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY_PROJECTION;

@Component
class GeographyPrimitiveFieldsMapper extends PrimitiveFieldsMapper<MetadataGeography> {

	GeographyPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper) {
		super(fieldGenerator, objectMapper);
	}

	@Override
	public void metadataToFields(MetadataGeography geography, Map<String, Object> fields) throws DataverseMappingException {
		// propriétés de BoundingBox
		final MetadataGeographyBoundingBox boundingBox = geography.getBoundingBox();
		createField(GEOGRAPHY_BOUNDING_BOX_WEST_LONGITUDE, boundingBox.getWestLongitude(), fields);
		createField(GEOGRAPHY_BOUNDING_BOX_EAST_LONGITUDE, boundingBox.getEastLongitude(), fields);
		createField(GEOGRAPHY_BOUNDING_BOX_NORTH_LATITUDE, boundingBox.getNorthLatitude(), fields);
		createField(GEOGRAPHY_BOUNDING_BOX_SOUTH_LATITUDE, boundingBox.getSouthLatitude(), fields);

		// geographic_distribution
		createField(GEOGRAPHY_GEOGRAPHIC_DISTRIBUTION, geography.getGeographicDistribution(), fields);

		// geography_projection
		createField(GEOGRAPHY_PROJECTION, geography.getProjection(), fields);
	}

	@Nonnull
	@Override
	public MetadataGeography fieldToMetadata(@Nonnull Field geographyField) throws DataverseMappingException {
		final MetadataGeography metadataGeography = new MetadataGeography()
				.boundingBox(new MetadataGeographyBoundingBox()
						.eastLongitude(requireNonNull(geographyField.get(GEOGRAPHY_BOUNDING_BOX_EAST_LONGITUDE)).getValueAsBigDecimal())
						.westLongitude(requireNonNull(geographyField.get(GEOGRAPHY_BOUNDING_BOX_WEST_LONGITUDE)).getValueAsBigDecimal())
						.northLatitude(requireNonNull(geographyField.get(GEOGRAPHY_BOUNDING_BOX_NORTH_LATITUDE)).getValueAsBigDecimal())
						.southLatitude(requireNonNull(geographyField.get(GEOGRAPHY_BOUNDING_BOX_SOUTH_LATITUDE)).getValueAsBigDecimal()));

		final Field distributionField = geographyField.get(GEOGRAPHY_GEOGRAPHIC_DISTRIBUTION);
		if (distributionField != null) {
			metadataGeography.geographicDistribution(distributionField.getValueAs(GeoJsonObject.class, objectMapper));
		}

		final Field projectionField = geographyField.get(GEOGRAPHY_PROJECTION);
		if (projectionField != null) {
			metadataGeography.projection(projectionField.getValueAsString());
		}

		return metadataGeography;
	}

	@Nullable
	@Override
	public MetadataGeography defaultMetadata() {
		return null;
	}

}
