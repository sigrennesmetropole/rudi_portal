package org.rudi.microservice.acl.service.captcha.service;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.microservice.acl.core.bean.CaptchaModel;

public interface CaptchaService {
	/**
	 * @param get nom du captcha
	 * @param c   type du captcha qui est obligatoire pour certains type de captcha (html)
	 * @param t   param technique utilisé par l'API
	 * @param cs  param technique utilisé par l'API
	 * @param d   param technique utilisé par l'API
	 * @return HTML du captcha généré en string
	 */
	DocumentContent generateCaptcha(String get, String c, String t, String cs, String d) throws ExternalServiceException;

	/**
	 * @param captchaModel DTO du captcha à valider
	 * @return boolean pour dire si tout s'est bien passé
	 */
	Boolean validateCaptcha(CaptchaModel captchaModel) throws ExternalServiceException;
}
