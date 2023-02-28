package org.rudi.facet.dataverse.helper.tsv;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

class TsvPart<L extends TsvLine> {
	final Set<L> lines;

	TsvPart(Collection<L> lines) {
		this.lines = new LinkedHashSet<>(lines);
	}

	@Nullable
	L getLine(Predicate<L> predicate) {
		return lines.stream().filter(predicate).findFirst().orElse(null);
	}

	@Nullable
	L getLineWithSameHashStringAs(L lineWithSameHashString) {
		final Predicate<L> lineWithSameHashStringPredicate = line -> line.getHashString().equals(lineWithSameHashString.getHashString());
		return getLine(lineWithSameHashStringPredicate);
	}
}
