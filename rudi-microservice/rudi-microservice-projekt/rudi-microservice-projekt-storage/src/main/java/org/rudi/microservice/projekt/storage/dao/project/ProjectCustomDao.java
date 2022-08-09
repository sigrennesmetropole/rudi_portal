package org.rudi.microservice.projekt.storage.dao.project;

import org.rudi.microservice.projekt.core.bean.ComputeIndicatorsSearchCriteria;
import org.rudi.microservice.projekt.core.bean.Indicators;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectCustomDao {

	Page<ProjectEntity> searchProjects(ProjectSearchCriteria searchCriteria, Pageable pageable);

	ProjectEntity findProjectByNewDatasetRequestUuid(UUID newDatasetRequestUuid);

	ProjectEntity findProjectByLinkedDatasetUuid(UUID linkedDatasetUuid);

	Indicators computeProjectInfos(ComputeIndicatorsSearchCriteria searchCriteria);

	Integer getNumberOfLinkedDatasets(UUID projectUuid);

	Integer getNumberOfNewRequests(UUID projectUuid);
}
