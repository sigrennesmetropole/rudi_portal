package org.rudi.common.test;

import okhttp3.mockwebserver.RecordedRequest;
import org.rudi.common.core.DocumentContent;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class RudiAssertions {

	private RudiAssertions() {
	}

	public static <T> RudiObjectAssert<T> assertThat(T actual) {
		return new RudiObjectAssert<>(actual);
	}

	/**
	 * Creates a new instance of <code>{@link RudiListAssert}</code>.
	 *
	 * @param <E>    the type of elements.
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	public static <E> RudiListAssert<E> assertThat(List<? extends E> actual) {
		return new RudiListAssert<>(actual);
	}

	public static RudiDocumentContentAssert assertThat(DocumentContent actual) {
		return new RudiDocumentContentAssert(actual);
	}

	public static <T> RudiResponseEntityAssert<T> assertThat(ResponseEntity<T> actual) {
		return new RudiResponseEntityAssert<>(actual);
	}

	public static RudiRecordedRequestAssert assertThat(RecordedRequest request) {
		return new RudiRecordedRequestAssert(request);
	}


}
