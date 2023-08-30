package org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeMapperUT {
	private final DateTimeMapper dateTimeMapper = new DateTimeMapper();

	@ParameterizedTest
	@CsvSource({
			"2021-03-22T12:15:12+01:00,      1616415312", // GMT+1
			"2021-06-22T12:15:12+02:00,      1624364112", // GMT+2
			"1970-01-01T00:00:00+01:00,               0", // = Epoch
			"1968-05-22T00:00:00+01:00,       -50889600", // < Epoch and before DST creation in France
	})
	void toDataverseTimestampV1(final OffsetDateTime source, final String expected) {
		dateTimeMapper.setDataverseUsesTimestampsV2(false);

		final var timestampV2 = dateTimeMapper.toDataverseTimestamp(source);
		assertThat(timestampV2).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1616415312, 2021-03-22T12:15:12+01:00", // GMT+1
			"1624364112, 2021-06-22T12:15:12+02:00", // GMT+2
			"         0, 1970-01-01T00:00:00+01:00", // = Epoch
			" -50889600, 1968-05-22T00:00:00+01:00", // < Epoch and before DST creation in France
	})
	void fromDataverseTimestampV1(final String dataverseTimestamp, final String expected) {
		dateTimeMapper.setDataverseUsesTimestampsV2(false);

		final var offsetDateTime = dateTimeMapper.fromDataverseTimestamp(dataverseTimestamp);
		assertThat(offsetDateTime).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"2021-03-22T12:15:12+01:00,      1616411712000000000", // GMT+1
			"2021-06-22T12:15:12+02:00,      1624356912000000000", // GMT+2
			"2021-06-22T10:15:12Z,           1624356912000000000", // UTC
			"2021-06-22T10:15:12.123456789Z, 1624356912123456789", // UTC with nanos
			"1970-01-01T00:00:00Z,           0",                   // = Epoch
			"1968-05-22T00:00:00+02:00,       -50896800000000000", // < Epoch
	})
	void toDataverseTimestampV2(final OffsetDateTime source, final String expected) {
		dateTimeMapper.setDataverseUsesTimestampsV2(true);

		final var timestampV2 = dateTimeMapper.toDataverseTimestamp(source);
		assertThat(timestampV2).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1616411712000000000, 2021-03-22T11:15:12Z",           // GMT+1
			"1624356912000000000, 2021-06-22T10:15:12Z",           // GMT+2
			"1624356912000000000, 2021-06-22T10:15:12Z",           // UTC
			"1624356912123456789, 2021-06-22T10:15:12.123456789Z", // UTC with nanos
			"                  0, 1970-01-01T00:00:00Z",           // = Epoch
			" -50896800000000000, 1968-05-21T22:00:00Z",           // < Epoch
	})
	void fromDataverseTimestampV2(final String dataverseTimestamp, final String expected) {
		dateTimeMapper.setDataverseUsesTimestampsV2(true);

		final var offsetDateTime = dateTimeMapper.fromDataverseTimestamp(dataverseTimestamp);
		assertThat(offsetDateTime).isEqualTo(expected);
	}

}
