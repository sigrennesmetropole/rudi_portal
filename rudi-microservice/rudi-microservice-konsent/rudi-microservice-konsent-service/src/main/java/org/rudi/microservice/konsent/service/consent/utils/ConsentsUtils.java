package org.rudi.microservice.konsent.service.consent.utils;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.RetentionEntity;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class ConsentsUtils {

	private static final int DAY_PER_WEEK = 7;
	private static final int DAY_PER_MONTH = 30;
	private static final int DAY_PER_YEAR = 365;

	public static int convertRetentionPeriodToDays(RetentionEntity retention) {
		val value = retention.getValue();
		val unit = retention.getUnit();
		int daysToAdd;
		switch (unit) {
			case WEEK:
				daysToAdd = value * DAY_PER_WEEK;
				break;
			case MONTH:
				daysToAdd = value * DAY_PER_MONTH;
				break;
			default:
				daysToAdd = value * DAY_PER_YEAR;
				break;
		}
		return daysToAdd;
	}
}
