package org.rudi.common.storage.dao;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation par défaut pour les entités "stamped" permettant de filtrer
 * celles-ci à une date donnée en fonction de leur activation
 * 
 * @author FNI18300
 *
 */
@SuppressWarnings({ "java:S2055" }) // Impossible d'ajouter un constructeur sans argument dans Spring Data
public class StampedRepositoryImpl<T extends AbstractStampedEntity> extends SimpleJpaRepository<T, Long>
		implements StampedRepository<T> {

	private static final long serialVersionUID = 2670116844084458196L;

	private transient EntityManager entityManager;

	// le type courant de l'instantiation
	private final Class<T> type;

	/**
	 * Le constructeur par défaut nécessaire à JPA
	 * 
	 * @param entityInformation
	 * @param entityManager
	 */
	public StampedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		type = entityInformation.getJavaType();
		this.entityManager = entityManager;
	}

	public List<T> findActive(Date d) {
		List<T> result = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<T> searchQuery = builder.createQuery(type);
		Root<T> searchRoot = searchQuery.from(type);

		buildQuery(d, builder, searchQuery, searchRoot);
		applySortCriteria(builder, searchQuery, searchRoot);

		TypedQuery<T> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		return result;
	}

	private void applySortCriteria(CriteriaBuilder builder, CriteriaQuery<T> criteriaQuery, Root<T> searchRoot) {
		List<Order> orders = new ArrayList<>();
		orders.add(builder.asc(searchRoot.get("code")));
		orders.add(builder.asc(searchRoot.get("id")));
		criteriaQuery.orderBy(orders);
	}

	private void buildQuery(Date d, CriteriaBuilder builder, CriteriaQuery<T> criteriaQuery, Root<T> searchRoot) {
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.lessThanOrEqualTo(searchRoot.get("openingDate"), d));
		predicates.add(builder.or(builder.greaterThanOrEqualTo(searchRoot.get("closingDate"), d),
				builder.isNull(searchRoot.get("closingDate"))));

		criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));

	}

	/**
	 * <b>Attention</b> : cette méthode ne peut être utilisée que pour des entités {@link AbstractStampedEntity}.
	 * Si on souhaite faire une recherche par UUID pour d'autres entités, il utiliser à la place dans la DAO :
	 *
	 * <pre>{@code
	 * T findByUuid(UUID uuid);
	 * }</pre>
	 *
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Override
	@Nonnull
	public T findByUUID(UUID uuid) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<T> searchQuery = builder.createQuery(type);
		Root<T> searchRoot = searchQuery.from(type);
		searchQuery.where(builder.equal(searchRoot.get("uuid"), uuid));

		TypedQuery<T> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getSingleResult();
	}
}
