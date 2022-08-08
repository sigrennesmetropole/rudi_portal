package org.rudi.facet.kaccess.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.FieldSpecNamingCase;
import org.rudi.facet.dataverse.fields.RootFieldSpec;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.MediaSeries;
import org.rudi.facet.kaccess.bean.Metadata;

import javax.annotation.Nonnull;

import static org.rudi.facet.kaccess.constant.ConstantMetadata.LANG_FIELD_LOCAL_NAME;
import static org.rudi.facet.kaccess.constant.ConstantMetadata.TEXT_FIELD_LOCAL_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RudiMetadataField {

	private static final String ORGANIZATION_NAME = "organizationName";
	private static final String AVAILABLE_FORMATS = "availableFormats";

	/**
	 * Regroupe tous les champs RUDI.
	 * Se traduit par un {@link org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement} côté Dataverse.
	 */
	public static final FieldSpec ROOT = new RootFieldSpec(Metadata.class, "rudi", FieldSpecNamingCase.SNAKE_CASE);

	public static final FieldSpec GLOBAL_ID = ROOT.newChildFromJavaField("globalId");
	public static final FieldSpec LOCAL_ID = ROOT.newChildFromJavaField("localId");
	public static final FieldSpec DOI = ROOT.newChildFromJavaField("doi");
	/**
	 * Correspond au champ DatasetVersion#datasetPersistentId côté Dataverse mais n'existe pas dans les blocks
	 */
	public static final FieldSpec DATAVERSE_DOI = ROOT.newChildFromJavaField("dataverseDoi");
	public static final FieldSpec RESOURCE_TITLE = ROOT.newChildFromJavaField("resourceTitle")
			.isSortable(false);

	// abstract renommé en synopsis : l'identifiant rudi_abstract est conservé cause impact dataverse
	public static final FieldSpec SYNOPSIS = ROOT.newChildFromJavaField("synopsis", "abstract");
	public static final FieldSpec SYNOPSIS_LANGUAGE = SYNOPSIS.newChildFromJavaField("lang", "language");
	public static final FieldSpec SYNOPSIS_TEXT = SYNOPSIS.newChildFromJavaField("text");

	public static final FieldSpec SUMMARY = ROOT.newChildFromJavaField("summary");
	public static final FieldSpec SUMMARY_LANGUAGE = SUMMARY.newChildFromJavaField("lang", "language");
	public static final FieldSpec SUMMARY_TEXT = SUMMARY.newChildFromJavaField("text");

	public static final FieldSpec THEME = ROOT.newChildFromJavaField("theme").isSortable(false);
	public static final FieldSpec KEYWORDS = ROOT.newChildFromJavaField("keywords").isSortable(false);

	public static final FieldSpec CONTACT = ROOT.newChildFromJavaField("contacts", "contact");
	public static final FieldSpec CONTACT_ID = CONTACT.newChildFromJavaField("contactId", "id");
	public static final FieldSpec CONTACT_ORGANIZATION_NAME = CONTACT.newChildFromJavaField(ORGANIZATION_NAME);
	public static final FieldSpec CONTACT_NAME = CONTACT.newChildFromJavaField("contactName", "name");
	public static final FieldSpec CONTACT_ROLE = CONTACT.newChildFromJavaField("role");
	public static final FieldSpec CONTACT_EMAIL = CONTACT.newChildFromJavaField("email");

	public static final FieldSpec PRODUCER = ROOT.newChildFromJavaField("producer");
	public static final FieldSpec PRODUCER_ORGANIZATION_ID = PRODUCER.newChildFromJavaField("organizationId");
	public static final FieldSpec PRODUCER_ORGANIZATION_NAME = PRODUCER.newChildFromJavaField(ORGANIZATION_NAME)
			.isSortable(true);
	public static final FieldSpec PRODUCER_ORGANIZATION_ADDRESS = PRODUCER.newChildFromJavaField("organizationAddress");

	public static final FieldSpec MEDIA = ROOT.newChildFromJavaField(AVAILABLE_FORMATS, "media");
	public static final FieldSpec MEDIA_ID = MEDIA.newChildFromJavaField("mediaId", "id");
	public static final FieldSpec MEDIA_TYPE = MEDIA.newChildFromJavaField("mediaType", "type").controlledVocabulary();
	public static final FieldSpec MEDIA_CONNECTOR = MEDIA.newChildFromJavaField("connector");
	public static final FieldSpec MEDIA_CONNECTOR_URL = MEDIA_CONNECTOR.newChildFromJavaField("url");
	public static final FieldSpec MEDIA_CONNECTOR_INTERFACE_CONTRACT = MEDIA_CONNECTOR.newChildFromJavaField("interfaceContract");

	public static final FieldSpec FILE = ROOT.newChildFromJavaField(AVAILABLE_FORMATS, "mediafile");
	public static final FieldSpec FILE_STRUCTURE = FILE.newChildFromJavaField(MediaFile.class, "fileStructure", "structure");
	public static final FieldSpec FILE_SIZE = FILE.newChildFromJavaField(MediaFile.class, "fileSize", "size");
	public static final FieldSpec FILE_TYPE = FILE.newChildFromJavaField(MediaFile.class, "fileType", "type");
	public static final FieldSpec FILE_ENCODING = FILE.newChildFromJavaField(MediaFile.class, "fileEncoding", "encoding");
	public static final FieldSpec FILE_CHECKSUM = FILE.newChildFromJavaField(MediaFile.class, "checksum", "checksum");
	public static final FieldSpec FILE_CHECKSUM_ALGO = FILE_CHECKSUM.newChildFromJavaField("algo");
	public static final FieldSpec FILE_CHECKSUM_HASH = FILE_CHECKSUM.newChildFromJavaField("hash");

	public static final FieldSpec SERIES = ROOT.newChildFromJavaField(AVAILABLE_FORMATS, "mediaseries");
	public static final FieldSpec SERIES_LATENCY = SERIES.newChildFromJavaField(MediaSeries.class, "latency");
	public static final FieldSpec SERIES_PERIOD = SERIES.newChildFromJavaField(MediaSeries.class, "period");
	public static final FieldSpec SERIES_CURENT_NUMBER_OF_RECORDS = SERIES.newChildFromJavaField(MediaSeries.class, "currentNumberOfRecords");
	public static final FieldSpec SERIES_CURENT_SIZE = SERIES.newChildFromJavaField(MediaSeries.class, "currentSize");
	public static final FieldSpec SERIES_TOTAL_NUMBER_OF_RECORDS = SERIES.newChildFromJavaField(MediaSeries.class, "totalNumberOfRecords");
	public static final FieldSpec SERIES_TOTAL_SIZE = SERIES.newChildFromJavaField(MediaSeries.class, "totalSize");

	public static final FieldSpec RESOURCE_LANGUAGES = ROOT.newChildFromJavaField("resourceLanguages", "resource_language");

	public static final FieldSpec TEMPORAL_SPREAD = ROOT.newChildFromJavaField("temporalSpread");
	public static final FieldSpec TEMPORAL_SPREAD_START_DATE = TEMPORAL_SPREAD.newChildFromJavaField("startDate");
	public static final FieldSpec TEMPORAL_SPREAD_END_DATE = TEMPORAL_SPREAD.newChildFromJavaField("endDate");

	public static final FieldSpec GEOGRAPHY = ROOT.newChildFromJavaField("geography");
	public static final FieldSpec GEOGRAPHY_BOUNDING_BOX = GEOGRAPHY.newChildFromJavaField("boundingBox");
	public static final FieldSpec GEOGRAPHY_BOUNDING_BOX_WEST_LONGITUDE = GEOGRAPHY_BOUNDING_BOX.newChildFromJavaField("westLongitude");
	public static final FieldSpec GEOGRAPHY_BOUNDING_BOX_EAST_LONGITUDE = GEOGRAPHY_BOUNDING_BOX.newChildFromJavaField("eastLongitude");
	public static final FieldSpec GEOGRAPHY_BOUNDING_BOX_NORTH_LATITUDE = GEOGRAPHY_BOUNDING_BOX.newChildFromJavaField("northLatitude");
	public static final FieldSpec GEOGRAPHY_BOUNDING_BOX_SOUTH_LATITUDE = GEOGRAPHY_BOUNDING_BOX.newChildFromJavaField("southLatitude");
	public static final FieldSpec GEOGRAPHY_GEOGRAPHIC_DISTRIBUTION = GEOGRAPHY.newChildFromJavaField("geographicDistribution");
	public static final FieldSpec GEOGRAPHY_PROJECTION = GEOGRAPHY.newChildFromJavaField("projection");

	public static final FieldSpec DATASET_SIZE = ROOT.newChildFromJavaField("datasetSize");
	public static final FieldSpec DATASET_SIZE_NUMBER_OF_RECORDS = DATASET_SIZE.newChildFromJavaField("numbersOfRecords");
	public static final FieldSpec DATASET_SIZE_NUMBER_OF_FIELDS = DATASET_SIZE.newChildFromJavaField("numberOfFields");

	public static final FieldSpec DATASET_DATES = ROOT.newChildFromJavaField("datasetDates");
	public static final FieldSpec DATASET_DATES_CREATED = DATASET_DATES.newChildFromJavaField("created").isSortable(false);
	public static final FieldSpec DATASET_DATES_VALIDATED = DATASET_DATES.newChildFromJavaField("validated").isSortable(false);
	public static final FieldSpec DATASET_DATES_PUBLISHED = DATASET_DATES.newChildFromJavaField("published").isSortable(false);
	public static final FieldSpec DATASET_DATES_UPDATED = DATASET_DATES.newChildFromJavaField("updated")
			.isSortable(false);
	public static final FieldSpec DATASET_DATES_DELETED = DATASET_DATES.newChildFromJavaField("deleted");

	public static final FieldSpec STORAGE_STATUS = RudiMetadataField.ROOT.newChildFromJavaField("storageStatus");

	public static final FieldSpec METADATA_INFO = ROOT.newChildFromJavaField("metadataInfo");
	public static final FieldSpec METADATA_INFO_API_VERSION = METADATA_INFO.newChildFromJavaField("apiVersion");

	public static final FieldSpec METADATA_INFO_DATES = METADATA_INFO.newChildFromJavaField("metadataDates", "dates");
	public static final FieldSpec METADATA_INFO_DATES_CREATED = METADATA_INFO_DATES.newChildFromJavaField("created");
	public static final FieldSpec METADATA_INFO_DATES_VALIDATED = METADATA_INFO_DATES.newChildFromJavaField("validated");
	public static final FieldSpec METADATA_INFO_DATES_PUBLISHED = METADATA_INFO_DATES.newChildFromJavaField("published");
	public static final FieldSpec METADATA_INFO_DATES_UPDATED = METADATA_INFO_DATES.newChildFromJavaField("updated");
	public static final FieldSpec METADATA_INFO_DATES_DELETED = METADATA_INFO_DATES.newChildFromJavaField("deleted");

	public static final FieldSpec METADATA_INFO_PROVIDER = METADATA_INFO.newChildFromJavaField("metadataProvider", "provider");
	public static final FieldSpec METADATA_INFO_PROVIDER_ORGANIZATION_ID = METADATA_INFO_PROVIDER.newChildFromJavaField("organizationId");
	public static final FieldSpec METADATA_INFO_PROVIDER_ORGANIZATION_NAME = METADATA_INFO_PROVIDER.newChildFromJavaField(ORGANIZATION_NAME);
	public static final FieldSpec METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS = METADATA_INFO_PROVIDER.newChildFromJavaField("organizationAddress");

	public static final FieldSpec METADATA_INFO_CONTACT = METADATA_INFO.newChildFromJavaField("metadataContacts", "contact");
	public static final FieldSpec METADATA_INFO_CONTACT_ID = METADATA_INFO_CONTACT.newChildFromJavaField("contactId", "id");
	public static final FieldSpec METADATA_INFO_CONTACT_ORGANIZATION_NAME = METADATA_INFO_CONTACT.newChildFromJavaField(ORGANIZATION_NAME);
	public static final FieldSpec METADATA_INFO_CONTACT_NAME = METADATA_INFO_CONTACT.newChildFromJavaField("contactName", "name");
	public static final FieldSpec METADATA_INFO_CONTACT_ROLE = METADATA_INFO_CONTACT.newChildFromJavaField("role");
	public static final FieldSpec METADATA_INFO_CONTACT_EMAIL = METADATA_INFO_CONTACT.newChildFromJavaField("email");

	public static final FieldSpec ACCESS_CONDITION = ROOT.newChildFromJavaField("accessCondition");

	public static final FieldSpec CONFIDENTIALITY = ACCESS_CONDITION.newChildFromJavaField("confidentiality");
	public static final FieldSpec RESTRICTED_ACCESS = CONFIDENTIALITY.newChildFromJavaField("restrictedAccess")
			.defaultValueIfMissing(false);
	public static final FieldSpec GDPR_SENSITIVE = CONFIDENTIALITY.newChildFromJavaField("gdprSensitive");

	public static final FieldSpec LICENCE = ACCESS_CONDITION.newChildFromJavaField("licence");

	public static final FieldSpec LICENCE_TYPE = LICENCE.newChildFromJavaField("licenceType").controlledVocabulary();
	public static final FieldSpec LICENCE_LABEL = LICENCE.newChildFromJavaField(LicenceStandard.class, "licenceLabel");

	public static final FieldSpec CUSTOM_LICENCE_URI = LICENCE.newChildFromJavaField(LicenceCustom.class, "customLicenceUri");
	public static final FieldSpec CUSTOM_LICENCE_LABEL = LICENCE.newChildFromJavaField(LicenceCustom.class, "customLicenceLabel");

	public static final FieldSpec USAGE_CONSTRAINT = ACCESS_CONDITION.newChildFromJavaField("usageConstraint");
	public static final FieldSpec BIBLIOGRAPHICAL_REFERENCE = ACCESS_CONDITION.newChildFromJavaField("bibliographicalReference");
	public static final FieldSpec MANDATORY_MENTION = ACCESS_CONDITION.newChildFromJavaField("mandatoryMention");
	public static final FieldSpec ACCESS_CONSTRAINT = ACCESS_CONDITION.newChildFromJavaField("accessConstraint");
	public static final FieldSpec OTHER_CONSTRAINTS = ACCESS_CONDITION.newChildFromJavaField("otherConstraints");

	public static final DatasetMetadataBlockElementSpec RUDI_ELEMENT_SPEC = new DatasetMetadataBlockElementSpec(ROOT)
			.add(GLOBAL_ID)
			.add(LOCAL_ID)
			.add(RESOURCE_TITLE)
			.add(SYNOPSIS,
					SYNOPSIS_LANGUAGE,
					SYNOPSIS_TEXT)
			.add(SUMMARY,
					SUMMARY_LANGUAGE,
					SUMMARY_TEXT)
			.add(THEME)
			.add(KEYWORDS)
			.add(PRODUCER,
					PRODUCER_ORGANIZATION_ID,
					PRODUCER_ORGANIZATION_NAME,
					PRODUCER_ORGANIZATION_ADDRESS)
			.add(CONTACT,
					CONTACT_ID,
					CONTACT_ORGANIZATION_NAME,
					CONTACT_NAME,
					CONTACT_ROLE,
					CONTACT_EMAIL)
			.add(MEDIA,
					MEDIA_ID,
					MEDIA_TYPE,
					MEDIA_CONNECTOR_URL,
					MEDIA_CONNECTOR_INTERFACE_CONTRACT,
					FILE_STRUCTURE,
					FILE_SIZE,
					FILE_TYPE,
					FILE_ENCODING,
					FILE_CHECKSUM_ALGO,
					FILE_CHECKSUM_HASH,
					SERIES_LATENCY,
					SERIES_PERIOD,
					SERIES_CURENT_NUMBER_OF_RECORDS,
					SERIES_CURENT_SIZE,
					SERIES_TOTAL_NUMBER_OF_RECORDS,
					SERIES_TOTAL_SIZE)
			.add(RESOURCE_LANGUAGES)
			.add(TEMPORAL_SPREAD,
					TEMPORAL_SPREAD_START_DATE,
					TEMPORAL_SPREAD_END_DATE
			)
			.add(GEOGRAPHY,
					GEOGRAPHY_BOUNDING_BOX_WEST_LONGITUDE,
					GEOGRAPHY_BOUNDING_BOX_EAST_LONGITUDE,
					GEOGRAPHY_BOUNDING_BOX_NORTH_LATITUDE,
					GEOGRAPHY_BOUNDING_BOX_SOUTH_LATITUDE,
					GEOGRAPHY_GEOGRAPHIC_DISTRIBUTION,
					GEOGRAPHY_PROJECTION
			)
			.add(DATASET_SIZE,
					DATASET_SIZE_NUMBER_OF_RECORDS,
					DATASET_SIZE_NUMBER_OF_FIELDS

			)
			.add(DATASET_DATES,
					DATASET_DATES_CREATED,
					DATASET_DATES_VALIDATED,
					DATASET_DATES_PUBLISHED,
					DATASET_DATES_UPDATED,
					DATASET_DATES_DELETED

			)
			.add(STORAGE_STATUS)
			.add(METADATA_INFO,
					METADATA_INFO_API_VERSION,
					METADATA_INFO_DATES_CREATED,
					METADATA_INFO_DATES_VALIDATED,
					METADATA_INFO_DATES_PUBLISHED,
					METADATA_INFO_DATES_UPDATED,
					METADATA_INFO_DATES_DELETED,
					METADATA_INFO_PROVIDER_ORGANIZATION_ID,
					METADATA_INFO_PROVIDER_ORGANIZATION_NAME,
					METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS
			)
			.add(METADATA_INFO_CONTACT,
					METADATA_INFO_CONTACT_ID,
					METADATA_INFO_CONTACT_ORGANIZATION_NAME,
					METADATA_INFO_CONTACT_NAME,
					METADATA_INFO_CONTACT_ROLE,
					METADATA_INFO_CONTACT_EMAIL
			)
			.add(ACCESS_CONDITION,
					RESTRICTED_ACCESS,
					GDPR_SENSITIVE,
					LICENCE_TYPE,
					LICENCE_LABEL,
					CUSTOM_LICENCE_URI)
			.add(CUSTOM_LICENCE_LABEL,
					getDictionaryFieldSpecs(CUSTOM_LICENCE_LABEL))
			.add(USAGE_CONSTRAINT,
					getDictionaryFieldSpecs(USAGE_CONSTRAINT))
			.add(BIBLIOGRAPHICAL_REFERENCE,
					getDictionaryFieldSpecs(BIBLIOGRAPHICAL_REFERENCE))
			.add(MANDATORY_MENTION,
					getDictionaryFieldSpecs(MANDATORY_MENTION))
			.add(ACCESS_CONSTRAINT,
					getDictionaryFieldSpecs(ACCESS_CONSTRAINT))
			.add(OTHER_CONSTRAINTS,
					getDictionaryFieldSpecs(OTHER_CONSTRAINTS));

	@Nonnull
	private static FieldSpec[] getDictionaryFieldSpecs(FieldSpec parentSpec) {
		return new FieldSpec[]{
				parentSpec.newChildFromJavaField(DictionaryEntry.class, LANG_FIELD_LOCAL_NAME),
				parentSpec.newChildFromJavaField(DictionaryEntry.class, TEXT_FIELD_LOCAL_NAME)
		};
	}

}
