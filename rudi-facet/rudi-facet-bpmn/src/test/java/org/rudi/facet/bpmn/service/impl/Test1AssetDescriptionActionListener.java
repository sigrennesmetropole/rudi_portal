/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service.impl;

import org.rudi.facet.bpmn.entity.workflow.Test1AssetDescriptionEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
public class Test1AssetDescriptionActionListener
		extends AbstractAssetDescriptionAssetListener<Test1AssetDescriptionEntity> {

	@Override
	public void beforeCreate(Test1AssetDescriptionEntity assetDescription) {
		log.info("Before create {}", assetDescription);
	}

}
