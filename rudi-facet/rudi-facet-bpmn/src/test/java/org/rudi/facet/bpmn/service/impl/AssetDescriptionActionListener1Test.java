/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity1Test;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
public class AssetDescriptionActionListener1Test
		extends AbstractAssetDescriptionAssetListener<AssetDescriptionEntity1Test> {

	@Override
	public void beforeCreate(AssetDescriptionEntity1Test assetDescription) {
		log.info("Before create {}", assetDescription);
	}

}
