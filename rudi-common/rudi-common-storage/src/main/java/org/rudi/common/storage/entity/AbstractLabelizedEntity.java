/**
 * 
 */
package org.rudi.common.storage.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rudi.common.core.Coded;
import org.rudi.common.core.Labelized;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author FNI18300
 *
 */
@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class AbstractLabelizedEntity extends AbstractLongIdEntity implements Coded, Labelized, Serializable {

	public static final String CODE_COLUMN_NAME = "code";
	public static final int CODE_COLUMN_LENGTH = 30;
	private static final long serialVersionUID = 3642617461998137595L;

	@Column(name = CODE_COLUMN_NAME, length = CODE_COLUMN_LENGTH, nullable = false)
	private String code;

	@Column(name = "label", length = 100)
	private String label;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof AbstractLabelizedEntity)) {
			return false;
		}
		AbstractLabelizedEntity other = (AbstractLabelizedEntity) obj;
		if (getId() != null && getId().equals(other.getId())) {
			return true;
		}
		if (getCode() == null) {
			if (other.getCode() != null) {
				return false;
			}
		} else if (!getCode().equals(other.getCode())) {
			return false;
		}
		return true;
	}

}
