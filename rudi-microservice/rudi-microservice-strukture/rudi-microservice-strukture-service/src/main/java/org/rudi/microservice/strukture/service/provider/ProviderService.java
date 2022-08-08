package org.rudi.microservice.strukture.service.provider;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.microservice.strukture.core.bean.AbstractAddress;
import org.rudi.microservice.strukture.core.bean.NodeProvider;
import org.rudi.microservice.strukture.core.bean.Provider;
import org.rudi.microservice.strukture.core.bean.ProviderSearchCriteria;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 */
public interface ProviderService {

	/**
	 * List all providers
	 *
	 * @return providers list
	 */
	Page<Provider> searchProviders(ProviderSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * Retourne
	 */
	Provider getProvider(UUID uuid, boolean full) throws AppServiceException;

	/**
	 * Create a providers
	 */
	Provider createProvider(Provider provider);

	/**
	 * Update a providers entity
	 */
	Provider updateProvider(Provider provider);

	/**
	 * Delete a providers entity
	 */
	void deleteProvider(UUID uuid);

	/**
	 * Ajoute une addresse sur un producteur
	 */
	AbstractAddress createAddress(UUID providerUuid, AbstractAddress abstractAddress);

	/**
	 * Créé un noeud dans un producteur
	 */
	NodeProvider createNode(UUID providerUuid, NodeProvider nodeProvider);

	/**
	 * Supprime une addresse d'un producteur
	 */
	void deleteAddress(UUID providerUuid, UUID addressUuid);

	/**
	 * Supprime un noeud d'un producteur
	 */
	void deleteNode(UUID providerUuid, UUID nodeUuid);

	/**
	 * Retourne une address d'un producteur
	 */
	AbstractAddress getAddress(UUID providerUuid, UUID addressUuid);

	/**
	 * Retourne les addresses d'un producteur
	 */
	List<AbstractAddress> getAddresses(UUID providerUuid);

	/**
	 * Retourne un noeud d'un producteur
	 */
	NodeProvider getNode(UUID providerUuid, UUID nodeUuid);

	/**
	 * Retourne les noeuds d'un producteur
	 */
	List<NodeProvider> getNodes(UUID providerUuid);

	/**
	 * Modifie l'adresse d'un producteur
	 */
	AbstractAddress updateAddress(UUID providerUuid, @Valid AbstractAddress abstractAddress);

	/**
	 * Modifie le noeud d'un producteur
	 */
	NodeProvider updateNode(UUID providerUuid, @Valid NodeProvider nodeProvider);

	/**
	 * Télécharge le média du fournisseur d'un jeu de données, dans le dataverse Rudi Media
	 *
	 * @param providerId identifiant du fournisseur
	 * @param kindOfData le type de média
	 * @return DocumentContent
	 * @throws AppServiceException Erreur lors de la récupération du média
	 */
	DocumentContent downloadMedia(UUID providerId, KindOfData kindOfData) throws AppServiceException;

	/**
	 * Uploade le média du fournisseur d'un jeu de données, dans le dataverse Rudi Media
	 *
	 * @param providerUuid identifiant du fournisseur
	 * @param kindOfData   le type de média
	 * @param media        le média à remplacer dans Dataverse
	 * @throws AppServiceException En cas d'erreur avec le service d'upload
	 */
	void uploadMedia(UUID providerUuid, KindOfData kindOfData, Resource media) throws AppServiceException;

	/**
	 * Supprime le média associé à ce fournisseur
	 *
	 * @param providerUuid l'uuid du fournisseur
	 */
	void deleteMedia(UUID providerUuid, KindOfData kindOfData) throws AppServiceException;

	/**
	 * Change la date de dernier moissonnage réussi du nœud fournisseur
	 * @return le nœud fournisseur après modification
	 */
	NodeProvider patchNode(UUID providerUuid, UUID nodeUuid, LocalDateTime lastHarvestingDate);
}
