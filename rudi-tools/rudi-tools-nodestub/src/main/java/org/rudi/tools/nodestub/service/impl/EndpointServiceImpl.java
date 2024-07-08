package org.rudi.tools.nodestub.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.service.EndpointService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EndpointServiceImpl implements EndpointService {
	private final NodeStubConfiguration nodeStubConfiguration;

	@Override
	public DocumentContent callEndpoint(UUID mediaUuid) throws AppServiceException {
		File resourcesDirectory = nodeStubConfiguration.getEndpointsDirectory();
		final File[] files = resourcesDirectory.listFiles();
		if (files != null) {
			File data = Arrays.stream(files).filter(file -> file.getName().startsWith(mediaUuid.toString())).findFirst()
					.orElse(null);
			if (data != null) {
				return new DocumentContent(data.getName(), "application/binary", data);
			}
		}
		throw new AppServiceBadRequestException("Invalid uuid");
	}

}
