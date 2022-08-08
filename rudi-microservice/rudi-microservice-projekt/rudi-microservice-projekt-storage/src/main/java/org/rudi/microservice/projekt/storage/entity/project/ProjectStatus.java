package org.rudi.microservice.projekt.storage.entity.project;

import org.rudi.common.storage.entity.PositionedStatus;
import org.rudi.common.storage.entity.StatusPosition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProjectStatus implements PositionedStatus {

	DRAFT(StatusPosition.INITIAL),

	/**
	 * En cours
	 */
	IN_PROGRESS(StatusPosition.INTERMEDIATE),

	/**
	 * Validé => le projet est une réutilisation
	 */
	VALIDATED(StatusPosition.INTERMEDIATE),

	/**
	 * Abandonné
	 */
	CANCELLED(StatusPosition.FINAL),

	/**
	 * Abandonné
	 */
	DISENGAGED(StatusPosition.FINAL);

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
