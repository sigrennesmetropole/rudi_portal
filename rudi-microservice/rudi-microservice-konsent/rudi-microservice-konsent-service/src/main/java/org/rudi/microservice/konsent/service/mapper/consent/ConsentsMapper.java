package org.rudi.microservice.konsent.service.mapper.consent;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.konsent.core.bean.Consent;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;

/**
 * @author FNI18300
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface ConsentsMapper extends AbstractMapper<ConsentEntity, Consent> {

	@Override
	@InheritInverseConfiguration
	ConsentEntity dtoToEntity(Consent dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Override
	@Mapping(target = "treatment", ignore = true)
	@Mapping(target = "treatmentVersion", ignore = true)
	void dtoToEntity(Consent dto, @MappingTarget ConsentEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	Consent entityToDto(ConsentEntity entity);

}
