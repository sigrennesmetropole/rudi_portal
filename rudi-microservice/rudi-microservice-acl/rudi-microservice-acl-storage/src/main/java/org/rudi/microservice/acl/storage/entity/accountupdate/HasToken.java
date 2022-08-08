package org.rudi.microservice.acl.storage.entity.accountupdate;

import java.time.LocalDateTime;
import java.util.UUID;

public interface HasToken {

	UUID getToken();

	/**
	 * @return date de création du token
	 */
	LocalDateTime getCreationDate();
}
