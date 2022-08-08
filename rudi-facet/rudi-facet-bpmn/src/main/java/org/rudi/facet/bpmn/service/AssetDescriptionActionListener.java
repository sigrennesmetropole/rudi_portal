package org.rudi.facet.bpmn.service;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.exception.InvalidDataException;

/**
 * Listener permettant de valider ou non / enrichir ou non l'asset avant modification
 * 
 * @author FNI18300
 *
 */
public interface AssetDescriptionActionListener<E extends AssetDescriptionEntity> {

	void beforeCreate(E assetDescription) throws InvalidDataException;

	void afterCreate(E assetDescription);

	void beforeUpdate(E assetDescription) throws InvalidDataException;

	void afterUpdate(E assetDescription);

	void beforeDelete(E assetDescription) throws InvalidDataException;

	void afterDelete(E assetDescription);
}
