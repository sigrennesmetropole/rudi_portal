package org.rudi.microservice.konsent.service.mapper.data;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.konsent.core.bean.DataManager;
import org.rudi.microservice.konsent.storage.entity.data.DataManagerEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface DataManagerMapper extends AbstractMapper<DataManagerEntity, DataManager> {
	@Override
	@InheritInverseConfiguration
	DataManagerEntity dtoToEntity(DataManager dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Override
	// TODO ignorer les vraies entités filles (cf Javadoc ci-dessus)
	void dtoToEntity(DataManager dto, @MappingTarget DataManagerEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	DataManager entityToDto(DataManagerEntity entity);
}
