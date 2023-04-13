package org.rudi.microservice.konsent.storage.dao.data;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryEntryDao extends JpaRepository<DictionaryEntryEntity, Long> {

	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc Spring</a>
	 */
	@Nonnull
	DictionaryEntryEntity findByUuid(UUID uuid);

}
