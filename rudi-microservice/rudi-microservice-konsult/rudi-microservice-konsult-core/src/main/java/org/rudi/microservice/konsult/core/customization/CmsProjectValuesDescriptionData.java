/**
 * RUDI Portail
 */
package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CmsProjectValuesDescriptionData extends AbstractCmsDescriptionData {

	private String mainCategory;

	private List<MultilingualText> titles1;

	private List<MultilingualText> titles2;

	private List<MultilingualText> descriptions;

}
