package org.rudi.common.service.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Classe abstraite des mappers
 * 
 * @author FNI18300
 *
 * @param <E>
 * @param <D>
 */
public interface AbstractMapper<E, D> {

	/**
	 * @param dto to transform to entity
	 * @return entity
	 */
	E dtoToEntity(D dto);

	/**
	 * update entity with dto
	 * 
	 * @param dto
	 * @param entity
	 */
	void dtoToEntity(D dto, @MappingTarget E entity);

	/**
	 * @param dtos
	 * @return la liste d'entité converties
	 */
	List<E> dtoToEntities(List<D> dtos);

	/**
	 * @param entity entity to transform to dto
	 * @return dto
	 */
	D entityToDto(E entity);

	/**
	 * @param entities
	 * @return la liste de dtos convertis
	 */
	List<D> entitiesToDto(Collection<E> entities);

	default Page<D> entitiesToDto(Page<E> entities, Pageable pageable) {
		return new PageImpl<>(entitiesToDto(entities.getContent()), pageable, entities.getTotalElements());
	}

}
