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

	@Override
	OrganizationBean entityToDto(OrganizationEntity entity);

	@AfterMapping
	default void afterMapping(OrganizationEntity entity, @MappingTarget OrganizationBean bean){
		//TODO RUDI-4155 : le nombre de JDD et de projet est à charger ici
	}
}
