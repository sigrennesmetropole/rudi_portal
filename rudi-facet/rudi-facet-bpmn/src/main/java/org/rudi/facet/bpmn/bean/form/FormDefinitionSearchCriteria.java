/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.bean.form;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinitionSearchCriteria {

	private String formName;

	private String sectionName;

	private List<UUID> sectionUuids;
}
