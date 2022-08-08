/**
 * 
 */
package org.rudi.facet.generator.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Classe utilitaire pour la gestion des données du modèle
 * 
 * @author FNI18300
 *
 */
public final class DataModelUtils {

	private static final String NEW_LINE = "\n";
	private static final String BR = "<br/>";

	/**
	 * 
	 * Constructeur pour DataModelUtils privé car classe utile
	 */
	private DataModelUtils() {

	}

	/**
	 * Encode chaine de charactere pour les template
	 * 
	 * @param chaine
	 * @return
	 */
	public static String encodeOdt(String chaine) {
		String result = chaine;
		result = StringUtils.replace(result, "&", "&amp;");
		result = StringUtils.replace(result, "<", "&lt;");

		return result;
	}

	/**
	 * Encode les \n\r\t en html
	 * 
	 * @param chaine
	 * @return la chaine encodée
	 */
	public static String encodeHtmlWhiteSpace(String chaine) {
		String result = chaine;
		result = StringUtils.replace(result, "\n\r", BR);
		result = StringUtils.replace(result, "\r", BR);
		result = StringUtils.replace(result, NEW_LINE, BR);
		result = StringUtils.replace(result, "\t", "&nbsp;&nbsp;");

		return result;
	}

	/**
	 * Encode chaine de charactere pour les template
	 * 
	 * @param chaine
	 * @return
	 */
	public static String encodeHtml(String chaine) {
		return StringEscapeUtils.escapeHtml4(chaine);
	}

	/**
	 * Encode les \n\r\t en html
	 * 
	 * @param chaine
	 * @return la chaine encodée
	 */
	public static String encodeHtmlMemo(String chaine) {
		String result = encodeHtml(chaine);
		return encodeHtmlWhiteSpace(result);
	}

	/**
	 * @param chaine
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeCharset(String chaine, String charset) throws UnsupportedEncodingException {
		return new String(chaine.getBytes(StandardCharsets.UTF_8), charset);
	}

	/**
	 * @param input
	 * @param lineLength
	 * @return le nombre de lignes en tenant compte de la longueur d'une ligne
	 */
	public static int countLines(String input, int lineLength) {
		int result = 0;
		if (input != null) {
			String[] lines = input.split(NEW_LINE);
			for (int i = 0; i < lines.length; i++) {
				result += Math.ceil(((double) lines[i].length()) / ((double) lineLength));
			}
		}
		return result;
	}

	/**
	 * @param input      la chaine
	 * @param maxLines   le nombre de lignes max
	 * @param lineLength la longueur d'une ligne
	 * @param suspens    le texte mis au bout de la chaine (la longueur est prise en
	 *                   compte pour le résultat total
	 * @return la chaine tronquées en nombre de lignes
	 */
	public static String truncateLines(String input, int maxLines, int lineLength, String suspens) {
		StringBuilder result = null;
		int countLines = 0;
		if (input != null) {
			int c = countLines(input, lineLength);
			result = new StringBuilder();
			String[] lines = input.split(NEW_LINE);
			for (int i = 0; i < lines.length; i++) {
				countLines = handleTruncateLine(result, lines[i], suspens, maxLines, lineLength, c, countLines, i);
			}
		}
		return result != null ? result.toString() : null;
	}

	// Tous paramètres utilisés non simplifiable
	@SuppressWarnings("java:S107")
	private static int handleTruncateLine(StringBuilder result, String line, String suspens, int maxLines,
			int lineLength, int totalLine, int countLines, int index) {
		int currentCountLines = (int) Math.ceil(((double) line.length()) / ((double) lineLength));
		if (countLines + currentCountLines > maxLines - 1) {
			int trailingLines = maxLines - countLines;
			if (countLines + trailingLines == totalLine) {
				suspens = null;
			}
			int currentMaxLength = trailingLines * lineLength - (suspens != null ? suspens.length() : 0);
			if (result.length() > 0) {
				result.append(NEW_LINE);
			}
			result.append(line.substring(0, Math.min(currentMaxLength, line.length())));
			if (suspens != null) {
				result.append(suspens);
			}
			countLines += trailingLines;
		} else {
			countLines += currentCountLines;
			if (index > 0) {
				result.append(NEW_LINE);
			}
			result.append(line);
		}
		return countLines;
	}

}
