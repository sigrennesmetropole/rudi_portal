package org.rudi.facet.kmedia.service;

import org.rudi.common.core.DocumentContent;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

public interface MediaService {

	/**
	 * Charge dans le dataverse Rudi Media le fichier dont le dataset correspond aux critères demandés. MediaSearchCriteria doit contenir au minimum
	 * l'UUID de l'auteur du média, qui correspond à l'UUID d'un fournisseur ou d'un producteur, ainsi que l'origine du media et le type de donnée. On
	 * considère qu'un dataset du dataverse Rudi Media ne contient qu'un seul fichier.
	 *
	 * @param mediaAuthorAffiliation type d'auteur
	 * @param mediaAuthorIdentifier  UUID de l'auteur
	 * @param kindOfData             type de média
	 * @return le document chargé, null si le media n'est pas trouvé ou si le media ne contient aucun fichier ou si l'auteur n'existe pas.
	 * @throws DataverseAPIException en cas d'erreur avec l'API Dataverse
	 */
	@Nullable
	DocumentContent getMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException;

	/**
	 * Remplace le fichier du média pour cet auteur
	 *
	 * @param mediaAuthorAffiliation type d'auteur
	 * @param mediaAuthorIdentifier  UUID de l'auteur
	 * @param kindOfData             type de média
	 * @param media                  fichier à remplacer
	 * @throws DataverseAPIException en cas d'erreur avec l'API Dataverse
	 */
	void setMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData, File media) throws DataverseAPIException;

	/**
	 * Supprime le média associé à cet auteur.
	 * La suppression par DataVerse du DataSet associé au média, supprime également tous les fichiers de ce DataSet.
	 *
	 * @param mediaAuthorAffiliation type d'auteur
	 * @param mediaAuthorIdentifier  l'uuid de l'auteur
	 * @param kindOfData             type du média à supprimer
	 */
	void deleteMediaFor(MediaOrigin mediaAuthorAffiliation, UUID mediaAuthorIdentifier, KindOfData kindOfData) throws DataverseAPIException;
}