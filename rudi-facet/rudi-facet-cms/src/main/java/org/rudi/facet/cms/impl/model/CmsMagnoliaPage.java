/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.model;

import java.util.List;

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
public class CmsMagnoliaPage<T extends CmsMagnoliaNode> {

	private long total;

	private long limit;

	private long offset;

	private List<T> results;

}
