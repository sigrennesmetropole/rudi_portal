package org.rudi.facet.dataset.bean;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum InterfaceContract {
	DOWNLOAD("dwnl"),
	GENERIC_DATA("gdata", "generic-data"),
	TEMPORAL_BAR_CHART("tpbc", "temporal-bar-chart"),
	WFS("wfs"),
	WMS("wms"),
	WMTS("wmts");

	/**
	 * Nom long de l'interface_contract, tel qu'utilisé dans le JSON des métadonnées de JDD.
	 */
	private final String code;

	/**
	 * Nom court de l'interface_contract, tel qu'utilisé dans les noms de fichiers et pour le context WSO2.
	 */
	private final String urlPath;

	InterfaceContract(String urlPath) {
		this(urlPath, urlPath);
	}

	InterfaceContract(String urlPath, String code) {
		this.code = code;
		this.urlPath = urlPath;
	}

	@JsonValue
	public String getUrlPath() {
		return urlPath;
	}

	@Override
	public String toString() {
		return String.valueOf(urlPath);
	}

	@JsonCreator
	@Nonnull
	public static InterfaceContract fromUrlPath(String urlPath) {
		final var enumValue = from("urlPath", urlPath, interfaceContract -> interfaceContract.urlPath.equals(urlPath), true);
		return Objects.requireNonNull(enumValue);
	}

	@Nonnull
	public static InterfaceContract fromCode(String code) {
		final var enumValue = from("code", code, interfaceContract -> interfaceContract.code.equals(code), true);
		return Objects.requireNonNull(enumValue);
	}

	@Nullable
	public static InterfaceContract nullableFromCode(String code) {
		return from("code", code, interfaceContract -> interfaceContract.code.equals(code), false);
	}

	public static InterfaceContract from(String fieldName, String fieldValue, Predicate<InterfaceContract> predicate, boolean throwException) {
		for (final var interfaceContract : InterfaceContract.values()) {
			if (predicate.test(interfaceContract)) {
				return interfaceContract;
			}
		}
		if (throwException) {
			throw new IllegalArgumentException(String.format("Unexpected %s '%s'", fieldName, fieldValue));
		} else {
			return null;
		}
	}

}
