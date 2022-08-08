package org.rudi.facet.email;

import org.rudi.facet.email.exception.EMailException;
import org.rudi.facet.email.model.EMailDescription;

/**
 * Service d'envoie des courriels
 * 
 * @author FNI18300
 *
 */
public interface EMailService {

	/**
	 * Emet un courriel à partir des éléments fournis
	 * 
	 * @param mailDescription
	 * @throws EMailException
	 */
	void sendMail(EMailDescription mailDescription) throws EMailException;

	/**
	 * Emet un courriel à partir des éléments fournis sans lancer d'exception
	 * (par exemple, lorsque le mail n'estqu'informatif et qu'il ne doit pas bloquer un autre traitement)
	 */
	void sendMailAndCatchException(EMailDescription mailDescription);

	/**
	 * 
	 * @return le from par défaut de l'application
	 */
	String getDefaultFrom();
}
