package org.rudi.microservice.acl.storage.dao.accountregistration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.accountregistration.AccountRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Dao for registration account entity
 * 
 * @author FNI18300
 *
 */
@Repository
public interface AccountRegistrationDao extends JpaRepository<AccountRegistrationEntity, Long> {

	AccountRegistrationEntity findByUuid(UUID uuid);

	AccountRegistrationEntity findByLogin(String login);

	AccountRegistrationEntity findByToken(String token);

	@Query("select a from AccountRegistrationEntity a where a.creationDate < :date")
	List<AccountRegistrationEntity> findByCreationDateBefore(@Param("date") LocalDateTime date);
}
