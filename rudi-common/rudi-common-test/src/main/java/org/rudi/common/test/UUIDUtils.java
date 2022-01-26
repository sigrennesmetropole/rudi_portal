package org.rudi.common.test;

import lombok.val;

import javax.annotation.Nonnull;
import java.util.UUID;

public class UUIDUtils {

	private UUIDUtils() {
	}

	/**
	 * Example with <code>segmentIndexToKeep</code> = 1 :
	 *
	 * <p>Input :</p>
	 * <pre>
	 * "5596b5b2-b227-4c74-a9a1-719e7c1008c7"
	 * </pre>
	 *
	 * <p>Output : </p>
	 * <pre>
	 * "00000000-b227-0000-0000-000000000000"
	 * </pre>
	 *
	 * @param segmentIndexToKeep segment index to keep, 0-based
	 * @param uuid               input UUID
	 * @return the UUID with all digits replaced by "0" but not in the <i>segmentIndexToErase</i> dash-delimited segment
	 * @see #eraseOnlyUUIDSegment(int, UUID) : opposite operation
	 */
	public static UUID keepOnlyUUIDSegment(int segmentIndexToKeep, UUID uuid) {
		return keepOrEraseOnlyUUIDSegment(segmentIndexToKeep, uuid, true);
	}

	@Nonnull
	private static UUID keepOrEraseOnlyUUIDSegment(int segmentIndexToKeep, UUID uuid, boolean keep) {
		val uuidString = uuid.toString();
		val sb = new StringBuilder(uuidString.length());
		var segmentIndex = 0;
		for (final char car : uuidString.toCharArray()) {
			final boolean isDash = car == '-';
			val keepCar = isDash || (keep == (segmentIndex == segmentIndexToKeep));
			if (keepCar) {
				sb.append(car);
			} else {
				sb.append('0');
			}
			if (isDash) {
				++segmentIndex;
			}
		}
		return UUID.fromString(sb.toString());
	}

	/**
	 * Example with <code>segmentIndexToErase</code> = 1 :
	 *
	 * <p>Input :</p>
	 * <pre>
	 * "5596b5b2-b227-4c74-a9a1-719e7c1008c7"
	 * </pre>
	 *
	 * <p>Output : </p>
	 * <pre>
	 * "5596b5b2-0000-4c74-a9a1-719e7c1008c7"
	 * </pre>
	 *
	 * @param segmentIndexToErase segment index to erase, 0-based
	 * @param uuid                input UUID
	 * @return the UUID with digits replaced by "0" only in the <i>segmentIndexToErase</i> dash-delimited segment
	 * @see #keepOnlyUUIDSegment(int, UUID) : opposite operation
	 */
	public static UUID eraseOnlyUUIDSegment(int segmentIndexToErase, UUID uuid) {
		return keepOrEraseOnlyUUIDSegment(segmentIndexToErase, uuid, false);
	}
}
