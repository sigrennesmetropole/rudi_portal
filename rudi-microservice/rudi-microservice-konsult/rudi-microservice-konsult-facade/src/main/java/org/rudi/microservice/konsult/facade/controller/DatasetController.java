package org.rudi.microservice.konsult.facade.controller;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DatasetController implements DatasetsApi {

	private final MetadataService metadataService;
	private final ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<MetadataList> searchMetadatas(Integer limit, Integer offset, String freeText,
			List<String> themes, List<String> keywords, List<String> producerNames, LocalDateTime dateDebut,
			LocalDateTime dateFin, String order, Boolean restrictedAccess) throws Exception {

		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria()
				.limit(limit)
				.offset(offset)
				.freeText(freeText)
				.keywords(keywords)
				.themes(themes)
				.producerNames(producerNames)
				.dateDebut(dateDebut)
				.dateFin(dateFin)
				.order(order)
				.restrictedAccess(restrictedAccess);

		return ResponseEntity.ok(metadataService.searchMetadatas(datasetSearchCriteria));
	}

	@Override
	public ResponseEntity<MetadataFacets> searchMetadataFacets(@Valid List<String> facets) throws Exception {
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
	public ResponseEntity<Boolean> hasSubscribeToMetadataMedia(UUID globalId, UUID mediaId) throws Exception {
		return ResponseEntity.ok(metadataService.hasSubscribeToMetadataMedia(globalId, mediaId));
	}
}
