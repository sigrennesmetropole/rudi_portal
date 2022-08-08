package org.rudi.microservice.projekt.storage.entity.linkeddataset;

import lombok.RequiredArgsConstructor;
import org.rudi.common.storage.entity.PositionedStatus;
import org.rudi.common.storage.entity.StatusPosition;

@RequiredArgsConstructor
public enum LinkedDatasetStatus implements PositionedStatus {
	/**
	 * En cours
	 */
	DRAFT(StatusPosition.INITIAL),

	/**
	 * Validé => le projet est une réutilisation
	 */
	IN_PROGRESS(StatusPosition.INTERMEDIATE),

	/**
	 * Validé
	 */
	VALIDATED(StatusPosition.FINAL),

	/**
	 * Abandonné
	 */
	CANCELLED(StatusPosition.FINAL);

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
