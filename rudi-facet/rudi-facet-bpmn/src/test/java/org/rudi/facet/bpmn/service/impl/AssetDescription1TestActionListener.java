/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import org.rudi.facet.bpmn.entity.workflow.AssetDescription1TestEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
public class AssetDescription1TestActionListener
		extends AbstractAssetDescriptionAssetListener<AssetDescription1TestEntity> {

	@Override
	public void beforeCreate(AssetDescription1TestEntity assetDescription) {
		log.info("Before create {}", assetDescription);
	}

}
