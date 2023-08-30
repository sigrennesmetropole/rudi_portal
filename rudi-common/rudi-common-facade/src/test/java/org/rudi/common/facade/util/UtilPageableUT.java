package org.rudi.common.facade.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class UtilPageableUT {
	private static final int PAGINATION_SIZE = 10;
	private final UtilPageable utilPageable = new UtilPageable(PAGINATION_SIZE);

	@Test
	void getPageable_offsetNull() {
		final Pageable pageable = utilPageable.getPageable(null, 10, "champ");
		assertThat(pageable.getOffset()).isEqualTo(0);
	}

	@Test
	void getPageable_offset() {
		final Pageable pageable = utilPageable.getPageable(22, 10, "champ");
		assertThat(pageable.getOffset()).isEqualTo(20);
	}

	@Test
	void getPageable_limitNull() {
		final Pageable pageable = utilPageable.getPageable(0, null, "champ");
		assertThat(pageable.getPageSize()).isEqualTo(PAGINATION_SIZE);
	}

	@Test
	void getPageable_limitZero() {
		final Pageable pageable = utilPageable.getPageable(0, 0, "champ");
		assertThat(pageable.getPageSize()).isEqualTo(PAGINATION_SIZE);
	}

	@Test
	void getPageable_sortNull() {
		final Pageable pageable = utilPageable.getPageable(0, 10, null);
		assertThat(pageable.getSort()).isEmpty();
	}

	@Test
	void getPageable_sortEmpty() {
		final Pageable pageable = utilPageable.getPageable(0, 10, "");
		assertThat(pageable.getSort()).isEmpty();
	}

	@Test
	void getPageable_sortChamp() {
		final Pageable pageable = utilPageable.getPageable(0, 10, "champ");
		assertThat(pageable.getSort()).containsExactly(Sort.Order.asc("champ"));
	}

	@Test
	void getPageable_sortChamps() {
		final Pageable pageable = utilPageable.getPageable(0, 10, "champ1,-champ2");
		assertThat(pageable.getSort()).containsExactly(Sort.Order.asc("champ1"), Sort.Order.desc("champ2"));
	}
}
