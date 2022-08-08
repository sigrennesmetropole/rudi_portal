package org.rudi.microservice.acl.service.account;

import static org.rudi.common.service.exception.AppServiceExceptionsStatus.SEND_EMAIL_ACTIVATION;

public class SendEmailException extends AccountCreationException {
	public SendEmailException(final Throwable cause) {
		super("Une erreur s'est produite pendant l'envoi du mail de confirmation suite à la création de l'utilisateur", cause, SEND_EMAIL_ACTIVATION);
	}
}
