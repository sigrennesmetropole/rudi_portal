package org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;

public interface SelfdataTokenTupleCustomDao {
	@Nullable
	SelfdataTokenTupleEntity findByDatasetUuidAndUserUuid(UUID datasetUuid, UUID userUuid);
}
