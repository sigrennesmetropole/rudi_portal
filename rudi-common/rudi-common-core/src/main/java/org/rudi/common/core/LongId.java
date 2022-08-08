package org.rudi.common.core;

import java.util.UUID;

/**
 * @author FNI18300
 *
 */
public interface LongId {

	Long getId();

	void setId(Long id);

	UUID getUuid();

	void setUuid(UUID uuid);

}
