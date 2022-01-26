package org.rudi.tools.nodestub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class ResourcesServiceImpl implements ResourcesService {
	private final NodeStubConfiguration nodeStubConfiguration;
	private final ObjectMapper mapper;

	@Override
	public List<Metadata> getMetadataList(int limit, int offset, LocalDateTime updateAfter) {
		final File resourcesDirectory = nodeStubConfiguration.getResourcesDirectory();
		final File[] files = resourcesDirectory.listFiles();
		if (files != null) {
			final AtomicInteger atomicCount = new AtomicInteger(0);
			return Arrays.stream(files)
					.map(this::getMetadata)
					.filter(Objects::nonNull)
					.filter(metadata -> updateAfter == null || wasUpdatedAfter(metadata, updateAfter))
					.filter(metadata -> isBetweenOffsetAndLimit(atomicCount, offset, limit))
					.collect(Collectors.toList());
		} else {
			log.warn("Resources directory {} does not exist or is not a directory.", resourcesDirectory);
			return Collections.emptyList();
		}
	}


	@Nullable
	@SuppressWarnings("squid:S2583") // si on tente de lire un fichier rpt, on objet metadata global_id null est quand même lu
	private Metadata getMetadata(File file) {
		try {
			final Metadata metadata = mapper.readValue(file, Metadata.class);
			if (metadata.getGlobalId() == null) {
				log.error("skip file without global_id : {}", file);
				return null;
			}
			return metadata;
		} catch (Exception e) {
			log.error("skip file : {}", file, e);
			return null;
		}
	}

	private boolean wasUpdatedAfter(Metadata metadata, @NotNull LocalDateTime updateAfter) {
		return metadata.getDatasetDates().getUpdated().isAfter(updateAfter);
	}

	private boolean isBetweenOffsetAndLimit(AtomicInteger atomicCount, int offset, int limit) {
		final int count = atomicCount.get();
		if (count >= offset && count < offset + limit) {
			atomicCount.incrementAndGet();
			return true;
		} else {
			return false;
		}
	}

}
