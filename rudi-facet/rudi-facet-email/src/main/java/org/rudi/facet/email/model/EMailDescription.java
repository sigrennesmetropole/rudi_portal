/**
 * 
 */
package org.rudi.facet.email.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe de description d'un courriel
 * 
 * @author FNI18300
 *
 */
@Data
@NoArgsConstructor
public class EMailDescription {

	private String from;

	private final List<String> tos = new ArrayList<>();

	private final List<String> ccs = new ArrayList<>();

	private final List<String> bccs = new ArrayList<>();

	private String subject;

	private DocumentContent body;

	private boolean html = true;

	private List<DocumentContent> attachments;

	/**
	 * Custructeur avec tous les paramètres sauf les pièces jointes
	 * 
	 * @param from    email de l'origine
	 * @param tos     la liste des destinataires
	 * @param ccs     la listes des destinaires en copie
	 * @param bccs    la listes des destinataires en copie cachée
	 * @param subject le sujet
	 * @param body    le corps du courriel
	 * @param html    si le corps est du html (dans ce cas on envoie le mail en text
	 *                et html)
	 */
	public EMailDescription(String from, List<String> tos, List<String> ccs, List<String> bccs, String subject,
			DocumentContent body, boolean html) {
		this.from = from;
		addItems(this.tos, tos);
		addItems(this.ccs, ccs);
		addItems(this.bccs, bccs);
		this.subject = subject;
		this.body = body;
		this.html = html;
	}

	/**
	 * Constructeur simplifié
	 * 
	 * @param from    email de l'origine
	 * @param tos     la liste des destinataires
	 * @param subject le sujet
	 * @param body    le corps du courriel
	 */
	public EMailDescription(String from, List<String> tos, String subject, DocumentContent body) {
		this(from, tos, null, null, subject, body, true);
	}

	/**
	 * Constructeur simplifié
	 * 
	 * @param tos     la liste des destinataires
	 * @param subject le sujet
	 * @param body    le corps du courriel
	 */
	public EMailDescription(List<String> tos, String subject, DocumentContent body) {
		this(null, tos, null, null, subject, body, true);
	}

	/**
	 * Constructeur simplifié
	 * 
	 * @param from    email de l'origine
	 * @param to      un destinataire unique
	 * @param subject le sujet
	 * @param body    le corps du courriel
	 */
	public EMailDescription(String from, String to, String subject, DocumentContent body) {
		this(from, Collections.singletonList(to), null, null, subject, body, true);
	}

	/**
	 * Constructeur simplifié
	 * 
	 * @param to      un destinataire unique
	 * @param subject le sujet
	 * @param body    le corps du courriel
	 */
	public EMailDescription(String to, String subject, DocumentContent body) {
		this(null, Collections.singletonList(to), null, null, subject, body, true);
	}

	public void addTo(String to) {
		addItem(tos, to);
	}

	public void addCc(String cc) {
		addItem(ccs, cc);
	}

	public void addBcc(String bcc) {
		addItem(bccs, bcc);
	}

	public void addAttachment(DocumentContent attachment) {
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		this.attachments.add(attachment);
	}

	protected void addItem(List<String> target, String item) {
		if (StringUtils.isNoneEmpty(item)) {
			target.add(item);
		}
	}

	protected void addItems(List<String> target, List<String> items) {
		if (CollectionUtils.isNotEmpty(items)) {
			for (String item : items) {
				addItem(target, item);
			}
		}
	}

}
