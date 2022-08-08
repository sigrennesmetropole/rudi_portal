package org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper;

import lombok.AccessLevel;
import lombok.Setter;
import org.rudi.common.core.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class DateTimeMapper {

	/**
	 * The ZoneOffset that was used to build Dataverse timestamp before RUDI-1060
	 */
	private static final ZoneOffset DATAVERSE_TIMESTAMP_V1_ZONE_OFFSET = ZoneOffset.UTC;

	private static final ZoneId DATAVERSE_TIMESTAMP_V1_ZONE_ID = DateTimeUtils.DEFAULT_ZONE_ID;
	private static final ZoneId DATAVERSE_TIMESTAMP_V2_ZONE_ID = DateTimeUtils.UTC_ZONE_ID;

	private static final int NANO_IN_A_SECOND = 1000000000;

	/**
	 * true if Dataverse has been migrated to handle V2 timestamps.
	 * @deprecated delete this boolean when RUDI-1060 is completely validated
	 */
	@Value("${dataverse.timestamps.v2:true}")
	@Setter(AccessLevel.PROTECTED)
	@Deprecated(forRemoval = true)
	private boolean dataverseUsesTimestampsV2 = true;

	@Nonnull
	public String toDataverseTimestamp(@Nonnull OffsetDateTime source) {
		if (dataverseUsesTimestampsV2) {
			return toDataverseTimestampV2(source);
		} else {
			return toDataverseTimestampV1(source);
		}
	}

	@Nonnull
	private String toDataverseTimestampV1(@Nonnull OffsetDateTime source) {
		final var localDateTime = source.toLocalDateTime();
		final var epochSecond = localDateTime.toEpochSecond(ZoneOffset.UTC);
		return Long.toString(epochSecond);
	}

	@Nonnull
	private String toDataverseTimestampV2(@Nonnull OffsetDateTime source) {
		final var utc = source.atZoneSameInstant(DATAVERSE_TIMESTAMP_V2_ZONE_ID);
		final var epochSecond = utc.toEpochSecond();
		final var epochNano = epochSecond * NANO_IN_A_SECOND + source.getNano();
		return Long.toString(epochNano);
	}

	/**
	 * Use this method only if Dataverse timestamp format is unknown.
	 * Otherwise prefer {@link #fromDataverseTimestampV1(long)} or {@link #fromDataverseTimestampV2(long)}
	 */
	@Nonnull
	public OffsetDateTime fromDataverseTimestamp(@Nonnull String dataverseTimestampString) {
		final var dataverseTimestamp = Long.parseLong(dataverseTimestampString);
		if (dataverseUsesTimestampsV2) {
			return fromDataverseTimestampV2(dataverseTimestamp);
		} else {
			return fromDataverseTimestampV1(dataverseTimestamp);
		}
	}

	/**
	 * @param epochSecond Dataverse timestamp stored before RUDI-1060,
	 *                    which was built with LocalDateTime.toEpochSecond(ZoneOffset.UTC) using UTC instead of local zone offset
	 */
	private OffsetDateTime fromDataverseTimestampV1(long epochSecond) {
		final var localDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, DATAVERSE_TIMESTAMP_V1_ZONE_OFFSET);
		final var zonedDateTime = localDateTime.atZone(DATAVERSE_TIMESTAMP_V1_ZONE_ID);
		return zonedDateTime.toOffsetDateTime();
	}

	/**
	 * @param epochNano Dataverse timestamp stored after RUDI-1060
	 */
	private OffsetDateTime fromDataverseTimestampV2(long epochNano) {
		final var epochSecond = epochNano / NANO_IN_A_SECOND;
		final var nanoAdjustment = epochNano % NANO_IN_A_SECOND;
		final var instant = Instant.ofEpochSecond(epochSecond, nanoAdjustment);
		return OffsetDateTime.ofInstant(instant, DATAVERSE_TIMESTAMP_V2_ZONE_ID);
	}

}
