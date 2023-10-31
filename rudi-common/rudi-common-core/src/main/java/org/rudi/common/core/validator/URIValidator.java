/**
 * RUDI Portail
 */
package org.rudi.common.core.validator;

import java.net.URISyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 * @author FNI18300
 *
 */
public class URIValidator implements ConstraintValidator<URI, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(value)) {
			return true;
		}

		try {
			new java.net.URI(value);
		} catch (URISyntaxException e) {
			return false;
		}

		return true;
	}

}
