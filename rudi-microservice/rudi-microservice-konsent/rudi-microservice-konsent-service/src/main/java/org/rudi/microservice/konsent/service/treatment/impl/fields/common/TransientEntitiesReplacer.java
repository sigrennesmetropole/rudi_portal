package org.rudi.microservice.konsent.service.treatment.impl.fields.common;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;

import lombok.RequiredArgsConstructor;

/**
 * Doc Transient / Persistent : https://docs.jboss.org/hibernate/core/3.3/reference/fr-FR/html/objectstate.html
 *
 * @param <V>
 * @param <E>
 */
@RequiredArgsConstructor
public abstract class TransientEntitiesReplacer<V, E> {
	private final Function<V, E> entitiesGetter;
	private final BiConsumer<V, E> entitiesSetter;


	public void replaceTransientEntitiesWithPersistentEntities(@Nullable V fieldToProcess, @Nullable V fieldToProcessPreviousValueInDB) throws AppServiceException {
		if (fieldToProcess != null) {
			final var transientEntities = getTransientEntities(fieldToProcess);
			final var persistentEntities = getPersistentEntities(transientEntities);

			// Si on est en update (existingProject != null) alors c'est lui qu'on modifie sinon c'est l'autre (création)
			final var targetTreatmentVersion = Objects.requireNonNullElse(fieldToProcessPreviousValueInDB, fieldToProcess);

			setPersistentEntities(targetTreatmentVersion, persistentEntities);
		}
	}

	@Nullable
	private E getTransientEntities(@Nonnull V sourceTreatmentVersion) {
		return entitiesGetter.apply(sourceTreatmentVersion);
	}

	/**
	 * // TODO javadoc à revoir
	 * Remplace les entités transient du projet source (sourceProject) par les entités réelles présentes en base dans le projet cible (targetProject)
	 */
	@Nullable
	protected abstract E getPersistentEntities(@Nullable E transientEntities) throws AppServiceException;

	private void setPersistentEntities(@Nonnull V targetTreatmentVersion, @Nullable E persistentEntities) {
		entitiesSetter.accept(targetTreatmentVersion, persistentEntities);
	}

}
