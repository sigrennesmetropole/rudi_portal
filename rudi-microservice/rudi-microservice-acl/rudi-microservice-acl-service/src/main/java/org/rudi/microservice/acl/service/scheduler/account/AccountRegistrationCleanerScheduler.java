package org.rudi.microservice.acl.service.scheduler.account;

import org.rudi.microservice.acl.service.account.AccountService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountRegistrationCleanerScheduler {

	/**
	 * Default delay for scheduler
	 */
	public static final long DEFAULT_DELAY = 60000L;

	private final AccountService service;

	@Scheduled(fixedDelayString = "${rudi.acl.scheduler.accountRegistrationCleaner.delay:" + DEFAULT_DELAY + "}")
	public void scheduleAccountRegistrationCleaner() {
		log.info("Start {}...", getClass().getSimpleName());
		service.cleanExpiredAccounts();
		log.info("Start {} done.", getClass().getSimpleName());
	}

	@Scheduled(fixedDelayString = "${rudi.acl.scheduler.expiredResetPasswordRequestCleaner.delay:" + DEFAULT_DELAY + "}")
	public void scheduleExpiredUpdatePasswordCleaner() {
		log.info("Start cleaning expired token {}...", getClass().getSimpleName());
		service.deleteAllExpiredToken();
		log.info("Expired token cleaned {} done.", getClass().getSimpleName());
	}

}
