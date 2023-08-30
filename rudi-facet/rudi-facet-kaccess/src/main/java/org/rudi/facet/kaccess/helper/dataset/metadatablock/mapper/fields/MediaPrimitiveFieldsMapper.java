package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_CHECKSUM_ALGO;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_CHECKSUM_HASH;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_ENCODING;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_STRUCTURE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CAPTION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CONNECTOR_INTERFACE_CONTRACT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CONNECTOR_PARAMETERS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CONNECTOR_URL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_CREATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_DELETED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_EXPIRES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_PUBLISHED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_DATES_VALIDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_VISUAL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_CURENT_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_CURENT_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_LATENCY;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_PERIOD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_TOTAL_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_TOTAL_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERVICE_API_DOCUMENTATION_URL;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.ConnectorConnectorParametersInner;
import org.rudi.facet.kaccess.bean.HashAlgorithm;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.MediaFileAllOfChecksum;
import org.rudi.facet.kaccess.bean.MediaSeries;
import org.rudi.facet.kaccess.bean.MediaService;
import org.rudi.facet.kaccess.bean.MediaType;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
class MediaPrimitiveFieldsMapper extends PrimitiveFieldsMapper<Media> {

	MediaPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper,
			DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(Media media, Map<String, Object> fields) throws DataverseMappingException {
		createField(MEDIA_ID, media.getMediaId().toString(), fields);
		createField(MEDIA_NAME, media.getMediaName(), fields);
		createDatesFields(media.getMediaDates(), fields, MEDIA_DATES_CREATED, MEDIA_DATES_VALIDATED,
				MEDIA_DATES_PUBLISHED, MEDIA_DATES_UPDATED, MEDIA_DATES_EXPIRES, MEDIA_DATES_DELETED);
		createField(MEDIA_CAPTION, media.getMediaCaption(), fields);
		createField(MEDIA_VISUAL, media.getMediaVisual(), fields);
		createField(MEDIA_TYPE, media.getMediaType().getValue(), fields);

		createConnectorFields(media.getConnector(), fields);

		if (Media.MediaTypeEnum.FILE.equals(media.getMediaType()) && media instanceof MediaFile) {
			createFileFields((MediaFile) media, fields);
		} else if (Media.MediaTypeEnum.SERIES.equals(media.getMediaType()) && media instanceof MediaSeries) {
			createSeriesFields((MediaSeries) media, fields);
		} else if (Media.MediaTypeEnum.SERVICE.equals(media.getMediaType()) && media instanceof MediaService) {
			createServiceFields((MediaService) media, fields);
		}
	}

	private void createConnectorFields(@Nullable Connector connector, Map<String, Object> fields)
			throws DataverseMappingException {
		if (connector != null) {
			createField(MEDIA_CONNECTOR_URL, connector.getUrl(), fields);
			createField(MEDIA_CONNECTOR_INTERFACE_CONTRACT, connector.getInterfaceContract(), fields);
			createField(MEDIA_CONNECTOR_PARAMETERS, connector.getConnectorParameters(), fields);
		}
	}

	private void createFileFields(MediaFile file, Map<String, Object> fields) throws DataverseMappingException {
		if (file.getFileStructure() != null) {
			createField(FILE_STRUCTURE, file.getFileStructure(), fields);
		}
		createField(FILE_SIZE, file.getFileSize(), fields);
		createField(FILE_TYPE, file.getFileType().getValue(), fields);
		createField(FILE_ENCODING, file.getFileEncoding(), fields);

		// propriétés de Checksum
		createChecksumFileFields(file, fields);
	}

	private void createChecksumFileFields(MediaFile file, Map<String, Object> fields) throws DataverseMappingException {
		Objects.requireNonNull(file.getChecksum(),
				MessageUtils.buildErrorMessageRequiredMandatoryAttributes(FILE_CHECKSUM_ALGO));
		Objects.requireNonNull(file.getChecksum().getAlgo(),
				MessageUtils.buildErrorMessageRequiredMandatoryAttributes(FILE_CHECKSUM_ALGO));

		createField(FILE_CHECKSUM_ALGO, file.getChecksum().getAlgo().getValue(), fields);
		createField(FILE_CHECKSUM_HASH, file.getChecksum().getHash(), fields);
	}

	private void createSeriesFields(MediaSeries series, Map<String, Object> fields) throws DataverseMappingException {
		createField(SERIES_LATENCY, series.getLatency(), fields);
		createField(SERIES_PERIOD, series.getPeriod(), fields);
		createField(SERIES_CURENT_NUMBER_OF_RECORDS, series.getCurrentNumberOfRecords(), fields);
		createField(SERIES_CURENT_SIZE, series.getCurrentSize(), fields);
		createField(SERIES_TOTAL_NUMBER_OF_RECORDS, series.getTotalNumberOfRecords(), fields);
		createField(SERIES_TOTAL_SIZE, series.getTotalSize(), fields);
	}

