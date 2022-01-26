package org.rudi.facet.dataverse.fields;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Spécifications d'un champ Dataverse
 */
public abstract class FieldSpec {
	@Getter
	final Collection<FieldSpec> children = new ArrayList<>();
	@Getter(lazy = true)
	private final InternalFieldSpec index = getIndex(this);
	private Object defaultValueIfMissing;

	/**
	 * @return l'index crée dans le dataverse
	 * si l'attribut est de type multiple, dataverse utilise nom + _ss, sinon il utilise nom + _s
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
	 * @param fieldName nom du champ s'il est différent dans l'annotation @JsonProperty, sinon utiliser tout simplement {@link #newChildFromJavaField(String)}
	 */
	public ChildFieldSpec newChildFromJavaField(String javaFieldName, String fieldName) {
		return new FieldSpecFromJavaFieldWithName(this, javaFieldName, fieldName);
	}

	/**
	 * Utilisé dans le cas de classe abstraite dont on ne peut pas devenir le type concret correspondant au champ Dataverse.
	 * De plus on fixe le nom du champ parent qui n'est pas celui habituel.
	 */
	public ChildFieldSpec newChildFromJavaField(Class<?> javaFieldClass, String javaFieldName, String fieldName) {
		return new FieldSpecFromJavaFieldWithName(this, javaFieldClass, javaFieldName, fieldName);
	}

	/**
	 * @return le nom complet du champ tel qu'il apparaît dans Dataverse
	 */
	public abstract String getName();

	/**
	 * @return true si on doit concaténer le nom du champ parent dans chaque champ fils
	 */
	public abstract FieldSpecNamingCase getNamingCase();

	/**
	 * @return le nom du champ JsonProperty
	 */
	public abstract String getLocalName();

	/**
	 * @return le type Java du champ
	 */
	protected abstract Class<?> getType();

	/**
	 * @return le type du champ dans Dataverse
	 */
	public abstract FieldTypeClass getTypeClass();

	/**
	 * @return le type Java des valeurs que peut prendre ce champ
	 */
	public abstract Class<?> getValueType();

	@Nullable
	abstract String getDescription();

	public boolean isMultiple() {
		return List.class.isAssignableFrom(getType());
	}

	@Override
	public String toString() {
		// Pour des raisons de compatibilités avec les anciennes constantes de type String, on renvoie simplement name
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
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
}
