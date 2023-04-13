package org.rudi.microservice.konsent.storage.dao.data;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.data.DataRecipientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRecipientDao extends JpaRepository<DataRecipientEntity, Long> {
	DataRecipientEntity findByUuid(UUID uuid);
}
