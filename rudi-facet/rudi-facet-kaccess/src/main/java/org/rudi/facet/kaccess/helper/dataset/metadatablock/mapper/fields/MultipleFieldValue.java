package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class MultipleFieldValue implements Iterable<MapOfFields> {

	private final List<MapOfFields> mapOfFields;


	static MultipleFieldValue empty() {
		return new MultipleFieldValue(Collections.emptyList());
	}

	static MultipleFieldValue from(List<Map<String, Object>> valueAsListOfMaps) {
		return new MultipleFieldValue(valueAsListOfMaps.stream()
				.map(MapOfFields::from)
				.collect(Collectors.toList()));
	}

	@Nonnull
	@Override
	public Iterator<MapOfFields> iterator() {
		return mapOfFields.iterator();
	}

	@Override
	public void forEach(Consumer<? super MapOfFields> action) {
		mapOfFields.forEach(action);
	}

	@Override
	public Spliterator<MapOfFields> spliterator() {
		return mapOfFields.spliterator();
	}
}
