/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.bean.workflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor()
@SuperBuilder(toBuilder = true)
public class TestTaskSearchCriteria extends TaskSearchCriteria {

	private String a;

}
