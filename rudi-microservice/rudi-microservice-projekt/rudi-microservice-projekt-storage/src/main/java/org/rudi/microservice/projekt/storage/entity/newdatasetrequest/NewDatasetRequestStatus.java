package org.rudi.microservice.projekt.storage.entity.newdatasetrequest;

import org.rudi.common.storage.entity.PositionedStatus;
import org.rudi.common.storage.entity.StatusPosition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NewDatasetRequestStatus implements PositionedStatus {

	DRAFT(StatusPosition.INITIAL),

	/**
	 * En cours
	 */
	IN_PROGRESS(StatusPosition.INTERMEDIATE),

	/**
	 * Validé => le projet est une réutilisation
	 */
	VALIDATED(StatusPosition.FINAL),

	/**
	 * Refusée
	 */
	REFUSED(StatusPosition.FINAL);

	private final StatusPosition position;

	@Override
	public boolean isInitial() {
		return position == StatusPosition.INITIAL;
	}

	@Override
	public boolean isFinal() {
		return position == StatusPosition.FINAL;
	}

}
