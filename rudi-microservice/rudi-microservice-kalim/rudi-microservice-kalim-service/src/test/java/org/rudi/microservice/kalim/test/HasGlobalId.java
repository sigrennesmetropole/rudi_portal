package org.rudi.microservice.kalim.test;

import org.mockito.ArgumentMatcher;
import org.rudi.facet.kaccess.bean.Metadata;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;

public class HasGlobalId implements ArgumentMatcher<Metadata> {

	@Nonnull
	private final UUID globalId;

	public HasGlobalId(@Nonnull UUID globalId) {
		Objects.requireNonNull(globalId, "Global ID cannot be null");
		this.globalId = globalId;
	}

	public static Metadata withSameGlobalIdAs(@Nonnull Metadata metadata) {
		return argThat(new HasGlobalId(metadata.getGlobalId()));
	}

	@Override
	public boolean matches(Metadata metadata) {
		return metadata != null && globalId.equals(metadata.getGlobalId());
	}
}
