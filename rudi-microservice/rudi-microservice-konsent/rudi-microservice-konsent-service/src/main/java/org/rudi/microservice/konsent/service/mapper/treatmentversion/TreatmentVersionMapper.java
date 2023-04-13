package org.rudi.microservice.konsent.service.mapper.treatmentversion;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.data.DataManagerMapper;
import org.rudi.microservice.konsent.service.mapper.data.DataRecipientMapper;
import org.rudi.microservice.konsent.service.mapper.data.DictionaryEntryMapper;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class, DataManagerMapper.class,
		DataRecipientMapper.class, DictionaryEntryMapper.class, InvolvedPopulationCategoryMapper.class, PurposeMapper.class, RetentionMapper.class, SecurityMeasureMapper.class, TypologyTreatmentMapper.class })
public interface TreatmentVersionMapper extends AbstractMapper<TreatmentVersionEntity, TreatmentVersion> {
	@Override
	@InheritInverseConfiguration
	TreatmentVersionEntity dtoToEntity(TreatmentVersion dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Override
	@Mapping(target = "retention", ignore = true)
	@Mapping(target = "purpose", ignore = true)
	@Mapping(target = "typology", ignore = true)
	@Mapping(target = "dataRecipients", ignore = true)
	@Mapping(target = "securityMeasures", ignore = true)
	@Mapping(target = "involvedPopulation", ignore = true)
	@Mapping(target = "dataProtectionOfficer", ignore = true)
	@Mapping(target = "manager", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "datas", ignore = true)
	@Mapping(target = "usages", ignore = true)
	@Mapping(target = "titles", ignore = true)
	@Mapping(target = "operationTreatmentNatures", ignore = true)
	void dtoToEntity(TreatmentVersion dto, @MappingTarget TreatmentVersionEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	TreatmentVersion entityToDto(TreatmentVersionEntity entity);
}
