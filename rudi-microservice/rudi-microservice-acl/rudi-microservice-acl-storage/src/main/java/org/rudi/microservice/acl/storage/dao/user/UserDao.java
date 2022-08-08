package org.rudi.microservice.acl.storage.dao.user;

import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Dao pour les Users
 * 
 * @author FNI18300
 *
 */
@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {

	@Nullable
	UserEntity findByUuid(UUID uuid);

	@Nullable
	UserEntity findByLogin(String login);

	List<UserEntity> findByAccountLockedAndLastFailedAttemptLessThan(boolean locked, LocalDateTime lastFailedAttempt);
}
