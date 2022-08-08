/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.service.AssetDescriptionActionListener;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractAssetDescriptionAssetListener<E extends AssetDescriptionEntity>
		implements AssetDescriptionActionListener<E> {

	@Override
	public void beforeCreate(E assetDescription) {

	}

	@Override
	public void afterCreate(E assetDescription) {

	}

	@Override
	public void beforeUpdate(E assetDescription) {

	}

	@Override
	public void afterUpdate(E assetDescription) {

	}

	@Override
	public void beforeDelete(E assetDescription) {

	}

	@Override
	public void afterDelete(E assetDescription) {

	}

}
