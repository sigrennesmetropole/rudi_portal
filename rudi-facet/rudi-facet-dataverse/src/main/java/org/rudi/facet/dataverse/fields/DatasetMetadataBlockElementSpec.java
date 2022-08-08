package org.rudi.facet.dataverse.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

	/**
	 * Check if element contains children
	 * 
	 * @param parent
	 * @return true if parent element contains children
	 */
	public boolean hasChildren(FieldSpec parent) {
		return CollectionUtils.isNotEmpty(getChildrenOf(parent));
	}

	/**
	 * Retourn a stream for the children of parent
	 * 
	 * @param parent
	 * @return the stream
	 */
	public Stream<FieldSpec> streamChildrenOf(FieldSpec parent) {
		return getChildrenOf(parent).stream();
	}

	/**
	 * Return a fieldSpec by its dot notation name
	 * 
	 * @param name the dot notation name
	 * @return the fieldSpec
	 */
	public FieldSpec findFieldByName(String name) {
		FieldSpec result = null;
		String[] nameParts = name.split("\\.");
		for (String namePart : nameParts) {
			if (result == null) {
				result = findField(level1Fields.keySet(), namePart);
			} else {
				result = findField(getChildrenOf(result), namePart);
			}
			if (result == null) {
				break;
			}
		}
		return result;
	}

	private FieldSpec findField(Collection<FieldSpec> fieldSpecs, String name) {
		return fieldSpecs.stream().filter(f -> f.getLocalName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	private List<FieldSpec> getChildrenOf(FieldSpec parent) {
		return level1Fields.get(parent);
	}

	/**
	 * Return the parent of a child
	 * 
	 * @param child
	 * @return the parent
	 */
	@Nullable
	public FieldSpec getParentOf(FieldSpec child) {
		return parentCache.computeIfAbsent(child,
				k -> stream().filter(level1Field -> level1Field.getChildren().contains(child)).findAny().orElse(null));
	}
}
