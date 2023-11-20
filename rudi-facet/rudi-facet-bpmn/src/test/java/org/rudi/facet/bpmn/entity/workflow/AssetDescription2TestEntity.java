/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.entity.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Entity
@Table(name = "test_asset_description")
@Getter
@Setter
@ToString
public class AssetDescription2TestEntity extends AbstractAssetDescriptionEntity {

	private static final long serialVersionUID = -2844782701808083737L;

	@Column(name = "a", length = 12)
	private String a;
}
