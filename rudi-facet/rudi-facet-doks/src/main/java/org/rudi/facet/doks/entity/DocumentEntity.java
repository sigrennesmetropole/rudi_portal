package org.rudi.facet.doks.entity;

import java.sql.Blob;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "document") // Le schéma est défini dans la propriété spring.jpa.properties.hibernate.default_schema
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public
class DocumentEntity extends AbstractLongIdEntity {

	private static final long serialVersionUID = -3428582644372910355L;

	@Column(nullable = false, length = 150)
	private String fileName;

	@Column(nullable = false, length = 150)
	private String contentType;

	/**
	 * Taille du fichier original, peut être différente de fileContents si fileContents est chiffré (encrypted)
	 */
	@Column(nullable = false)
	private long fileSize;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(nullable = false)
	@SuppressWarnings("java:S1948") // Le type Blob ne peut pas être modifié pour être Serializable
	@ToString.Exclude
	private Blob fileContents;

	@Column(nullable = false)
	@Builder.Default
	private boolean encrypted = false;

	/**
	 * L'UUID du User qui a uploadé le document
	 */
	@Column(nullable = false)
	private UUID uploaderUuid;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DocumentEntity)) return false;
		if (!super.equals(o)) return false;
		final DocumentEntity that = (DocumentEntity) o;
		return fileSize == that.fileSize && Objects.equals(fileName, that.fileName) && Objects.equals(contentType, that.contentType) && Objects.equals(fileContents, that.fileContents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), fileName, contentType, fileSize, fileContents);
	}
}
