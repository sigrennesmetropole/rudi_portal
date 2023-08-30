/**
 * 
 */
package org.rudi.facet.email.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.email.EMailConfiguration;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.email.exception.EMailException;
import org.rudi.facet.email.model.EMailDescription;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation du services d'envoie des courriels
 * 
 * @author FNI18300
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EMailServiceImpl implements EMailService {

	private final EMailConfiguration emailConfiguration;

	private final JavaMailSenderImpl javaMailSender;

	@Override
	public void sendMail(EMailDescription mailDescription) throws EMailException {
		if (mailDescription == null) {
			throw new IllegalArgumentException("Mail description required");
		}

		if (StringUtils.isEmpty(mailDescription.getFrom())) {
			mailDescription.setFrom(getDefaultFrom());
		}
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(mailDescription.getFrom());
			if (CollectionUtils.isNotEmpty(mailDescription.getTos())) {
				helper.setTo(mailDescription.getTos().toArray(new String[mailDescription.getTos().size()]));
			}
			if (CollectionUtils.isNotEmpty(mailDescription.getCcs())) {
				helper.setCc(mailDescription.getCcs().toArray(new String[mailDescription.getCcs().size()]));
			}
			if (CollectionUtils.isNotEmpty(mailDescription.getBccs())) {
				helper.setBcc(mailDescription.getBccs().toArray(new String[mailDescription.getBccs().size()]));
			}
			if (StringUtils.isNotEmpty(mailDescription.getSubject())) {
				helper.setSubject(mailDescription.getSubject());
			}

			if (mailDescription.getBody() != null) {
				handleBody(helper, mailDescription);
			}

			if (CollectionUtils.isNotEmpty(mailDescription.getAttachments())) {
				for (DocumentContent attachment : mailDescription.getAttachments()) {
					handleAttachment(helper, attachment);
				}
			}

			javaMailSender.send(message);
		} catch (Exception e) {
			throw new EMailException("Failed to send mail:" + mailDescription, e);
		}
	}

	private void handleAttachment(MimeMessageHelper helper, DocumentContent attachment)
			throws MessagingException, FileNotFoundException {
		if (attachment.isFile()) {
			FileSystemResource fileResource = new FileSystemResource(attachment.getFile());
			helper.addAttachment(attachment.getFileName(), fileResource);
		} else if (attachment.isStream()) {
			InputStreamResource inputStreamResource = new InputStreamResource(attachment.getFileStream());
			helper.addAttachment(attachment.getFileName(), inputStreamResource, attachment.getContentType());
		}
	}

	private void handleBody(MimeMessageHelper helper, EMailDescription mailDescription)
			throws IOException, MessagingException {
		String text = null;
		try (InputStream bodyStream = mailDescription.getBody().getFileStream()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(bodyStream, baos);
			text = baos.toString();
			if (mailDescription.isHtml()) {
				String plainText = extractPlainText(text);
				helper.setText(plainText, text);
			} else {
				helper.setText(text);
			}
		}
	}

	@Override
	public void sendMailAndCatchException(EMailDescription mailDescription) {
		try {
			sendMail(mailDescription);
		} catch (EMailException e) {
			log.error("EMailException catched", e);
		}
	}

	@Override
	public String getDefaultFrom() {
		return emailConfiguration.getDefaultFrom();
	}

	private String extractPlainText(String text) {
		return Jsoup.parse(text).wholeText();
	}

}
