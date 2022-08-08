package org.rudi.microservice.acl.storage.dao.accountupdate;

import org.rudi.microservice.acl.storage.entity.accountupdate.ResetPasswordRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.UUID;

@Repository
public interface ResetPasswordRequestDao extends JpaRepository<ResetPasswordRequestEntity, Long>, HasTokenDao<ResetPasswordRequestEntity> {
	@Nullable
	ResetPasswordRequestEntity findByUuid(UUID uuid);
	@Nullable
	ResetPasswordRequestEntity findByToken(UUID token);
}
