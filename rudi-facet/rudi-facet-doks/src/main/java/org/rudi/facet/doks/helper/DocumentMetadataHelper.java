package org.rudi.facet.doks.helper;

import java.util.UUID;

import org.rudi.doks.core.bean.DocumentMetadata;
import org.rudi.facet.doks.dao.DocumentDao;
import org.rudi.facet.doks.exceptions.DocumentNotFoundException;
import org.rudi.facet.doks.mapper.DocumentMetadataMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentMetadataHelper {
	private final DocumentDao documentDao;
	private final DocumentMetadataMapper documentMetadataMapper;

	public DocumentMetadata getDocumentMetadata(UUID uuid) throws DocumentNotFoundException {
		var document = documentDao.findByUuid(uuid);
		if (document == null) {
			throw new DocumentNotFoundException(uuid);
		}
		return documentMetadataMapper.entityToDto(document);
	}
}
