/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CmsRequest {

	private List<String> categories;
	private List<String> filters;
	private Locale locale;

}
