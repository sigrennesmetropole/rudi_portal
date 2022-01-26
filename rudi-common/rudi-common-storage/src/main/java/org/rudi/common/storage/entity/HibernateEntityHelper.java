package org.rudi.common.storage.entity;

import java.util.Collection;
import java.util.function.Supplier;

public class HibernateEntityHelper {

	private HibernateEntityHelper() {
	}

	/**
	 * Le remplacement de collection dans une entité provoque des problèmes de synchronisation Hibernate.
	 * Cela implique qu'on ne peut pas directement appeler des setters sur des collection. À la place on utilise cette méthode qui contourne le problème.
	 *
	 * <p>Exemple d'utilisation par la classe IntegrationRequestEntity :</p>
	 *
	 * <pre>{@code
	 * public void setErrors(Set<IntegrationRequestErrorEntity> errors) {
	 *     HibernateEntityHelper.setCollection(this::getErrors, errors);
	 * }
	 * }</pre>
	 *
	 * @param collectionGetter le getter pour obtenir la collection gérée par Hibernate
	 * @param newCollection    la collection qui doit remplacer l'actuelle
	 * @param <E>              type des éléments de cette collection
	 */
	public static <E> void setCollection(Supplier<Collection<E>> collectionGetter, Collection<E> newCollection) {
		final Collection<E> childrenSet = collectionGetter.get();
		childrenSet.clear();
		childrenSet.addAll(newCollection);
	}
}
