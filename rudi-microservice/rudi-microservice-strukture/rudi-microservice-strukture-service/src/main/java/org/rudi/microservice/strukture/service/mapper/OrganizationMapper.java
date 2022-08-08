package org.rudi.microservice.strukture.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		MapperUtils.class,
		AbstractAddressMapper.class
})
public interface OrganizationMapper extends AbstractMapper<OrganizationEntity, Organization> {

	@Override
	@InheritInverseConfiguration
	OrganizationEntity dtoToEntity(Organization dto);

	@Override
	Organization entityToDto(OrganizationEntity entity);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Mapping(source = "uuid", target = "uuid", ignore = true)
	void dtoToEntity(Organization dto, @MappingTarget OrganizationEntity entity);

}
