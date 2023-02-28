/**
 * 
 */
package org.rudi.facet.generator.docx;

import java.io.IOException;
import java.util.List;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.generator.exception.GenerationException;

/**
 * @author FNI18300
 *
 */
public interface DocxMerger {

	DocumentContent merge(String filename, List<DocumentContent> documents) throws GenerationException, IOException;

}
