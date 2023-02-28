package org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest;

import org.rudi.common.storage.entity.PositionedStatus;
import org.rudi.common.storage.entity.StatusPosition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SelfdataInformationRequestStatus implements PositionedStatus {

	DRAFT(StatusPosition.INITIAL),

	/**
	 * En cours d'étude animateur
	 */
	IN_PROGRESS(StatusPosition.INTERMEDIATE),

	/**
	 * En cours d'étude producteur
	 */
	VALIDATED(StatusPosition.FINAL),

	/**
	 * Rejeté
	 */
	REJECTED(StatusPosition.INTERMEDIATE),

	/**
	 * Complété
	 */
	COMPLETED(StatusPosition.FINAL),

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
