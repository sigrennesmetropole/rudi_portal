/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class HeroDescriptionData {

	private String leftImage;

	private String rightImage;

	private List<MultilingualText> titles1;

	private List<MultilingualText> titles2;
}
