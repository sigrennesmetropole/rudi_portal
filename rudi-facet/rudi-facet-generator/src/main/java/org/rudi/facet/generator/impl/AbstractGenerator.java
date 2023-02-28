/**
 * 
 */
package org.rudi.facet.generator.impl;

import org.rudi.facet.generator.Generator;
import org.rudi.facet.generator.TemporaryHelper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Classes abstraite pour la génération des documents
 * 
 * @author FNI18300
 *
 */
@RequiredArgsConstructor
public abstract class AbstractGenerator<T> implements Generator<T> {

	@Getter(value = AccessLevel.PROTECTED)
	private final TemporaryHelper temporaryHelper;
}
