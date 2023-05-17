package org.rudi.microservice.projekt.storage.entity.project;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.rudi.common.storage.entity.SkosConceptCodeColumn;
import org.rudi.facet.bpmn.entity.workflow.AbstractAssetDescriptionEntity;
import org.rudi.microservice.projekt.core.common.SchemaConstants;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.rudi.microservice.projekt.storage.entity.TargetAudienceEntity;
import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project entity
 */
@Entity
@Table(name = "project", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@ToString(exclude = { "desiredSupports", "linkedDatasets", "datasetRequests" })
public class ProjectEntity extends AbstractAssetDescriptionEntity {

	private static final long serialVersionUID = -6508639499690690560L;
	public static final String FIELD_OWNER_UUID = "ownerUuid";
	public static final String FIELD_DATASET_REQUESTS = "datasetRequests";
	public static final String FIELD_LINKED_DATASET = "linkedDatasets";
	public static final String FIELD_ID = "id";


	/**
	 * Titre
	 */
	@Column(length = 150, nullable = false)
	private String title;

	/**
	 * Statut
	 */
	@Column(name = "project_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProjectStatus projectStatus = ProjectStatus.DRAFT;

	/**
	 * Mois/année du début de la période envisagée de réalisation du projet
	 */
	@Column(name = "expected_completion_start_date")
	private LocalDateTime expectedCompletionStartDate;

	/**
	 * Mois/année dé la fin de la période envisagée de réalisation du projet
	 */
	@Column(name = "expected_completion_end_date")
	private LocalDateTime expectedCompletionEndDate;

	/**
	 * Thèmes du jeu de données (codes SKOS)
	 */
	@ElementCollection
	@CollectionTable(name = "project_theme", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "project_fk"))
	@Column(name = SkosConceptCodeColumn.NAME, length = SkosConceptCodeColumn.LENGTH)
	private Set<String> themes = new HashSet<>();

	/**
	 * Mots-clés du projet (codes SKOS)
	 */
	@ElementCollection
	@CollectionTable(name = "project_keyword", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "project_fk"))
	@Column(name = SkosConceptCodeColumn.NAME, length = SkosConceptCodeColumn.LENGTH)
	private Set<String> keywords = new HashSet<>();

	/**
	 * Type de projet
	 */
	@ManyToOne
	@JoinColumn(name = "type_fk")
	private ProjectTypeEntity type;

	/**
	 * URL d'accès
	 */
	private String accessUrl;

	/**
	 * Public cible
	 */
	@ManyToMany
	@JoinTable(name = "project_audience", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "project_fk"), inverseJoinColumns = @JoinColumn(name = "target_audience_fk"))
	private Set<TargetAudienceEntity> targetAudiences = new HashSet<>();

	/**
	 * Échelle du territoire
	 */
	@ManyToOne
	@JoinColumn(name = "territorial_scale_fk")
	private TerritorialScaleEntity territorialScale;

	/**
	 * Précision sur l'échelle du territoire
	 */
	@Column(name = "detailed_territorial_scale", length = 150)
	private String detailedTerritorialScale;

	/**
	 * Type d’accompagnement souhaité
	 */
	@ManyToMany
	@JoinTable(name = "project_support", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "project_fk"), inverseJoinColumns = @JoinColumn(name = "support_fk"))
	private Set<SupportEntity> desiredSupports = new HashSet<>();

	/**
	 * Description de l’accompagnement sollicité
	 */
	@Column(name = "desired_support_description", length = 150)
	private String desiredSupportDescription;

	/**
	 * Caractère confidentiel du projet (par défaut : confidentiel)
	 */
	@ManyToOne
	@JoinColumn(name = "confidentiality_fk", nullable = false)
	private ConfidentialityEntity confidentiality;

	/**
	 * UUID de l'utilisateur dans ACL du porteur de projet (si owner_type = USER) ou de l'organization dans strukture (si owner_type = ORGANIZATION).
	 * Contact : utilisateur soumettant l’idée
	 */
	@Column(name = "owner_uuid", nullable = false)
	private UUID ownerUuid;

	/**
	 * Adresse mail de contact (peut être différente du porteur de projet)
	 */
	@Column(name = "contact_email", length = 100, nullable = false)
	private String contactEmail;

	/**
	 * Au nom de qui le projet a-t-il été créé ?
	 */
	@Column(name = "owner_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private OwnerType ownerType;

	/**
	 * Liste des datasets liés (ou demandés à être lié pour jdd restreint) au projet
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = LinkedDatasetEntity.PROJECT_FK)
	private Set<LinkedDatasetEntity> linkedDatasets = new HashSet<>();

	/**
	 * Liste des demandes de nouvelles données associées
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = LinkedDatasetEntity.PROJECT_FK)
	private Set<NewDatasetRequestEntity> datasetRequests = new HashSet<>();

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProjectEntity)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * @return <code>true</code> si le projet est en fait une réutilisation
	 */
	@Transient
	public boolean isAReuse() {
		// Les types d’accompagnement souhaités sont obligatoires pour un projet
		// On considère donc que s'ils sont vides, alors le projet est une réutilisation
		return CollectionUtils.isEmpty(getDesiredSupports());
	}

	// Utilisé par le mapper MapStruct ProjectMapper
	@Transient
	public boolean getIsAReuse() {
		return isAReuse();
	}

}
