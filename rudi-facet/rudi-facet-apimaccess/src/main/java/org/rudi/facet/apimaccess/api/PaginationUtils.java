package org.rudi.facet.apimaccess.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class PaginationUtils {
	private PaginationUtils() {
	}

	/**
	 * @return null if not found
	 */
	@Nullable
	public static Integer getNextPageOffset(org.wso2.carbon.apimgt.rest.api.publisher.SubscriptionList page) {
		final var pagination = page.getPagination();
		if (pagination == null) {
			return null;
		}

		final var next = pagination.getNext();
		if (StringUtils.isBlank(next)) {
			return null;
		}

		final var offset = getOffsetFromQueryParam(next);
		if (offset == null) {
			return null;
		}

		try {
			return Integer.valueOf(offset);
		} catch (NumberFormatException e) {
			log.error("The offset \"{}\" found in next page URL \"{}\" is not a valid integer", offset, next, e);
			return null;
		}
	}

	@Nullable
	private static String getOffsetFromQueryParam(String uri) {
		try {
			final var params = URLEncodedUtils.parse(new URI(uri), StandardCharsets.UTF_8);
			return params.stream()
					.filter(param -> param.getName().equals("offset"))
					.map(NameValuePair::getValue)
					.findFirst()
					.orElse(null);
		} catch (URISyntaxException e) {
			log.error("Cannot read next page URL : {}", uri, e);
			return null;
		}
	}
}
