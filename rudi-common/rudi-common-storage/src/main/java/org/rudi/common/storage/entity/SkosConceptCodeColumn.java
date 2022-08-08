package org.rudi.common.storage.entity;

/**
 * Variables pour indiquer qu'une colonne correspond à un code de concept SKOS.
 *
 * <p>Exemple de code :</p>
 *
 * <pre>{@code
 * @ElementCollection
 * @CollectionTable(name = "project_theme", schema = SchemaConstants.DATA_SCHEMA, joinColumns = @JoinColumn(name = "project_fk"))
 * @Column(name = SkosConceptCodeColumn.NAME, length = SkosConceptCodeColumn.LENGTH)
 * private Set<String> themes = new HashSet<>();
 * }</pre>
 */
public final class SkosConceptCodeColumn {

	public static final String TABLE_NAME = "skos_concept";
	public static final String NAME = TABLE_NAME + "_" + AbstractLabelizedEntity.CODE_COLUMN_NAME;
	public static final int LENGTH = AbstractLabelizedEntity.CODE_COLUMN_LENGTH;

	private SkosConceptCodeColumn() {
	}
}
