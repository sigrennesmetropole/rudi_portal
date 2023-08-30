package org.rudi.common.test;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDUtilsUT {

	@Test
	void keepOnlySecondUUIDSegment() {
		final UUID uuid = UUIDUtils.keepOnlyUUIDSegment(1, UUID.fromString("5596b5b2-b227-4c74-a9a1-719e7c1008c7"));
		assertThat(uuid).hasToString("00000000-b227-0000-0000-000000000000");
	}

	@Test
	void eraseOnlySecondUUIDSegment() {
		final UUID uuid = UUIDUtils.eraseOnlyUUIDSegment(1, UUID.fromString("5596b5b2-b227-4c74-a9a1-719e7c1008c7"));
		assertThat(uuid).hasToString("5596b5b2-0000-4c74-a9a1-719e7c1008c7");
	}
}
