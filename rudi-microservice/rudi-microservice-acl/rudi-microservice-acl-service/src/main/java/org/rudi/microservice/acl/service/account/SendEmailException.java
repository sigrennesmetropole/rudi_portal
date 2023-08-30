package org.rudi.microservice.acl.service.account;

import static org.rudi.common.service.exception.AppServiceExceptionsStatus.SEND_EMAIL_ACTIVATION;

/**
 * Echec d'envoie de courriel
 * 
 * @author FNI18300
 *
 */
public class SendEmailException extends AccountCreationException {

	private static final long serialVersionUID = 2128167968711776124L;

	public SendEmailException(final Throwable cause) {
		super("Une erreur s'est produite pendant l'envoi du mail de confirmation suite à la création de l'utilisateur",
				cause, SEND_EMAIL_ACTIVATION);
	}
}
