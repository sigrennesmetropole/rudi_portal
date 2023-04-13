package org.rudi.microservice.konsent.storage.entity.treatmentversion;

import java.time.OffsetDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.rudi.common.storage.entity.AbstractLongIdEntity;
import org.rudi.microservice.konsent.core.common.SchemaConstants;
import org.rudi.microservice.konsent.storage.entity.data.DataManagerEntity;
import org.rudi.microservice.konsent.storage.entity.data.DataRecipientEntity;
import org.rudi.microservice.konsent.storage.entity.data.DictionaryEntryEntity;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treatment_version", schema = SchemaConstants.DATA_SCHEMA)
@Getter
@Setter
@NoArgsConstructor
public class TreatmentVersionEntity extends AbstractLongIdEntity {
	private static final long serialVersionUID = 4513827721992762762L;

	@Column(name = "version", nullable = false)
	private Integer version;

	@Column(name = "creation_date", nullable = false)
	private OffsetDateTime creationDate;

	@Column(name = "updated_date")
	private OffsetDateTime updatedDate;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private TreatmentStatus status;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_mutlilingual_title", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk"))
	private Set<DictionaryEntryEntity> titles;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_mutlilingual_data", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk"))
	private Set<DictionaryEntryEntity> datas;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "retention_fk")
	private RetentionEntity retention;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "purpose_fk")
	private PurposeEntity purpose;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "typology_treatment_fk")
	private TypologyTreatmentEntity typology;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_data_recipient", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "data_recipient_fk"))
	private Set<DataRecipientEntity> dataRecipients;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_security_measure", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "security_measure_fk"))
	private Set<SecurityMeasureEntity> securityMeasures;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_mutlilingual_usage", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk"))
	private Set<DictionaryEntryEntity> usages;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "treatment_version_mutlilingual_operation_nature", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "treatment_version_fk"), inverseJoinColumns = @JoinColumn(name = "dictionary_entry_fk"))
	private Set<DictionaryEntryEntity> operationTreatmentNatures;

	@Column(name = "data_recipient_detail")
	private String dataRecipientDetail;

	@Column(name = "security_measure_detail")
	private String securityMeasureDetail;

	@Column(name = "outside_ue_transfert")
	private String outsideUETransfert;

	@ManyToOne
	@JoinColumn(name = "involved_population_category_fk")
	private InvolvedPopulationCategoryEntity involvedPopulation;

	@Column(name = "obsolete_date")
	private OffsetDateTime obsoleteDate;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "data_protection_officer")
	private DataManagerEntity dataProtectionOfficer;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "data_manager_fk")
	private DataManagerEntity manager;

	@Column(name = "treatment_hash", length = 512)
	private String treatmentHash;

	public TreatmentVersionEntity(TreatmentVersionEntity source) {
		setUuid(source.getUuid());
		setVersion(source.getVersion());
		setCreationDate(source.getCreationDate());
		setUpdatedDate(source.getUpdatedDate());
		setStatus(source.getStatus());
		setTitles(source.getTitles());
		setDatas(source.getDatas());
		setRetention(source.getRetention());
		setPurpose(source.getPurpose());
		setTypology(source.getTypology());
		setDataRecipients(source.getDataRecipients());
		setSecurityMeasures(source.getSecurityMeasures());
		setUsages(source.getUsages());
		setOperationTreatmentNatures(source.getOperationTreatmentNatures());
		setDataRecipientDetail(source.getDataRecipientDetail());
		setSecurityMeasureDetail(source.getSecurityMeasureDetail());
		setOutsideUETransfert(source.getOutsideUETransfert());
		setInvolvedPopulation(source.getInvolvedPopulation());
		setObsoleteDate(source.getObsoleteDate());
		setDataProtectionOfficer(source.getDataProtectionOfficer());
		setManager(source.getManager());
		setTreatmentHash(source.getTreatmentHash());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TreatmentVersionEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
