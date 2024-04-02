/**
 * RUDI Portail
 */
package org.rudi.facet.cms.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.rudi.facet.cms.bean.CmsCategory;
import org.rudi.facet.cms.impl.model.CmsMagnoliaCategory;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CmsCategoryMapper extends AbstractCmsMapper<CmsMagnoliaCategory, CmsCategory> {

	@Mapping(source = "displayName", target = "label")
	CmsCategory convertItem(CmsMagnoliaCategory input);

}
