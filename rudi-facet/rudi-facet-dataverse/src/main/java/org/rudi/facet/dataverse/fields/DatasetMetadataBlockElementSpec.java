package org.rudi.facet.dataverse.fields;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DatasetMetadataBlockElementSpec {

	@Getter
	private final FieldSpec root;
	private final Map<FieldSpec, List<FieldSpec>> level1Fields = new HashMap<>();
	private final Map<FieldSpec, FieldSpec> parentCache = new HashMap<>();

	/**
	 * @param fieldSpec           Level 1 Field Specification to add
	 * @param childrenFieldsSpecs Level 2 Field Specifications to add
	 * @return this
	 */
	public DatasetMetadataBlockElementSpec add(FieldSpec fieldSpec, FieldSpec... childrenFieldsSpecs) {
		level1Fields.putIfAbsent(fieldSpec, new ArrayList<>());
		if (childrenFieldsSpecs.length > 0) {
			getChildrenOf(fieldSpec).addAll(Arrays.asList(childrenFieldsSpecs));
		}
		return this;
	}

	public Stream<FieldSpec> stream() {
		return level1Fields.keySet().stream();
	}

	public boolean hasChildren(FieldSpec parent) {
		return CollectionUtils.isNotEmpty(getChildrenOf(parent));
	}

	public Stream<FieldSpec> streamChildrenOf(FieldSpec parent) {
		return getChildrenOf(parent).stream();
	}

	private List<FieldSpec> getChildrenOf(FieldSpec parent) {
		return level1Fields.get(parent);
	}

	@Nullable
	public FieldSpec getParentOf(FieldSpec child) {
		return parentCache.computeIfAbsent(child, k ->
				stream()
						.filter(level1Field -> level1Field.getChildren().contains(child))
						.findAny()
						.orElse(null));
	}
}
