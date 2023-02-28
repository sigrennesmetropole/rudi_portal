package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.kaccess.bean.Media;

public class UnhandledMediaTypeException extends AppServiceBadRequestException {
	private static final long serialVersionUID = -6396443451642468365L;

	public UnhandledMediaTypeException(Media.MediaTypeEnum mediaType) {
		super("Type de média " + mediaType + " non pris en charge. Veuillez utiliser l'API /apm/medias à la place. Pour plus d'informations consulter la documentation : https://doc.rudi.bzh/api/authentification/#pour-une-acc%C3%A8s-en-son-nom-propre-%C3%A9l%C3%A9ments-en-cours-de-d%C3%A9finition-cot%C3%A9-portail-et-non-disponible-actuellement-sur-rudibzh-il-faut-");
	}
}
