/**
 * 
 */
package org.rudi.facet.buckets3.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.buckets3.DocumentStorageService;
import org.rudi.facet.buckets3.config.DocumentStorageConfiguration;
import org.rudi.facet.buckets3.exception.DocumentStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentStorageServiceImpl.class);

	private static final String FILE_NAME = "filename";

	private static final String MIME_TYPE = "mimetype";

	@Value("${temporary.directory:${java.io.tmpdir}}")
	private String temporaryDirectory;

	@Autowired
	private DocumentStorageConfiguration documentStorageConfiguration;

	private Boolean literalProvider = null;

	private ProviderMetadata provider = null;

	private List<com.google.inject.Module> modules = new ArrayList<>();

	@Override
	public void storeDocument(String key, Map<String, String> metadatas, DocumentContent documentContent)
			throws DocumentStorageException {
		if (StringUtils.isEmpty(key) || documentContent == null) {
			throw new IllegalArgumentException("Key and document content are required");
		}
		if (StringUtils.isEmpty(documentContent.getContentType())) {
			throw new IllegalArgumentException("Content type could not be null");
		}

		BlobStoreContext context = buildContext();

		Map<String, String> userMetaDatas = new HashMap<>();
		// store custom metadata
		if (MapUtils.isNotEmpty(metadatas)) {
			userMetaDatas.putAll(metadatas);
		}

		// store standard metadatas
		userMetaDatas.put(MIME_TYPE, documentContent.getContentType());
		if (StringUtils.isNotEmpty(documentContent.getFileName())) {
			userMetaDatas.put(FILE_NAME, documentContent.getFileName());
		} else {
			userMetaDatas.put(FILE_NAME, key);
		}

		try {
			BlobStore blobStore = context.getBlobStore();

			ensureContainer(blobStore);
			ensureRoot(blobStore, key);

			BlobBuilder blobBuilder = blobStore.blobBuilder(key);
			if (documentContent.isFile()) {
				blobBuilder.payload(documentContent.getFile());
			} else if (documentContent.isStream()) {
				blobBuilder.payload(documentContent.getFileStream());
			}
			blobBuilder.userMetadata(userMetaDatas);

			// Add a Blob
			Blob blob = blobBuilder.build();
			// Upload a file
			String eTag = blobStore.putBlob(getBucketName(), blob, PutOptions.Builder.multipart(false));
			LOGGER.debug(eTag);
		} catch (Exception e) {
			throw new DocumentStorageException("Failed to store document:" + key + " with matedata:" + userMetaDatas,
					e);
		} finally {
			context.close();
			if (documentContent.isStream()) {
				documentContent.closeStream();
			}
		}

	}

	@Override
	public DocumentContent retreiveDocument(String key, Map<String, String> metadatas) throws DocumentStorageException {
		if (StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Key is required");
		}
		DocumentContent result = null;

		BlobStoreContext context = buildContext();

		File outputFile = null;
		try {
			BlobStore blobStore = context.getBlobStore();

			ensureContainer(blobStore);
			ensureRoot(blobStore, key);

			Blob blob = blobStore.getBlob(getBucketName(), key);

			// create file
			outputFile = createTemporaryFile();

			// restore standard metadata
			String contentType = blob.getMetadata().getUserMetadata().get(MIME_TYPE);
			String fileName = blob.getMetadata().getUserMetadata().get(FILE_NAME);

			// restore custom metadata
			if (metadatas != null) {
				metadatas.putAll(blob.getMetadata().getUserMetadata());
				metadatas.remove(MIME_TYPE);
				metadatas.remove(FILE_NAME);
			}

			FileUtils.copyInputStreamToFile(blob.getPayload().openStream(), outputFile);
			result = new DocumentContent(fileName, contentType, outputFile);

		} catch (Exception e) {
			throw new DocumentStorageException("Failed to get document:" + key, e);
		} finally {
			// Close connecton
			context.close();
		}
		return result;
	}

	@Override
	public void deleteDocument(String key) throws DocumentStorageException {
		if (!documentStorageConfiguration.isDeleteEnable()) {
			throw new DocumentStorageException("Invalid operation");
		}
		if (StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("Key is required");
		}
		BlobStoreContext context = buildContext();

		try {
			BlobStore blobStore = context.getBlobStore();

			blobStore.removeBlob(getBucketName(), key);

		} catch (Exception e) {
			throw new DocumentStorageException("Failed to delete document:" + key, e);
		} finally {
			// Close connecton
			context.close();
		}
	}

	/**
	 * Construit et retourne le context du blobstore
	 * 
	 * @return
	 */
	protected BlobStoreContext buildContext() {
		// Init
		Properties overrides = new Properties();
		overrides.put(Constants.PROPERTY_ENDPOINT, documentStorageConfiguration.getEndPoint());
		if (documentStorageConfiguration.getThreadCount() != null) {
			overrides.setProperty("jclouds.mpu.parallel.degree", documentStorageConfiguration.getThreadCount());
		}
		if (documentStorageConfiguration.isTrustAllCerts()) {
			overrides.setProperty("jclouds.trust-all-certs", Boolean.TRUE.toString());
			overrides.setProperty("jclouds.relax-hostname", Boolean.TRUE.toString());
		}

		return createContextBuilder()
				.credentials(documentStorageConfiguration.getIdentity(), documentStorageConfiguration.getCredential())
				.modules(getModules()).overrides(overrides).endpoint(documentStorageConfiguration.getEndPoint())
				.build(BlobStoreContext.class);
	}

	/**
	 * Si le provider n'a jamais été chargé, un tentative de chargement est réalisé. On trace alors si l'on est en littéral ou en provider et on construit
	 * le context
	 * 
	 * @return
	 */
	protected ContextBuilder createContextBuilder() {
		if (literalProvider == null) {
			initProvider();
		}
		ContextBuilder contextBuilder = null;
		if (Boolean.FALSE.equals(literalProvider) && provider != null) {
			contextBuilder = ContextBuilder.newBuilder(provider);
		} else {
			contextBuilder = ContextBuilder.newBuilder(documentStorageConfiguration.getProviderId());
		}
		return contextBuilder;
	}

	/**
	 * Charge le provider ou indique qu'il s'agit d'un provider litteral
	 */
	protected synchronized void initProvider() {
		if (provider == null) {
			try {
				provider = Providers.withId(documentStorageConfiguration.getProviderId());
				literalProvider = false;
			} catch (NoSuchElementException exception) {
				LOGGER.debug("provider {} not in supported list: {}", documentStorageConfiguration.getProviderId(),
						Providers.all());
				literalProvider = true;
			}
		} else {
			literalProvider = false;
		}
	}

	/**
	 * Construit la lste des modules
	 * 
	 * @return
	 */
	protected List<com.google.inject.Module> getModules() {
		if (CollectionUtils.isEmpty(modules)) {
			modules.add(new SLF4JLoggingModule());
		}
		return modules;
	}

	/**
	 * Retourne le nom du bucket
	 * 
	 * @return
	 */
	protected String getBucketName() {
		if (StringUtils.isEmpty(documentStorageConfiguration.getBucketName())) {
			return null;
		} else {
			return documentStorageConfiguration.getBucketName();
		}
	}

	/**
	 * S'assure que le container est présent
	 * 
	 * @param blobStore
	 */
	protected void ensureContainer(BlobStore blobStore) {
		if (!documentStorageConfiguration.isEnsureBucket()) {
			return;
		}
		if (!blobStore.containerExists(getBucketName())) {
			blobStore.createContainerInLocation(null, getBucketName());
		}
	}

	/**
	 * Permet de s'assurer que le premier éléments du chemin existe
	 * 
	 * @param blobStore
	 * @param key
	 */
	protected void ensureRoot(BlobStore blobStore, String key) {
		if (!documentStorageConfiguration.isEnsureRoot()) {
			return;
		}
		String[] keys = key.split("/");
		if (!blobStore.blobExists(getBucketName(), keys[0])) {
			try {
				Blob blob = blobStore.blobBuilder(keys[0]).type(StorageType.FOLDER).payload("").build();
				blobStore.putBlob(getBucketName(), blob, PutOptions.Builder.multipart(false));
			} catch (Exception e) {
				if (!blobStore.blobExists(getBucketName(), keys[0])) {
					throw e;
				}
			}
		}
	}

	protected File createTemporaryFile() throws IOException {
		return createTemporaryFile("rudi", ".s3");
	}

	protected File createTemporaryFile(String prefix, String extension) throws IOException {
		File outputFile = null;
		if (StringUtils.isNotEmpty(temporaryDirectory)) {
			outputFile = File.createTempFile(prefix, extension, new File(temporaryDirectory));
		} else {
			outputFile = File.createTempFile(prefix, extension);
		}
		return outputFile;
	}
}
