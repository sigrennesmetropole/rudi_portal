package org.rudi.microservice.projekt.service.replacer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.Support;
import org.rudi.microservice.projekt.service.support.SupportService;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SupportTransientDtoReplacer implements TransientDtoReplacer {
	private final SupportService supportService;

	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final @Valid List<Support> supports = project.getDesiredSupports();
		if (CollectionUtils.isNotEmpty(supports)) {
			final List<Support> savedSupports = new ArrayList<>(supports.size());
			for (final Support support : supports) {
				savedSupports.add(supportService.createSupport(support));
			}
			project.setDesiredSupports(savedSupports);
		}
	}

}

