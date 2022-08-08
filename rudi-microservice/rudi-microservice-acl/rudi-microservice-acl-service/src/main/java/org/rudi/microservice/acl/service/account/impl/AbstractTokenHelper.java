package org.rudi.microservice.acl.service.account.impl;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.Processor;
import org.rudi.microservice.acl.service.account.TokenExpiredException;
import org.rudi.microservice.acl.storage.dao.accountupdate.HasTokenDao;
import org.rudi.microservice.acl.storage.entity.accountupdate.HasToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe utilitaire pour valider un token, récupérer l'entité liée et la supprimer si nécessaire.
 *
 * @param <E> type de l'entité contenant le token
 * @param <D> DAO gérant l'entité
 */
@RequiredArgsConstructor
abstract class AbstractTokenHelper<E extends HasToken, D extends HasTokenDao<E> & JpaRepository<E, Long>> {

	private final D dao;

	public void validateToken(final UUID token, final Processor<E> onSuccessProcessor) throws AppServiceException {
		final var entity = findEntityAndCheckTokenValidity(token);
		onSuccessProcessor.process(entity);
		deleteEntityByToken(token);
	}

	@Nonnull
	private E findEntityAndCheckTokenValidity(@Nullable UUID token) throws MissingParameterException, AppServiceNotFoundException, TokenExpiredException {

		if (token == null) {
			throw new MissingParameterException("token manquant");
		}

		final var updatePasswordEntity = dao.findByToken(token);
		if (updatePasswordEntity == null) {
			throw new AppServiceNotFoundException(getEntityClass(), token);
		}

		final var maxValidCreationDate = LocalDateTime.now().minus(Duration.ofMinutes(getTokenValidity()));
		if (updatePasswordEntity.getCreationDate().isBefore(maxValidCreationDate)) {
			deleteEntityByToken(token);
			throw new TokenExpiredException();
		}

		return updatePasswordEntity;
	}

	abstract Class<E> getEntityClass();

	abstract int getTokenValidity();

	private void deleteEntityByToken(UUID token) {
		final var updatePasswordEntity = dao.findByToken(token);
		dao.delete(updatePasswordEntity);
	}

	public void checkTokenValidity(@Nullable UUID token) throws AppServiceNotFoundException, TokenExpiredException, MissingParameterException {
		findEntityAndCheckTokenValidity(token);
	}
}
