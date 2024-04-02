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
@ToString(callSuper = true)
public class CmsNewsDescriptionData extends AbstractCmsDescriptionData {

	private String alauneCategory;

	private String templateSimpleWithImage;
}
