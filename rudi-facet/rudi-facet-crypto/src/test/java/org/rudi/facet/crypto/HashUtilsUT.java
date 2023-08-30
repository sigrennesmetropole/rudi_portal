/**
 * RUDI Portail
 */
package org.rudi.facet.crypto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author FNI18300
 *
 */
class HashUtilsUT {

	@Test
	void sha3String() {
		try {
			String sha3256 = HashUtils
					.sha3("Pac-Man Inky bashfull orange dots blue enemies ghosts Toru Iwatani Puck Man power up. ");
			assertNotNull(sha3256);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void sha3Object() {

		HashBeanTest object = HashBeanTest.builder().field1("toto").field2(true).field3(LocalDateTime.now())
				.field4(List.of(1L, 12L, 24L)).build();

		try {
			String sha3256 = HashUtils.sha3(object);
			assertNotNull(sha3256);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
