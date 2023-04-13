package org.rudi.microservice.konsent.storage.entity.common;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.rudi.common.core.Ordered;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class AbstractMultilangualStampedEntity extends AbstractMultilangualEntity implements Ordered, Serializable {
	private static final long serialVersionUID = 3642519721998137595L;

	@Column(name = "opening_date", nullable = false)
	private OffsetDateTime openingDate;

	@Column(name = "closing_date")
	private OffsetDateTime closingDate;

	@Column(name = "order_", nullable = false)
	private int order;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractMultilangualStampedEntity)) {
			return false;
		}
		return super.equals(obj);
	}
}
