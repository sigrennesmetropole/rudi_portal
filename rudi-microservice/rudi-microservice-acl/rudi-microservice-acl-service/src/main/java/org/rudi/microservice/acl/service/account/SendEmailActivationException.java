package org.rudi.microservice.acl.service.account;

import static org.rudi.common.service.exception.AppServiceExceptionsStatus.SEND_EMAIL_ACTIVATION;

/**
 * Exception lors de l'envoie d'un courriel de confirmation
 * 
 * @author FNI18300
 *
 */
public class SendEmailActivationException extends AbstractAccountRegistrationException {

	private static final long serialVersionUID = -268823054251213764L;

	public SendEmailActivationException() {
		super("Une erreur s'est produite pendant l'envoi du mail de confirmation suite à la création de l'utilisateur",
				SEND_EMAIL_ACTIVATION);
	}
}
