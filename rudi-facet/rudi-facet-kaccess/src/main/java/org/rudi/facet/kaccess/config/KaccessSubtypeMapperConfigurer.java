/**
 * RUDI Portail
 */
package org.rudi.facet.kaccess.config;

import javax.validation.constraints.NotNull;

import org.rudi.common.core.json.SubTypeRegister;
import org.rudi.facet.kaccess.bean.Feature;
import org.rudi.facet.kaccess.bean.FeatureCollection;
import org.rudi.facet.kaccess.bean.GeometryCollection;
import org.rudi.facet.kaccess.bean.LineString;
import org.rudi.facet.kaccess.bean.MultiLineString;
import org.rudi.facet.kaccess.bean.MultiPoint;
import org.rudi.facet.kaccess.bean.MultiPolygon;
import org.rudi.facet.kaccess.bean.Point;
import org.rudi.facet.kaccess.bean.Polygon;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
public class KaccessSubtypeMapperConfigurer implements SubTypeRegister {

	public void addSubTypes(@NotNull ObjectMapper objectMapper) {
		objectMapper.registerSubtypes(new NamedType(Feature.class, "Feature"),
				new NamedType(FeatureCollection.class, "FeatureCollection"), new NamedType(Point.class, "Point"),
				new NamedType(MultiPoint.class, "MultiPoint"), new NamedType(LineString.class, "LineString"),
				new NamedType(MultiLineString.class, "MultiLineString"), new NamedType(Polygon.class, "Polygon"),
				new NamedType(MultiPolygon.class, "MultiPolygon"),
				new NamedType(GeometryCollection.class, "GeometryCollection"));
	}

}