	private void createServiceFields(MediaService service, Map<String, Object> fields)
			throws DataverseMappingException {
		createField(SERVICE_API_DOCUMENTATION_URL, service.getApiDocumentationUrl(), fields);
	}

	@Nonnull
	@Override
	public Media fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException {
		final Media media;

		final var mediaTypeField = fields.get(MEDIA_TYPE);
		final var mediaType = mediaTypeField.getValueAsEnumWith(Media.MediaTypeEnum::fromValue);
		if (Media.MediaTypeEnum.FILE.equals(mediaType)) {
			media = getMediaFile(fields);
		} else if (Media.MediaTypeEnum.SERIES.equals(mediaType)) {
			media = getMediaSeries(fields);
		} else if (Media.MediaTypeEnum.SERVICE.equals(mediaType)) {
			media = getMediaService(fields);
		} else {
			throw new NotImplementedException("Media Type not handled : " + mediaType);
		}

		return media.mediaId(fields.get(MEDIA_ID).getValueAsUUID()).mediaName(fields.get(MEDIA_NAME).getValueAsString())
				.mediaDates(buildDates(fields)).mediaType(mediaType).connector(buildConnector(fields))
				.mediaCaption(fields.get(MEDIA_CAPTION).getValueAsString())
				.mediaVisual(fields.get(MEDIA_VISUAL).getValueAsString());
	}

	@Nullable
	private ReferenceDates buildDates(MapOfFields fields) {
		final var dates = new ReferenceDates()
				.created(fields.get(MEDIA_DATES_CREATED).getValueAsOffsetDateTime(dateTimeMapper))
				.validated(fields.get(MEDIA_DATES_VALIDATED).getValueAsOffsetDateTime(dateTimeMapper))
				.published(fields.get(MEDIA_DATES_PUBLISHED).getValueAsOffsetDateTime(dateTimeMapper))
				.updated(fields.get(MEDIA_DATES_UPDATED).getValueAsOffsetDateTime(dateTimeMapper))
				.expires(fields.get(MEDIA_DATES_EXPIRES).getValueAsOffsetDateTime(dateTimeMapper))
				.deleted(fields.get(MEDIA_DATES_DELETED).getValueAsOffsetDateTime(dateTimeMapper));
		return ObjectsUtils.nullIfEmpty(dates);
	}

	private Connector buildConnector(MapOfFields fields) throws DataverseMappingException {
		return new Connector().interfaceContract(fields.get(MEDIA_CONNECTOR_INTERFACE_CONTRACT).getValueAsString())
				.connectorParameters(fields.get(MEDIA_CONNECTOR_PARAMETERS)
						.getValueAsListOf(ConnectorConnectorParametersInner.class, objectMapper))
				.url(fields.get(MEDIA_CONNECTOR_URL).getValueAsString());
	}

	@Nonnull
	private MediaFile getMediaFile(MapOfFields fields) {
		return new MediaFile().fileStructure(fields.get(FILE_STRUCTURE).getValueAsString())
				.fileSize(fields.get(FILE_SIZE).getValueAsLong())
				.fileType(fields.get(FILE_TYPE).getValueAsEnumWith(MediaType::fromValue))
				.fileEncoding(fields.get(FILE_ENCODING).getValueAsString()).checksum(getFileChecksum(fields));
	}

	private MediaFileAllOfChecksum getFileChecksum(MapOfFields fields) {
		return new MediaFileAllOfChecksum()
				.algo(fields.get(FILE_CHECKSUM_ALGO).getValueAsEnumWith(HashAlgorithm::fromValue))
				.hash(fields.get(FILE_CHECKSUM_HASH).getValueAsString());
	}

	@Nonnull
	private MediaSeries getMediaSeries(MapOfFields fields) {
		return new MediaSeries().latency(fields.get(SERIES_LATENCY).getValueAsLong())
				.period(fields.get(SERIES_PERIOD).getValueAsLong())
				.currentNumberOfRecords(fields.get(SERIES_CURENT_NUMBER_OF_RECORDS).getValueAsLong())
				.currentSize(fields.get(SERIES_CURENT_SIZE).getValueAsLong())
				.totalNumberOfRecords(fields.get(SERIES_TOTAL_NUMBER_OF_RECORDS).getValueAsLong())
				.totalSize(fields.get(SERIES_TOTAL_SIZE).getValueAsLong());
	}

	private Media getMediaService(MapOfFields fields) {
		return new MediaService().apiDocumentationUrl(fields.get(SERVICE_API_DOCUMENTATION_URL).getValueAsString());
	}

	@Nullable
	@Override
	public Media defaultMetadata() {
		return null;
	}
}
