package org.rudi.microservice.selfdata.service.selfdata.impl;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.selfdata.service.selfdata.MatchingService;
import org.rudi.microservice.selfdata.storage.dao.selfdatatokentuple.SelfdataTokenTupleCustomDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
class MatchingServiceImpl implements MatchingService {
	private final ACLHelper aclHelper;
	private final SelfdataTokenTupleCustomDao selfdataTokenDao;
	private static final int MINIMUM_USER_NUMBER = 1; // Nombre minimum d'utilisateur attendu comme réponse de la requête

	@Override
	public UUID getMatchingToken(UUID datasetUuid, String login) throws AppServiceNotFoundException {
		User user = aclHelper.getUserByLogin(login);
		if (user == null) {
			throw new AppServiceNotFoundException(new EmptyResultDataAccessException(String.format("Aucun utilisateur trouvé pour ce login %s", login), MINIMUM_USER_NUMBER));
		}

		SelfdataTokenTupleEntity tokenTupleEntity = selfdataTokenDao.findByDatasetUuidAndUserUuid(datasetUuid, user.getUuid());
		if (tokenTupleEntity == null) {
			return null;
		}
		return tokenTupleEntity.getToken();
	}

	@Override
	public boolean hasMatchingToDataset(UUID userUuid, UUID datasetUuid) {
		return selfdataTokenDao.findByDatasetUuidAndUserUuid(datasetUuid, userUuid) != null;
	}
}