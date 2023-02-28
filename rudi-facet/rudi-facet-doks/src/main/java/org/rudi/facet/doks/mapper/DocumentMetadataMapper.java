package org.rudi.facet.doks.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.doks.core.bean.DocumentMetadata;
import org.rudi.facet.doks.entity.DocumentEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMetadataMapper extends AbstractMapper<DocumentEntity, DocumentMetadata> {
	/**
	 * Converti un DocumentEntity en DocumentMetadata.
	 *
	 * @param entity entity to transform to dto
	 * @return DocumentMetadata
	 */
	@Override
	default DocumentMetadata entityToDto(DocumentEntity entity) {
		DocumentMetadata documentMetadata = new DocumentMetadata();
		documentMetadata.setName(entity.getFileName());
		documentMetadata.setContentType(entity.getContentType());
		documentMetadata.setSize(new BigDecimal(entity.getFileSize()));
		return documentMetadata;
	}
}
