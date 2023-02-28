package bean.workflow;


import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SelfdataTaskSearchCriteria extends TaskSearchCriteria {

	private String title;

}
