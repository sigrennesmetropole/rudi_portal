package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.strukture.core.bean.OrganizationBean;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperUtils.class,
})
public interface OrganizationBeanMapper extends AbstractMapper<OrganizationEntity, OrganizationBean> {

	@AfterMapping
	default void afterMapping(OrganizationEntity entity, @MappingTarget OrganizationBean bean){
		// on set les valeurs par défaut à zéro projet et zéro jdd
		bean.setDatasetCount(0);
		bean.setProjectCount(0);
	}
}
