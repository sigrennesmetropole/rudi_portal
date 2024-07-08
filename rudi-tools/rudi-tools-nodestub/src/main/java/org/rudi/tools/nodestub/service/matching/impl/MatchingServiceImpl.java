package org.rudi.tools.nodestub.service.matching.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.tools.nodestub.bean.MatchingDescription;
import org.rudi.tools.nodestub.bean.MatchingField;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.service.matching.MatchingProcessor;
import org.rudi.tools.nodestub.service.matching.MatchingRequest;
import org.rudi.tools.nodestub.service.matching.MatchingService;
import org.rudi.tools.nodestub.service.matching.MatchingTokenMetadata;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class MatchingServiceImpl implements MatchingService {

	private final NodeStubConfiguration nodeStubConfiguration;
	private final ObjectMapper objectMapper;
	private final UtilContextHelper utilContextHelper;
	private final List<MatchingProcessor> matchingProcessors;

	private static final int MINIMUM_NUMBER_EXPECTED = 1;

	@Override
	public MatchingDescription createMatchingToken(UUID datasetUuid, String login, List<MatchingField> matchingFields)
			throws AppServiceUnauthorizedException, IOException, AppServiceBadRequestException,
			AppServiceNotFoundException {
		// on controle l'authentification
		final var authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("Cannot create matching token without being authenticated.");
		}

		// cas de tests pour rejet immédiat
		if (StringUtils.equals(login, nodeStubConfiguration.getBlacklistedUserLogin())) {
			throw new AppServiceNotFoundException(new EmptyResultDataAccessException(
					String.format("Erreur 404 : Utilisateur non présent dans le JDD %s", datasetUuid),
					MINIMUM_NUMBER_EXPECTED));
		}

		// là on prévoit plusieurs jdd avec chacun son processor
		MatchingDescription response = null;
		if (CollectionUtils.isNotEmpty(matchingProcessors)) {
			for (MatchingProcessor matchingProcessor : matchingProcessors) {
				try {
					if (response == null && matchingProcessor.accept(datasetUuid, login, matchingFields)) {
						response = matchingProcessor.computeToken(datasetUuid, login, matchingFields);
					}
				} catch (Exception e) {
					log.error("Failed to process", e);
				}
			}
		}

		// aucun processor n'a répondu => erreur 404
		if (response == null) {
			throw new AppServiceNotFoundException(new EmptyResultDataAccessException(
					String.format("Erreur 404 : Utilisateur non présent dans le JDD %s", datasetUuid),
					MINIMUM_NUMBER_EXPECTED));
		}

		// sinon on écrit le token sur disque et on retourne le résultat
		final var matchingTokenMetadata = MatchingTokenMetadata.builder()
				.request(MatchingRequest.builder().datasetUuid(datasetUuid).matchingFields(matchingFields).build())
				.login(login).response(response).build();

		write(matchingTokenMetadata);
		return response;
	}

	private void write(MatchingTokenMetadata matchingTokenMetadata) throws IOException {
		final var token = matchingTokenMetadata.getResponse().getToken();
		final Path matchingTokenMetadataPath = getMatchingTokenMetadataPath(token);
		Files.createDirectories(matchingTokenMetadataPath.getParent());
		objectMapper.writeValue(matchingTokenMetadataPath.toFile(), matchingTokenMetadata);
	}

	@Nonnull
	private Path getMatchingTokenMetadataPath(String token) {
		final var matchingTokensDirectory = nodeStubConfiguration.getMatchingTokensDirectory();
		return matchingTokensDirectory.resolve(token + ".json");
	}

	@Override
	public MatchingTokenMetadata getMatchingTokenMetadata(String token)
			throws IOException, AppServiceNotFoundException {
		final Path matchingTokenMetadataPath = getMatchingTokenMetadataPath(token);
		if (Files.notExists(matchingTokenMetadataPath)) {
			throw new AppServiceNotFoundException(MatchingTokenMetadata.class, "token", token);
		}
		return objectMapper.readValue(matchingTokenMetadataPath.toFile(), MatchingTokenMetadata.class);
	}
}
