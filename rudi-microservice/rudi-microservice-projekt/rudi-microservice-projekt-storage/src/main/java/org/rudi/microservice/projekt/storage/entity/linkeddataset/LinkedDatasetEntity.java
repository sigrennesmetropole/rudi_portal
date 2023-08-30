package org.rudi.microservice.projekt.storage.entity.linkeddataset;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.rudi.facet.bpmn.entity.workflow.AbstractAssetDescriptionEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Datasets (ouverts et restreints) liés au projet
 */
@Entity
@Table(name = "linked_dataset", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString
public class LinkedDatasetEntity extends AbstractAssetDescriptionEntity {

	private static final long serialVersionUID = -7654406330206580256L;
	public static final String FIELD_ID = "id";
	public static final String DATASET_UUID_FIELD = "datasetUuid";
	public static final String DATASET_CONFIDENTIALITY_FIELD = "datasetConfidentiality";
	public static final String STATUS_FIELD = "linkedDatasetStatus";
	public static final String END_DATE_FIELD = "endDate";
	public static final String PROJECT_FK = "project_fk";

	@Column(name = "dataset_uuid", nullable = false, unique = true)
	private UUID datasetUuid;

	@Column(name = "dataset_organization_uuid", nullable = true)
	private UUID datasetOrganisationUuid;

	/**
	 * Commentaire
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Mois/année envisagée de la fin de la validité de la demande d'accès
	 */
	@Column(name = "end_date")
	private LocalDateTime endDate;

	/**
	 * Statut
	 */
	@Column(name = "linked_dataset_status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private LinkedDatasetStatus linkedDatasetStatus = LinkedDatasetStatus.DRAFT;

	/**
	 * Confidentialité du dataset (une copié gardée ici pour éviter de toujours aller le chercher dans le jdd. Conformité vérifiée avec le jdd avant
	 * enregistrement)
	 */
	@Column(name = "dataset_confidentiality", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private DatasetConfidentiality datasetConfidentiality = DatasetConfidentiality.OPENED;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final LinkedDatasetEntity that = (LinkedDatasetEntity) o;
		return Objects.equals(getDatasetUuid(), that.getDatasetUuid());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDatasetUuid());
	}
}
