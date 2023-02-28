package org.rudi.facet.dataverse.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.rudi.facet.dataverse.bean.FieldTypeClass;

import lombok.Getter;

/**
 * Spécifications d'un champ Dataverse
 */
public abstract class FieldSpec {
	@Getter
	final Collection<FieldSpec> directChildren = new ArrayList<>();
	@Getter(lazy = true)
	private final InternalFieldSpec index = getIndex(this);
	private Object defaultValueIfMissing;
	private Boolean directSortable;
	@Getter(lazy = true)
	private final FieldSpec sortableField = initSortableField();
	private Boolean forcedAllowControlledVocabulary;

	/**
	 * @return l'index crée dans le dataverse si l'attribut est de type multiple, dataverse utilise nom + _ss, sinon il utilise nom + _s
	 */
	private static InternalFieldSpec getIndex(FieldSpec fieldSpec) {
		if (fieldSpec.isMultiple()) {
			return new InternalFieldSpec(fieldSpec, "ss");
		}
		return new InternalFieldSpec(fieldSpec, "s");
	}

	public ChildFieldSpec newChildFromJavaField(String javaFieldName) {
		return new FieldSpecFromJavaField(this, javaFieldName);
	}

	/**
	 * Utilisé dans le cas de classe abstraite dont on ne peut pas devenir le type concret correspondant au champ Dataverse
	 */
	public ChildFieldSpec newChildFromJavaField(Class<?> javaFieldClass, String javaFieldName) {
		return new FieldSpecFromJavaField(this, javaFieldClass, javaFieldName);
	}

	/**
	 * @param fieldName nom du champ s'il est différent dans l'annotation @JsonProperty, sinon utiliser tout simplement
	 *                  {@link #newChildFromJavaField(String)}
	 */
	public ChildFieldSpec newChildFromJavaField(String javaFieldName, String fieldName) {
		return new FieldSpecFromJavaFieldWithName(this, javaFieldName, fieldName);
	}

	/**
	 * Utilisé dans le cas de classe abstraite dont on ne peut pas devenir le type concret correspondant au champ Dataverse. De plus on fixe le nom du
	 * champ parent qui n'est pas celui habituel.
	 */
	public ChildFieldSpec newChildFromJavaField(Class<?> javaFieldClass, String javaFieldName, String fieldName) {
		return new FieldSpecFromJavaFieldWithName(this, javaFieldClass, javaFieldName, fieldName);
	}

	/**
	 * @return le nom complet du champ tel qu'il apparaît dans Dataverse
	 */
	public abstract String getName();

	/**
	 * @return Le nom de la facet utilisable dans le service org.rudi.facet.kaccess.service.dataset.impl.DatasetServiceImpl#searchDatasets(org.rudi.facet.kaccess.bean.DatasetSearchCriteria, java.util.List)
	 */
	public abstract String getFacet();

	/**
	 * @return true si on doit concaténer le nom du champ parent dans chaque champ fils
	 */
	public abstract FieldSpecNamingCase getNamingCase();

	/**
	 * @return le nom du champ JsonProperty
	 */
	public abstract String getLocalName();

	/**
	 * @return le nom du champ JsonProperty en camelCase
	 */
	public String getLocalNameCamelCase() {
		return Pattern.compile("_([a-z])").matcher(getLocalName()).replaceAll(m -> m.group(1).toUpperCase());
	}

	/**
	 * @return le type Java du champ
	 */
	public abstract Class<?> getJavaType();

	/**
	 * @return le type du champ dans Dataverse
	 */
	public abstract FieldTypeClass getTypeClass();

	/**
	 * @return le type Java des valeurs que peut prendre ce champ.
	 * Quand le champ n'est pas une liste, renvoie la même chose que {@link #getJavaType()}.
	 * Quand le champ est une liste, renvoie le type des éléments de cette liste.
	 */
	public Class<?> getValueType() {
		return getJavaType();
	}

	@Nullable
	public abstract String getDescription();

	public boolean isMultiple() {
		return List.class.isAssignableFrom(getJavaType());
	}

	@Override
	public String toString() {
		// Pour des raisons de compatibilités avec les anciennes constantes de type String, on renvoie simplement name
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final FieldSpec fieldSpec = (FieldSpec) o;
		return Objects.equals(getName(), fieldSpec.getName());
	}

	@Override
	public int hashCode() {
		// Pour des raisons de compatibilités avec les anciennes constantes de type String, on utilise simplement name
		return getName().hashCode();
	}

	public boolean isCompound() {
		return getTypeClass() == FieldTypeClass.COMPOUND;
	}

	/**
	 * Set a default value if this field is nullable
	 *
	 * @param defaultValueIfMissing default value if this field is missing in a DataSet
	 * @param <T>                   type of the value
	 * @return this
	 */
	public <T> FieldSpec defaultValueIfMissing(T defaultValueIfMissing) {
		this.defaultValueIfMissing = defaultValueIfMissing;
		return this;
	}

	/**
	 * @see #defaultValueIfMissing(Object)
	 */
	public Object getDefaultValueIfMissing() {
		return defaultValueIfMissing;
	}

	/**
	 * Specify if that this field is sortable.
	 *
	 * <p>
	 * If this field is not sortable then a copy field must be declared in ansible/roles/dataverse/files/solr-data/collection1/conf/schema.xml. See
	 * example "rudi_resource_title_sortable" :
	 * </p>
	 *
	 * <pre>
	 * {@code
	 * <field name="rudi_resource_title_sortable" type="alphaOnlySort" indexed="true" stored="true"/>
	 * <copyField source="rudi_resource_title" dest="rudi_resource_title_sortable" maxChars="3000"/>
	 * }
	 * </pre>
	 *
	 * @return this
	 */
	public FieldSpec isDirectSortable(boolean directSortable) {
		this.directSortable = directSortable;
		return this;
	}

	private FieldSpec initSortableField() {
		if (directSortable == null) {
			throw new UnsupportedOperationException(
					"Cannot get sortable field because this.sortable is not specified. See this.isSortable.");
		}
		if (directSortable) {
			return this;
		} else {
			return getIndex();
		}
	}

	/**
	 * Cf javadoc champ required de la classe TsvDatasetFieldLine
	 */
	public boolean isRequired() {
		return false;
	}

	public boolean allowControlledVocabulary() {
		return forcedAllowControlledVocabulary != null ? forcedAllowControlledVocabulary : getValueType().isEnum();
	}

	public FieldSpec allowControlledVocabulary(boolean forcedAllowControlledVocabulary) {
		this.forcedAllowControlledVocabulary = forcedAllowControlledVocabulary;
		return this;
	}
}
