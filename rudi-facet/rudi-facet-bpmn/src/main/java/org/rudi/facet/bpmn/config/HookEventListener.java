/**
 * 
 */
package org.rudi.facet.bpmn.config;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FNI18300
 *
 */
public class HookEventListener implements ActivitiEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(HookEventListener.class);

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {

		case ENGINE_CLOSED:
			LOGGER.info("Activiti - Engine closed.");
			break;

		case ENGINE_CREATED:
			LOGGER.info("Activiti - Engine created...");
			break;

		case JOB_EXECUTION_SUCCESS:
			LOGGER.info("Activiti - A job well done! {}", event);
			break;

		case JOB_EXECUTION_FAILURE:
			LOGGER.info("Activiti - A job has failed... {}", event);
			break;

		case ENTITY_CREATED:
			ActivitiEntityEvent ec = (ActivitiEntityEvent) event;
			Object o1 = ec.getEntity();
			LOGGER.info("Activiti - Entity created: {}/{}=>{}", event.getType(), o1, event);
			break;

		case ENTITY_INITIALIZED:
			ActivitiEntityEvent ei = (ActivitiEntityEvent) event;
			Object o2 = ei.getEntity();
			LOGGER.info("Activiti - Entity initialized: {}/{}=>{}", event.getType(), o2, event);
			break;

		case TASK_ASSIGNED:
			ActivitiEntityEvent ea = (ActivitiEntityEvent) event;
			Object o3 = ea.getEntity();
			LOGGER.info("Activiti - Entity initialized: {}/{}=>{}", event.getType(), o3, event);
			break;

		default:
			LOGGER.info("Activiti - Event received: {}=>{}", event.getType(), event);
		}
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

}
