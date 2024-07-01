/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.customization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
public class AbstractCmsDescriptionData {

	private String category;

	private String templateDetailed;

	private String templateSimple;
}
