package org.rudi.common.core;

import java.time.LocalDateTime;

/**
 * @author FNI18300
 */
public interface Stamped {

	LocalDateTime getOpeningDate();

	void setOpeningDate(LocalDateTime openingDate);

	LocalDateTime getClosingDate();

	void setClosingDate(LocalDateTime closingDate);
}
