/**
 * 
 */
package org.rudi.facet.buckets3;

import java.util.Map;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.buckets3.exception.DocumentStorageException;

/**
 * Interface de gestion stockage des documents
 * 
 * @author FNI18300
 *
 */
public interface DocumentStorageService {

	void storeDocument(String key, Map<String, String> metadatas, DocumentContent documentContent)
			throws DocumentStorageException;

	DocumentContent retreiveDocument(String key, Map<String, String> metadatas) throws DocumentStorageException;

	void deleteDocument(String key) throws DocumentStorageException;

}
