/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.mapper;

import java.util.List;

/**
 * @author FNI18300
 *
 */
public interface AbstractCmsMapper<T, D> {

	D convertItem(T input);

	List<D> convertItems(List<T> input);

}
