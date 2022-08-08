package org.rudi.microservice.acl.storage.dao.accountupdate;

import java.util.UUID;

public interface HasTokenDao<E> {
	E findByToken(UUID token);
}
