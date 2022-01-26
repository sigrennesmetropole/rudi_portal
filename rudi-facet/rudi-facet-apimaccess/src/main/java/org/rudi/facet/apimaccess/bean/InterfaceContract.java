package org.rudi.facet.apimaccess.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InterfaceContract {
	DOWNLOAD("dwnl");

	private String value;

	InterfaceContract(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static InterfaceContract fromValue(String value) {
		for (InterfaceContract b : InterfaceContract.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
