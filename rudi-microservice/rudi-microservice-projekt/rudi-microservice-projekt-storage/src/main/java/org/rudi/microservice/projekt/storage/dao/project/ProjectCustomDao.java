package org.rudi.microservice.projekt.storage.dao.project;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.projekt.core.bean.ComputeIndicatorsSearchCriteria;
import org.rudi.microservice.projekt.core.bean.Indicators;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectCustomDao {

	Page<ProjectEntity> searchProjects(ProjectSearchCriteria searchCriteria, Pageable pageable);

	ProjectEntity findProjectByNewDatasetRequestUuid(UUID newDatasetRequestUuid);

	ProjectEntity findProjectByLinkedDatasetUuid(UUID linkedDatasetUuid);

	Indicators computeProjectInfos(ComputeIndicatorsSearchCriteria searchCriteria);

	Integer getNumberOfLinkedDatasets(UUID projectUuid);

	Integer getNumberOfNewRequests(UUID projectUuid);

	List<ProjectByOwner> getNumberOfProjectsPerOwners(List<UUID> owners);
}
