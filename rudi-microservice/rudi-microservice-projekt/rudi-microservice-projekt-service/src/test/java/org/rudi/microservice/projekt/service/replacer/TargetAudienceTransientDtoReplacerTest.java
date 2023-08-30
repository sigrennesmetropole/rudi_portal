package org.rudi.microservice.projekt.service.replacer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.service.targetaudience.TargetAudienceService;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TargetAudienceTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final TargetAudienceService targetAudienceService;

	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final @Valid List<TargetAudience> targetAudiences = project.getTargetAudiences();
		if (CollectionUtils.isNotEmpty(targetAudiences)) {
			final List<TargetAudience> savedTargetAudiences = new ArrayList<>(targetAudiences.size());
			for (final TargetAudience TargetAudience : targetAudiences) {
				savedTargetAudiences.add(targetAudienceService.createTargetAudience(TargetAudience));
			}
			project.setTargetAudiences(savedTargetAudiences);
		}
	}

}
