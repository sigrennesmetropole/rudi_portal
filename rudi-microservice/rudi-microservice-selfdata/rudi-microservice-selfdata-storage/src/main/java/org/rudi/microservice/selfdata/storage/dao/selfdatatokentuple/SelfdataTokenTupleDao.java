package org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelfdataTokenTupleDao extends JpaRepository<SelfdataTokenTupleEntity, Long> {
	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc Spring</a>
	 */
	@Nullable
	SelfdataTokenTupleEntity findByUuid(UUID uuid);

}