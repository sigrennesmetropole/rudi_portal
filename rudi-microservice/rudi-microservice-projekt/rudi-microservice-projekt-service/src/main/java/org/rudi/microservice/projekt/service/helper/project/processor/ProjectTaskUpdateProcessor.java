package org.rudi.microservice.projekt.service.helper.project.processor;

import java.lang.reflect.InvocationTargetException;

import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;

public interface ProjectTaskUpdateProcessor {
	void process(Project project, ProjectEntity projectEntity) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
