/**
 * 
 */
package org.rudi.facet.generator.text.impl;

import java.util.Arrays;
import java.util.List;

/**
 * @author fni18300
 *
 */
public final class TemplateGeneratorConstants {

	public static final String STRING_TEMPLATE_LOADER_PREFIX = "stl:";

	public static final String STRING_TEMPLATE_LOADER_SHORT_PREFIX = "stl";

	public static final String FONT_TTF = "ttf";

	public static final String FONT_OTF = "otf";

	private static final String[] FREE_MARKER_EXTENSION = { "ftl", "html", "txt", "xml", "properties" };

	private TemplateGeneratorConstants() {
		super();
	}

	public static List<String> getFreeMarkerExtension() {
		return Arrays.asList(FREE_MARKER_EXTENSION);
	}

}
