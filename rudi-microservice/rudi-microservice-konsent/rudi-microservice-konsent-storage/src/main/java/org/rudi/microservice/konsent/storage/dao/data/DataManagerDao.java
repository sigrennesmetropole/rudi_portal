package org.rudi.microservice.konsent.storage.dao.data;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.data.DataManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataManagerDao extends JpaRepository<DataManagerEntity, Long> {
	@Nonnull
	DataManagerEntity findByUuid(UUID uuid);
}
