package org.rudi.microservice.konsent.service.mapper.treatment;

import java.util.Optional;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.TreatmentVersionMapper;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class,
		TreatmentVersionMapper.class })
public interface TreatmentsMapper extends AbstractMapper<TreatmentEntity, Treatment> {
	@Override
	@InheritInverseConfiguration
	TreatmentEntity dtoToEntity(Treatment dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité. On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles
	 * sont recréées en base)
	 */
	@Override
	@Mapping(target = "versions", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "status", ignore = true)
	void dtoToEntity(Treatment dto, @MappingTarget TreatmentEntity entity);

	@Mapping(target = "versions", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "ownerType", ignore = true)
	@Mapping(target = "ownerUuid", ignore = true)
	@Mapping(target = "targetType", ignore = true)
	@Mapping(target = "targetUuid", ignore = true)
	// TODO compléter et appeler quand DRAFT ou pas de versions
	void dtoToEntityLight(Treatment dto, @MappingTarget TreatmentEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	Treatment entityToDto(TreatmentEntity entity);

	@AfterMapping
	default void setCurrentVersion(@MappingTarget Treatment dto, TreatmentEntity entity) {
		var currentVersion = entity.getVersions().stream()
				.filter(treatmentVersion -> treatmentVersion.getStatus() == TreatmentStatus.VALIDATED).findFirst();
		// Si une version VALIDATED présente, alors c'est la version courante
		setVersion(dto, currentVersion);
		if (currentVersion.isPresent()) { // Si on a trouvé une version VALIDATED, on arrête la recherche
			return;
		}
		// Si on a aucune version VALIDATED, on doit normalement avoir une version DRAFT, la première
		currentVersion = entity.getVersions().stream()
				.filter(treatmentVersion -> treatmentVersion.getStatus() == TreatmentStatus.DRAFT).findFirst();
		setVersion(dto, currentVersion);
	}

	// ApplicationContext.getBean(XX.class) pushée sur dev à recup dans common-service
	private void setVersion(Treatment dto, Optional<TreatmentVersionEntity> currentVersion) {
		currentVersion.ifPresent(versionEntity -> dto
				.setVersion(ApplicationContext.getBean(TreatmentVersionMapper.class).entityToDto(versionEntity)));
	}
}
