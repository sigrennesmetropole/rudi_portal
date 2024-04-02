/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.customization;

import java.util.Locale;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class MultilingualText {

	private Locale locale;

	private String text;
}
