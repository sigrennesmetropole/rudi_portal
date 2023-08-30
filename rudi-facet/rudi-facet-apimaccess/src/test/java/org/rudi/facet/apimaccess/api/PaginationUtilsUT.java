package org.rudi.facet.apimaccess.api;

import org.junit.jupiter.api.Test;
import org.wso2.carbon.apimgt.rest.api.publisher.Pagination;
import org.wso2.carbon.apimgt.rest.api.publisher.SubscriptionList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PaginationUtilsUT {

	@Test
	void getNextPageOffset() {
		final SubscriptionList page = new SubscriptionList()
				.pagination(new Pagination()
						.next("/subscriptions?limit=1&offset=1&apiId=032f4d51-f91a-42ff-a45d-1a378c73458a&groupId="));
		final var offset = PaginationUtils.getNextPageOffset(page);
		assertThat(offset).isEqualTo(1);
	}

	@Test
	void getNextPageOffset_empty() {
		final SubscriptionList page = new SubscriptionList()
				.pagination(new Pagination()
						.next(""));
		final var offset = PaginationUtils.getNextPageOffset(page);
		assertThat(offset).isNull();
	}

	@Test
	void getNextPageOffset_missing() {
		final SubscriptionList page = new SubscriptionList()
				.pagination(new Pagination()
						.next("/subscriptions?limit=1&apiId=032f4d51-f91a-42ff-a45d-1a378c73458a&groupId="));
		final var offset = PaginationUtils.getNextPageOffset(page);
		assertThat(offset).isNull();
	}

}
