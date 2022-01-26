package org.rudi.microservice.acl.core.bean;

import lombok.Data;

/**
 * @author MCY12700
 *
 */
@Data
public class RoleSearchCriteria {

	private Boolean active;

	private String code;

	private String label;

}
