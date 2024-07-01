package org.rudi.facet.apimaccess.api.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.rudi.common.core.util.ContentTypeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;

public class FlexClientHttpResponseDecorator extends ClientHttpResponseDecorator {
	private final ClientHttpResponse delegate;

	public FlexClientHttpResponseDecorator(ClientHttpResponse delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public @NotNull HttpHeaders getHeaders() {
		HttpHeaders customHeaders = new HttpHeaders();
		for (Map.Entry<String, List<String>> entry : this.delegate.getHeaders().entrySet()) {
			if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
				var quotedList = new ArrayList<String>();
				for(String value : entry.getValue()) {
					var result = ContentTypeUtils.normalize(value);
					quotedList.add(result.toString());
				}
				customHeaders.put(entry.getKey(), quotedList);
			} else {
				customHeaders.put(entry.getKey(), entry.getValue());
			}
		}
		return customHeaders;
	}
}
