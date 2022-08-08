package org.rudi.microservice.projekt.storage.dao.type;

import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.UUID;

@Repository
public interface ProjectTypeDao extends JpaRepository<ProjectTypeEntity, Long> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	ProjectTypeEntity findByUUID(UUID uuid);

	ProjectTypeEntity findByCode(String code);
}
