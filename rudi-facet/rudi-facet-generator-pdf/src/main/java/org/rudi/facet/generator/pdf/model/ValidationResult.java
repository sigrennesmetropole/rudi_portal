/**
 * RUDI Portail
 */
package org.rudi.facet.generator.pdf.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
public class ValidationResult {

	private boolean valid;

	private List<ValidationResultItem> items;

	public void addItem(ValidationResultItem item) {
		if (items == null) {
			items = new ArrayList<>();
		}
		items.add(item);
	}
}
