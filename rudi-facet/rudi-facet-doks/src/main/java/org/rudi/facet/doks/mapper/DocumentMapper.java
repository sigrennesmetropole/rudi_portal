package org.rudi.facet.doks.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.BlobProxy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.facet.doks.entity.DocumentEntity;

import lombok.SneakyThrows;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper extends AbstractMapper<DocumentEntity, DocumentContent> {

	@Override
	@SneakyThrows
	default DocumentEntity dtoToEntity(DocumentContent dto) {
		return DocumentEntity.builder()
				.fileName(dto.getFileName())
				.contentType(dto.getContentType())
				.fileSize(dto.getFileSize())
				.fileContents(toFileContents(dto.getFileStream(), dto.getFileSize()))
				.build();
	}

	default Blob toFileContents(InputStream inputStream, long fileSize) {
		return BlobProxy.generateProxy(inputStream, fileSize);
	}

	@Override
	@SneakyThrows
	default DocumentContent entityToDto(DocumentEntity entity) {
		return new DocumentContent(entity.getFileName(), entity.getContentType(), entity.getFileSize(), getInputStream(entity));
	}

	default InputStream getInputStream(DocumentEntity entity) throws SQLException {
		return entity.getFileContents().getBinaryStream();
	}

	default Blob toFileContents(Path filePath) throws IOException {
		@SuppressWarnings("java:S2095") // Il ne faut pas fermer l'InputStream car le Blob va s'en servir
		final var inputStream = Files.newInputStream(filePath);
		return toFileContents(inputStream, Files.size(filePath));
	}
}
