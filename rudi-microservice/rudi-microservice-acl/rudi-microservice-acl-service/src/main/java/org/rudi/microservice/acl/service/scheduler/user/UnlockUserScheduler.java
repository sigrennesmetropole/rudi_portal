/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.scheduler.user;

import org.rudi.microservice.acl.service.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UnlockUserScheduler {

	/**
	 * Default delay for scheduler
	 */
	public static final long DEFAULT_DELAY = 60000L;

	private final UserService service;

	@Scheduled(fixedDelayString = "${rudi.acl.scheduler.lockedAccount.delay:" + DEFAULT_DELAY + "}")
	public void scheduleAccountRegistrationCleaner() {
		log.info("Start {}...", getClass().getSimpleName());
		service.unlockUsers();
		log.info("Start {} done.", getClass().getSimpleName());
	}

}
