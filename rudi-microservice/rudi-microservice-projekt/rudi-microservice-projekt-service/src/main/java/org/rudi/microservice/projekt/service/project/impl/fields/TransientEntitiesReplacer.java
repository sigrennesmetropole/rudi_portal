package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;

import lombok.RequiredArgsConstructor;

/**
 * Doc Transient / Persistent : https://docs.jboss.org/hibernate/core/3.3/reference/fr-FR/html/objectstate.html
 *
 * @param <E>
 */
@RequiredArgsConstructor
abstract class TransientEntitiesReplacer<E> {
	private final Function<ProjectEntity, E> entitiesGetter;
	private final BiConsumer<ProjectEntity, E> entitiesSetter;

	void replaceTransientEntitiesWithPersistentEntities(@Nullable ProjectEntity project, @Nullable ProjectEntity existingProject) throws AppServiceException {
		if (project != null) {
			final var transientEntities = getTransientEntities(project);
			final var persistentEntities = getPersistentEntities(transientEntities);

			// Si on est en update (existingProject != null) alors c'est lui qu'on modifie sinon c'est l'autre (création)
			final var targetProject = Objects.requireNonNullElse(existingProject, project);

			setPersistentEntities(targetProject, persistentEntities);
		}
	}

	@Nullable
	private E getTransientEntities(@Nonnull ProjectEntity sourceProject) {
		return entitiesGetter.apply(sourceProject);
	}

	/**
	 * // TODO javadoc à revoir
	 * Remplace les entités transient du projet source (sourceProject) par les entités réelles présentes en base dans le projet cible (targetProject)
	 */
	@Nullable
	protected abstract E getPersistentEntities(@Nullable E transientEntities) throws AppServiceException;

	private void setPersistentEntities(@Nonnull ProjectEntity targetProject, @Nullable E persistentEntities) {
		entitiesSetter.accept(targetProject, persistentEntities);
	}

}
