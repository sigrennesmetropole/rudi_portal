package org.rudi.microservice.konsult.service.scheduler.sitemap;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsult.service.sitemap.SitemapService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PFO23835
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SitemapGenerationScheduler {

	/**
	 * A cron-like expression, extending the usual UN*X definition to include triggerson the second, minute, hour, day of month, month, and day of week.
	 */
	private static final String DEFAULT_CRON = "0 0 1,13 * * *";

	private final SitemapService sitemapService;

	@Scheduled(cron = "${rudi.konsult.scheduler.sitemap-generation.cron:" + DEFAULT_CRON + "}")
	public void generateSitemap() {
		log.info("Start generating sitemap {}...", getClass().getSimpleName());
		try {
			sitemapService.generateSitemap();
			log.info("Sitemap generation {} done.", getClass().getSimpleName());
		} catch (AppServiceException e) {
			log.error("Sitemap generation {} failed.", getClass().getSimpleName(), e);
		}
	}

}
