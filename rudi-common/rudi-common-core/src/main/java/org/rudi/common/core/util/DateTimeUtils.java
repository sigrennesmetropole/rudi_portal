package org.rudi.common.core.util;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class DateTimeUtils {

	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Paris");
	public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

	private DateTimeUtils() {
	}

	/**
	 * @param localDateTime local date time at {@link #DEFAULT_ZONE_ID} zone
	 */
	@Nonnull
	public static OffsetDateTime toUTC(LocalDateTime localDateTime) {
		final var defaultOffsetDateTime = OffsetDateTime.of(localDateTime, DEFAULT_ZONE_ID.getRules().getOffset(localDateTime));
		final var utcZonedDateTime = defaultOffsetDateTime.atZoneSameInstant(UTC_ZONE_ID);
		return utcZonedDateTime.toOffsetDateTime();
	}

}
