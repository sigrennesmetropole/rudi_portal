package org.rudi.common.service.mapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Classe utilitaire des Mapper.
 */
public class MapperUtils {

	private MapperUtils() {
	}

	/**
	 * Converti une LocaleDate en Timestamp.
	 *
	 * @param value LocalDate
	 * @return Timestamp
	 */
	public static Timestamp map(LocalDate value) {
		if (value != null) {
			Date date = Date.from(value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			return new Timestamp(date.getTime());
		} else {
			return null;
		}

	}

	/**
	 * Converti un Boolean en caractères '1' ou '0'.
	 *
	 * @param value booleen à convertir
	 * @return '0' ou '1'
	 */
	public static Character map(Boolean value) {
		if (value.equals(Boolean.TRUE)) {
			return '1';
		} else {
			return '0';
		}
	}

	/**
	 * Converti les caractères '1' et '0' en Booleen.
	 *
	 * @param value LocalDate
	 * @return Timestamp
	 */
	public static Boolean map(Character value) {
		return (value == null || value.equals(('0')));
	}

	public static String map(URI value) {

		return (value != null ? value.toString() : null);
	}

	public static URI map(String value) throws URISyntaxException {

		return (value != null ? new URI(value) : null);
	}
}
