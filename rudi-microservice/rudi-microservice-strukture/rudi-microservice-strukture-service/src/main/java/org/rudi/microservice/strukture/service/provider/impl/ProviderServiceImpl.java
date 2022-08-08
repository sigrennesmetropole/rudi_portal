package org.rudi.microservice.strukture.service.provider.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.microservice.strukture.core.bean.AbstractAddress;
import org.rudi.microservice.strukture.core.bean.NodeProvider;
import org.rudi.microservice.strukture.core.bean.Provider;
import org.rudi.microservice.strukture.core.bean.ProviderSearchCriteria;
import org.rudi.microservice.strukture.service.mapper.AbstractAddressMapper;
import org.rudi.microservice.strukture.service.mapper.NodeProviderMapper;
import org.rudi.microservice.strukture.service.mapper.ProviderFullMapper;
import org.rudi.microservice.strukture.service.mapper.ProviderMapper;
import org.rudi.microservice.strukture.service.provider.ProviderService;
import org.rudi.microservice.strukture.storage.dao.address.AbstractAddressDao;
import org.rudi.microservice.strukture.storage.dao.address.AddressRoleDao;
import org.rudi.microservice.strukture.storage.dao.provider.NodeProviderDao;
import org.rudi.microservice.strukture.storage.dao.provider.ProviderCustomDao;
import org.rudi.microservice.strukture.storage.dao.provider.ProviderDao;
import org.rudi.microservice.strukture.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.strukture.storage.entity.address.AddressRoleEntity;
import org.rudi.microservice.strukture.storage.entity.provider.NodeProviderEntity;
import org.rudi.microservice.strukture.storage.entity.provider.ProviderEntity;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

	private static final String ADDRESS_UNKNOWN_MESSAGE = "Address unknown:";

	private static final String UUID_ADRESSE_MISSING_MESSAGE = "UUID Adresse missing";

	private static final String NODE_PROVIDER_UNKNOWN_MESSAGE = "NodeProvider unknown:";

	private static final String UUID_NODE_PROVIDER_MISSING_MESSAGE = "UUID NodeProvider missing";

	private static final String PROVIDER_UNKNOWN_MESSAGE = "Provider unknown:";

	private static final String UUID_PROVIDER_MISSING_MESSAGE = "UUID provider missing";

	private static final String ADDRESS_ROLE_INVALID_MESSAGE = "AddresseRole invalid";

	private static final String ADDRESS_ROLE_MISSING_MESSAGE = "AddresseRole missing";

	private static final String EMPTY_CODE_MESSAGE = "Invalid empty code";

	private static final String EMPTY_LABEL_MESSAGE = "Invalid empty label";

	private static final String EMPTY_OPENING_DATE_MESSAGE = "Invalid empty opening date";

	private static final String OPENING_DATE_LESS_THAN_CLOSING_DATE_MESSAGE = "Opening date less than closing date";

	private final MediaService mediaService;
	private final ProviderDao providerDao;
	private final NodeProviderDao nodeProviderDao;
	private final ProviderCustomDao providerCustomDao;
	private final AbstractAddressDao abstractAddressDao;
	private final AddressRoleDao addressRoleDao;
	private final ProviderMapper providerMapper;
	private final ProviderFullMapper providerFullMapper;
	private final NodeProviderMapper nodeProviderMapper;
	private final AbstractAddressMapper abstractAddressMapper;
	private final ResourceHelper resourceHelper;

	@Override
	public Page<Provider> searchProviders(ProviderSearchCriteria searchCriteria, Pageable pageable) {
		if (searchCriteria != null && Boolean.TRUE.equals(searchCriteria.getFull())) {
			return providerFullMapper.entitiesToDto(providerCustomDao.searchProviders(searchCriteria, pageable),
					pageable);
		} else {
			return providerMapper.entitiesToDto(providerCustomDao.searchProviders(searchCriteria, pageable), pageable);
		}
	}

	@Override
	@Nonnull
	public Provider getProvider(UUID uuid, boolean full) throws EmptyResultDataAccessException {
		if (full) {
			return providerFullMapper.entityToDto(providerDao.findByUUID(uuid));
		} else {
			return providerMapper.entityToDto(providerDao.findByUUID(uuid));
		}
	}

	@Override
	@Transactional // readOnly = false
	public Provider createProvider(Provider provider) {
		if (provider != null) {
			provider.setNodeProviders(null);
			provider.setAddresses(null);
		}

		ProviderEntity entity = providerMapper.dtoToEntity(provider);
		validProviderEntity(entity);
		entity.setUuid(UUID.randomUUID());

		providerDao.save(entity);
		return providerMapper.entityToDto(entity);
	}

	@Override
	@Transactional // readOnly = false
	public Provider updateProvider(Provider provider) {
		if (provider.getUuid() == null) {
			throw new IllegalArgumentException(UUID_PROVIDER_MISSING_MESSAGE);
		}

		ProviderEntity entity = providerDao.findByUUID(provider.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException(PROVIDER_UNKNOWN_MESSAGE + provider.getUuid());
		}
		validProviderEntity(entity);

		providerMapper.dtoToEntity(provider, entity);

		providerDao.save(entity);
		return providerMapper.entityToDto(entity);
	}

	private void validProviderEntity(ProviderEntity entity) {
		if (StringUtils.isEmpty(entity.getCode())) {
			throw new IllegalArgumentException(EMPTY_CODE_MESSAGE);
		}
		if (StringUtils.isEmpty(entity.getLabel())) {
			throw new IllegalArgumentException(EMPTY_LABEL_MESSAGE);

		}
		if (entity.getOpeningDate() == null) {
			throw new IllegalArgumentException(EMPTY_OPENING_DATE_MESSAGE);

		}
		// si closingDate existe vérifier supérieur ou égale à opening date
		if (entity.getClosingDate() != null && entity.getOpeningDate().isAfter(entity.getClosingDate()))
			throw new IllegalArgumentException(OPENING_DATE_LESS_THAN_CLOSING_DATE_MESSAGE);
	}

	@Override
	@Transactional // readOnly = false
	public void deleteProvider(@NotNull UUID uuid) {
		ProviderEntity entity = providerDao.findByUUID(uuid);
		if (entity == null) {
			throw new IllegalArgumentException(PROVIDER_UNKNOWN_MESSAGE + uuid);
		}
		providerDao.delete(entity);
	}

	@Override
	@Transactional // readOnly = false
	public NodeProvider createNode(@NotNull UUID providerUuid, @NotNull NodeProvider nodeProvider) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		if (providerEntity == null) {
			throw new IllegalArgumentException(PROVIDER_UNKNOWN_MESSAGE + providerUuid);
		}
		NodeProviderEntity nodeProviderEntity = nodeProviderMapper.dtoToEntity(nodeProvider);
		nodeProviderEntity.setUuid(UUID.randomUUID());
		providerEntity.getNodeProviders().add(nodeProviderEntity);
		providerDao.save(providerEntity);

		return nodeProviderMapper.entityToDto(nodeProviderEntity);
	}

	@Override
	@Transactional // readOnly = false
	public void deleteNode(@NotNull UUID providerUuid, @NotNull UUID nodeUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		providerEntity.removeNodeProvider(nodeUuid);
		providerDao.save(providerEntity);
	}

	@Override
	public NodeProvider getNode(@NotNull UUID providerUuid, @NotNull UUID nodeUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		return nodeProviderMapper.entityToDto(providerEntity.lookupNodeProvider(nodeUuid));
	}

	@Override
	public List<NodeProvider> getNodes(@NotNull UUID providerUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		return nodeProviderMapper.entitiesToDto(providerEntity.getNodeProviders());
	}

	@Override
	@Transactional // readOnly = false
	public NodeProvider updateNode(@NotNull UUID providerUuid, @NotNull @Valid NodeProvider nodeProvider) {
		if (nodeProvider.getUuid() == null) {
			throw new IllegalArgumentException(UUID_NODE_PROVIDER_MISSING_MESSAGE);
		}
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		NodeProviderEntity nodeProviderEntity = providerEntity.lookupNodeProvider(nodeProvider.getUuid());
		if (nodeProviderEntity == null) {
			throw new IllegalArgumentException(NODE_PROVIDER_UNKNOWN_MESSAGE + nodeProvider.getUuid());
		}
		nodeProviderMapper.dtoToEntity(nodeProvider, nodeProviderEntity);
		nodeProviderDao.save(nodeProviderEntity);

		return nodeProviderMapper.entityToDto(nodeProviderEntity);
	}

	@Override
	@Transactional // readOnly = false
	public AbstractAddress createAddress(@NotNull UUID providerUuid, @NotNull AbstractAddress abstractAddress) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		if (providerEntity == null) {
			throw new IllegalArgumentException(PROVIDER_UNKNOWN_MESSAGE + providerUuid);
		}
		AbstractAddressEntity abstractAddressEntity = abstractAddressMapper.dtoToEntity(abstractAddress);
		assignAddressRole(abstractAddress, abstractAddressEntity);
		abstractAddressEntity.setUuid(UUID.randomUUID());
		providerEntity.getAddresses().add(abstractAddressEntity);
		providerDao.save(providerEntity);
		return abstractAddressMapper.entityToDto(abstractAddressEntity);
	}

	@Override
	@Transactional // readOnly = false
	public AbstractAddress updateAddress(@NotNull UUID providerUuid, @NotNull @Valid AbstractAddress abstractAddress) {
		if (abstractAddress.getUuid() == null) {
			throw new IllegalArgumentException(UUID_ADRESSE_MISSING_MESSAGE);
		}
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		AbstractAddressEntity abstractAddressEntity = providerEntity.lookupAddress(abstractAddress.getUuid());
		if (abstractAddressEntity == null) {
			throw new IllegalArgumentException(ADDRESS_UNKNOWN_MESSAGE + abstractAddress.getUuid());
		}
		abstractAddressMapper.dtoToEntity(abstractAddress, abstractAddressEntity);
		assignAddressRole(abstractAddress, abstractAddressEntity);
		abstractAddressDao.save(abstractAddressEntity);
		return abstractAddressMapper.entityToDto(abstractAddressEntity);
	}

	private void assignAddressRole(AbstractAddress abstractAddress,
			AbstractAddressEntity abstractAddressEntity) {
		AddressRoleEntity addressRoleEntity = null;
		if (abstractAddress.getAddressRole() != null && abstractAddress.getAddressRole().getUuid() != null) {
			addressRoleEntity = addressRoleDao.findByUUID(abstractAddress.getAddressRole().getUuid());

			if (addressRoleEntity == null) {
				throw new IllegalArgumentException(ADDRESS_ROLE_MISSING_MESSAGE);
			}
		}

		abstractAddressEntity.setAddressRole(addressRoleEntity);

		if (addressRoleEntity != null && addressRoleEntity.getType() != abstractAddressEntity.getType()) {
			throw new IllegalArgumentException(ADDRESS_ROLE_INVALID_MESSAGE);
		}
	}

	@Override
	@Transactional // readOnly = false
	public void deleteAddress(@NotNull UUID providerUuid, @NotNull UUID addressUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		providerEntity.removeAddress(addressUuid);
		providerDao.save(providerEntity);
	}

	@Override
	public AbstractAddress getAddress(@NotNull UUID providerUuid, @NotNull UUID addressUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		return abstractAddressMapper.entityToDto(providerEntity.lookupAddress(addressUuid));
	}

	@Override
	public List<AbstractAddress> getAddresses(@NotNull UUID providerUuid) {
		ProviderEntity providerEntity = providerDao.findByUUID(providerUuid);
		return abstractAddressMapper.entitiesToDto(providerEntity.getAddresses());
	}

	@Override
	public DocumentContent downloadMedia(@NotNull UUID providerUuid, @NotNull KindOfData kindOfData) throws AppServiceException {
		try {
			return mediaService.getMediaFor(MediaOrigin.PROVIDER, providerUuid, KindOfData.LOGO);
		} catch (DataverseAPIException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors du téléchargement du %s du fournisseur avec providerId = %s",
							kindOfData.getValue(),
							providerUuid),
					e);
		}
	}

	@Override
	public void uploadMedia(@NotNull UUID providerUuid, @NotNull KindOfData kindOfData, @NotNull Resource media) throws AppServiceException {
		try {
			getProvider(providerUuid, false);
		}
		catch (final EmptyResultDataAccessException e){
			throw new AppServiceNotFoundException(Provider.class, providerUuid);
		}

		try {
			final File tempFile = resourceHelper.copyResourceToTempFile(media);
			mediaService.setMediaFor(MediaOrigin.PROVIDER, providerUuid, KindOfData.LOGO, tempFile);
		} catch (final DataverseAPIException | IOException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors de l'upload du %s du fournisseur d'id %s",
							kindOfData.getValue(),
							providerUuid)
					, e);
		}
	}

	@Override
	public void deleteMedia(UUID providerUuid, KindOfData kindOfData) throws AppServiceException {
		try {
			mediaService.deleteMediaFor(MediaOrigin.PROVIDER, providerUuid, kindOfData);
		} catch (final DataverseAPIException e) {
			throw new AppServiceException(
					String.format(
							"Erreur lors de la suppression du %s du producteur d'id %s",
							kindOfData.getValue(),
							providerUuid)
					, e);
		}
	}

	@Override
	@Transactional // readOnly = false
	public NodeProvider patchNode(UUID providerUuid, UUID nodeUuid, LocalDateTime lastHarvestingDate) {
		final NodeProvider node = getNode(providerUuid, nodeUuid);
		node.setLastHarvestingDate(lastHarvestingDate);
		return updateNode(providerUuid, node);
	}
}
