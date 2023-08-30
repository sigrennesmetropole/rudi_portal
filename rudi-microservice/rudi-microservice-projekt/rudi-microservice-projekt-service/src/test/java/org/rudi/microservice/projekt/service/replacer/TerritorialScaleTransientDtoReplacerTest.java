package org.rudi.microservice.projekt.service.replacer;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.service.territory.TerritorialScaleService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TerritorialScaleTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final TerritorialScaleService territorialScaleService;

	@Override
	public void replaceDtoFor(Project project) throws AppServiceException {
		final TerritorialScale territorialScale = project.getTerritorialScale();
		if (territorialScale != null) {
			project.setTerritorialScale(territorialScaleService.createTerritorialScale(territorialScale));
		}
	}
}
