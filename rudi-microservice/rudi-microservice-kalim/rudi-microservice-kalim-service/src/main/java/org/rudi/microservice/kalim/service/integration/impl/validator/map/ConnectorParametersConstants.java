package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.List;

public class ConnectorParametersConstants {
	public static final String EPSG_TEXT = "EPSG";
	public static final String ALPHANUMERIC_REGEX = "^[a-z0-9_-]+";
	public static final String WFS_INTERFACE_CONTRACT = "wfs";
	public static final String WMS_INTERFACE_CONTRACT = "wms";
	public static final String WMTS_INTERFACE_CONTRACT = "wmts";
	public static final String DEFAULT_CRS_PARAMETER = "default_crs";
	public static final String FORMATS_PARAMETER = "formats";
	public static final String APP_JSON_FORMAT = "application/json";
	public static final String GML2_FORMAT = "GML2";
	public static final String GML3_FORMAT = "GML3";
	public static final String LAYER_PARAMETER = "layer";
	public static final String MAX_FEATURES_PARAMETER = "max_features";
	public static final String MAX_ZOOM_PARAMETER = "max_zoom";
	public static final String OTHER_CRSS_PARAMETER = "other_crss";
	public static final String STYLES_PARAMETER = "styles";
	public static final String TRANSPARENT_PARAMETER = "transparent";
	public static final String VERSION_PARAMETER = "versions";
	public static final String MATRIX_SET_PARAMETER = "matrix_set";
	public static final List<String> WMTS_MANDATORY_PARAMS = List.of(VERSION_PARAMETER, LAYER_PARAMETER, DEFAULT_CRS_PARAMETER, FORMATS_PARAMETER, MATRIX_SET_PARAMETER, MAX_ZOOM_PARAMETER);
	public static final List<String> WMS_MANDATORY_PARAMS = List.of(VERSION_PARAMETER, LAYER_PARAMETER, DEFAULT_CRS_PARAMETER, FORMATS_PARAMETER);
	public static final List<String> WFS_MANDATORY_PARAMS = List.of(VERSION_PARAMETER, LAYER_PARAMETER, DEFAULT_CRS_PARAMETER, FORMATS_PARAMETER);

	private ConnectorParametersConstants() {}
}
