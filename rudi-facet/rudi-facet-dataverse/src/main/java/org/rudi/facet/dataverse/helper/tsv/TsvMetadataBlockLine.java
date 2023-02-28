package org.rudi.facet.dataverse.helper.tsv;

import java.util.Objects;

import lombok.Builder;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @see <a href="https://guides.dataverse.org/en/latest/admin/metadatacustomization.html#metadatablock-properties">Documentation Dataverse</a>
 */
@Builder
class TsvMetadataBlockLine extends TsvLine {

	/**
	 * A user-definable string used to identify a #metadataBlock
	 * <ul>
	 * <li>No spaces or punctuation, except underscore.</li>
	 * <li>By convention, should start with a letter, and use lower camel case.</li>
	 * <li>Must not collide with a field of the same name in the same or any other #datasetField definition, including metadata blocks defined elsewhere.</li>
	 * </ul>
	 */
	final String name;

	/**
	 * If specified, this metadata block will be available only to the Dataverse collection designated here by its alias and to children of that Dataverse collection.
	 * <p>
	 * Free text. For an example, see custom_hbgdki.tsv.
	 */
	final String dataverseAlias;

	/**
	 * Acts as a brief label for display related to this #metadataBlock.
	 * <p>
	 * Should be relatively brief. The limit is 256 character, but very long names might cause display problems.
	 */
	final String displayName;

	/**
	 * Associates the properties in a block with an external URI. Properties will be assigned the global identifier blockURI<name> in the OAI_ORE metadata and archival Bags
	 * <p>
	 * The citation #metadataBlock has the blockURI <a href="https://dataverse.org/schema/citation/">https://dataverse.org/schema/citation/</a> which assigns a default global URI to terms such as <a href="https://dataverse.org/schema/citation/subtitle">https://dataverse.org/schema/citation/subtitle</a>
	 */
	final String blockURI;

	@Override
	public String toString() {
		return String.format("\t%s\t%s\t%s\t%s\t\t\t\t\t\t\t\t\t\t\t\t",
				name,
				Objects.toString(dataverseAlias, EMPTY),
				displayName,
				Objects.toString(blockURI, EMPTY));
	}

	@Override
	protected String getHashString() {
		return name;
	}
}
