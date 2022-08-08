package org.rudi.microservice.projekt.storage.dao.project;

import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao pour
 * 
 * @author FNI18300
 *
 */
@Repository
public interface ProjectDao extends AssetDescriptionDao<ProjectEntity> {

}
