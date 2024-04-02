/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.customization;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class CustomizationDescriptionData {

	private String overrideCssFile;

	private String mainLogo;

	private HeroDescriptionData heroDescription;

	private ProjectsDescriptionData projectsDescription;

	private KeyFiguresDescriptionData keyFiguresDescription;
}
