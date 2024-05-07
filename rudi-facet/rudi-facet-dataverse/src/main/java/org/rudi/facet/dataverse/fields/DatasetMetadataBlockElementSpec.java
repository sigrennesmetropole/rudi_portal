package org.rudi.facet.dataverse.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
	private final Map<FieldSpec, FieldSpec> level1ParentCache = new HashMap<>();

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

	public Stream<FieldSpec> streamLevel1Fields() {
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

	public Stream<FieldSpec> streamAllFields() {
		final var localLevel1Fields = this.level1Fields.keySet();
		var allFieldsStream = localLevel1Fields.stream();

		for (final var level1Field : localLevel1Fields) {
			allFieldsStream = Stream.concat(allFieldsStream, streamChildrenOf(level1Field));
		}

		return allFieldsStream;
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
	 * Return the parent of a child.
	 * For instance METADATA_INFO_DATES is parent of METADATA_INFO_DATES_CREATED and METADATA_INFO is parent of METADATA_INFO_DATES.
	 * 
	 * @param child
	 * @return the parent
	 */
	@Nullable
	public FieldSpec getDirectParentOf(FieldSpec child) {
		return getParentOf(child, level1ParentCache, FieldSpec::getDirectChildren);
	}

	/**
	 * Return the level 1 field parent of a child.
	 * Level 1 field is a direct child of the root field.
	 * For instance METADATA_INFO is parent of METADATA_INFO_DATES_CREATED.
	 */
	@Nullable
	public FieldSpec getLevel1ParentOf(FieldSpec child) {
		return getParentOf(child, level1ParentCache, this::getChildrenOf);
	}

	private FieldSpec getParentOf(FieldSpec child, Map<FieldSpec, FieldSpec> parentCache, Function<FieldSpec, Collection<FieldSpec>> childrenFinder) {
		return parentCache.computeIfAbsent(child,
				k -> streamLevel1Fields()
						.filter(level1Field -> childrenFinder.apply(level1Field).contains(child))
						.findAny()
						.orElse(null));
	}

	public boolean containsLevel1Field(FieldSpec fieldSpec) {
		return level1Fields.containsKey(fieldSpec);
	}
}
