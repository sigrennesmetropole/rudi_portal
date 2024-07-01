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
public class CmsTermsDescriptionData extends AbstractCmsDescriptionData {

	private String cguCategory;

	private String legalMentionCategory;

	private String privacyPolicyCategory;

	private String copyrightsCategory;

}
