package org.rudi.microservice.acl.storage.dao.user;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Users
 * 
 * @author FNI18300
 *
 */
@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {

	UserEntity findByUuid(UUID uuid);

	UserEntity findByLogin(String login);
}
