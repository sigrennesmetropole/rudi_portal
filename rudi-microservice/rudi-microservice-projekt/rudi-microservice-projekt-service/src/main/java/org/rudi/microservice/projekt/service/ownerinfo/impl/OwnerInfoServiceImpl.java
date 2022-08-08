package org.rudi.microservice.projekt.service.ownerinfo.impl;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.rudi.microservice.projekt.service.ownerinfo.OwnerInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerInfoServiceImpl implements OwnerInfoService {

	private final List<OwnerInfoHelper> ownerInfoHelpers;
	private final EnumMap<OwnerType, OwnerInfoHelper> ownerInfoHelpersByOwnerType = new EnumMap<>(OwnerType.class);

	@Override
	public OwnerInfo getOwnerInfo(OwnerType ownerType, UUID ownerUuid) throws AppServiceException {
		OwnerInfoHelper helper = findHelperFor(ownerType);
		return helper.getOwnerInfo(ownerUuid);
	}

	@Nonnull
	private OwnerInfoHelper findHelperFor(OwnerType ownerType) {
		final var ownerInfoHelper = ownerInfoHelpersByOwnerType.computeIfAbsent(ownerType, k -> ownerInfoHelpers.stream()
				.filter(helper -> helper.isHelperFor(ownerType))
				.findFirst()
				.orElse(null));
		if (ownerInfoHelper == null) {
			throw new OwnerInfoHelperNotImplementedException(ownerType);
		}
		return ownerInfoHelper;
	}
}
