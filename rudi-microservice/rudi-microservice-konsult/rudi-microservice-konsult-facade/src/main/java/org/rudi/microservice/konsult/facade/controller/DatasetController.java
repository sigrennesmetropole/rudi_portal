package org.rudi.microservice.konsult.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.ANONYMOUS;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KALIM;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KALIM_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_PROJEKT;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_PROJEKT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_SELFDATA;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_SELFDATA_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacets;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.microservice.konsult.facade.controller.api.DatasetsApi;
import org.rudi.microservice.konsult.service.metadata.MetadataService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DatasetController implements DatasetsApi {

	private final MetadataService metadataService;
	private final ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<MetadataList> searchMetadatas(String freeText, List<String> themes, List<String> keywords,
			List<String> producerNames, OffsetDateTime dateDebut, OffsetDateTime dateFin, Boolean restrictedAccess,
			Boolean gdprSensitive, List<UUID> globalId, List<UUID> producerUuids, Integer offset, Integer limit,
			String order) throws Exception {

		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().limit(limit).offset(offset)
				.freeText(freeText).keywords(keywords).themes(themes).producerNames(producerNames)
				.producerUuids(producerUuids).dateDebut(dateDebut).dateFin(dateFin).order(order)
				.restrictedAccess(restrictedAccess).gdprSensitive(gdprSensitive).globalIds(globalId);

		return ResponseEntity.ok(metadataService.searchMetadatas(datasetSearchCriteria));
	}

	@Override
	public ResponseEntity<MetadataFacets> searchMetadataFacets(List<String> facets) throws Exception {
		return ResponseEntity.ok(metadataService.searchMetadatasFacets(facets));
	}

	@Override
	public ResponseEntity<Metadata> getMetadataById(UUID globalId) throws Exception {
		return ResponseEntity.ok(metadataService.getMetadataById(globalId));
	}

	@Override
	public ResponseEntity<Resource> downloadMetadataMedia(UUID globalId, UUID mediaId) throws Exception {
		final DocumentContent documentContent = metadataService.downloadMetadataMedia(globalId, mediaId);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + ANONYMOUS + ", " + USER + ")")
	public ResponseEntity<Boolean> hasSubscribeToSelfdataDataset(UUID globalId) throws Exception {
		return ResponseEntity.ok(metadataService.hasSubscribeToSelfdataDataset(globalId));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + ANONYMOUS + ", " + USER + ")")
	public ResponseEntity<Boolean> hasSubscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid) throws Exception {
		return ResponseEntity.ok(metadataService.hasSubscribeToLinkedDataset(globalId, linkedDatasetUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + ANONYMOUS + ", " + USER + ")")
	public ResponseEntity<Void> subscribeToSelfdataDataset(UUID globalId) throws Exception {
		metadataService.subscribeToSelfdataDataset(globalId);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + ANONYMOUS + ", " + USER + ")")
	public ResponseEntity<Void> subscribeToLinkedDataset(UUID globalId, UUID linkedDatasetUuid) throws Exception {
		metadataService.subscribeToLinkedDataset(globalId, linkedDatasetUuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Boolean> hasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws Exception {
		return ResponseEntity.ok(metadataService.hasSubscribeToMetadataMedia(globalId, mediaId));
	}

	@Override
	public ResponseEntity<List<Metadata>> getMetadatasWithSameTheme(UUID globalId, Integer limit) throws Exception {
		return ResponseEntity.ok(metadataService.getMetadatasWithSameTheme(globalId, limit));
	}

	@Override
	public ResponseEntity<Integer> getNumberOfDatasetsOnTheSameTheme(UUID globalId) throws Exception {
		return ResponseEntity.ok(metadataService.getNumberOfDatasetsOnTheSameTheme(globalId));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSULT + ", " + MODULE_PROJEKT + ", "
			+ MODULE_PROJEKT_ADMINISTRATOR + ", " + MODULE_KALIM + ", " + MODULE_KALIM_ADMINISTRATOR + ", "
			+ MODULE_SELFDATA + ", " + MODULE_SELFDATA_ADMINISTRATOR + ", " + MODULE_KONSULT_ADMINISTRATOR + ")")
	public ResponseEntity<Void> unsubscribeToDataset(UUID globalId, UUID subscriptionOwnerUuid) throws Exception {
		metadataService.unsubscribeToDataset(globalId, subscriptionOwnerUuid);
		return ResponseEntity.status(200).build();
	}

	@Override
	public ResponseEntity<Resource> callServiceMetadataMedia(UUID globalId, UUID mediaId,
			Map<String, String> parameters) throws Exception {
		final DocumentContent documentContent = metadataService.callServiceMetadataMedia(globalId, mediaId, parameters);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}
}
