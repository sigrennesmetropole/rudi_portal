package org.rudi.microservice.acl.service.account;

import static org.rudi.common.service.exception.AppServiceExceptionsStatus.SEND_EMAIL_REGISTRATION;

/**
 * Exception lors de l'envoie d'un courriel de confirmation
 * 
 * @author FNI18300
 *
 */
public class SendEmailRegistrationException extends AbstractAccountRegistrationException {

	private static final long serialVersionUID = -268823054251213764L;

	public SendEmailRegistrationException() {
		super("Une erreur s'est produite pendant l'envoi du mail de demande de création de compte",
				SEND_EMAIL_REGISTRATION);
	}
}
