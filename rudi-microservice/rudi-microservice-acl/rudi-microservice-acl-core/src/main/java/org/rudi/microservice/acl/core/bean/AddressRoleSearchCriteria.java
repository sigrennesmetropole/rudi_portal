/**
 * 
 */
package org.rudi.microservice.acl.core.bean;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class AddressRoleSearchCriteria {

	private Boolean active;

	private AddressType type;
}
